package com.blog.som.domain.member.security.service;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import com.blog.som.EntityCreator;
import com.blog.som.domain.member.dto.MemberDto;
import com.blog.som.domain.member.dto.MemberLogin;
import com.blog.som.domain.member.dto.MemberLogin.Request;
import com.blog.som.domain.member.dto.MemberLogin.Response;
import com.blog.som.domain.member.dto.MemberLogoutResponse;
import com.blog.som.domain.member.dto.TokenResponse;
import com.blog.som.domain.member.entity.MemberEntity;
import com.blog.som.domain.member.repository.MemberRepository;
import com.blog.som.domain.member.security.token.JwtTokenService;
import com.blog.som.domain.member.type.Role;
import com.blog.som.global.components.mail.MailSender;
import com.blog.som.global.components.password.PasswordUtils;
import com.blog.som.global.exception.ErrorCode;
import com.blog.som.global.exception.custom.MemberException;
import com.blog.som.global.redis.token.TokenRepository;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@Slf4j
@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

  @Mock
  private MemberRepository memberRepository;
  @Mock
  private MailSender mailSender;
  @Mock
  private TokenRepository tokenRepository;
  @Mock
  private JwtTokenService jwtTokenService;

  @InjectMocks
  private AuthService authService;


  @Nested
  @DisplayName("로그인")
  class LoginMember {

    @Test
    @DisplayName("성공")
    void loginMember() {
      MemberEntity member = EntityCreator.createMember(1L);
      String plainPassword = member.getPassword();
      String encPassword = PasswordUtils.encPassword(member.getPassword());
      member.setPassword(encPassword);

      String accessToken = "test.accessToken";
      String refreshToken = "test.refreshToken";

      Request loginInput = Request.builder()
          .email(member.getEmail())
          .password(plainPassword)
          .build();
      //given
      when(memberRepository.findByEmail(member.getEmail()))
          .thenReturn(Optional.of(member));
      when(jwtTokenService.generateTokenResponse(member.getEmail(), Role.USER))
          .thenReturn(new TokenResponse(accessToken, refreshToken));
      //when
      Response response = authService.loginMember(loginInput);

      //then
      assertThat(response.getTokenResponse().getAccessToken()).isEqualTo(accessToken);
      assertThat(response.getTokenResponse().getRefreshToken()).isEqualTo(refreshToken);

      assertThat(member.getMemberId()).isEqualTo(response.getMember().getMemberId());
      assertThat(member.getEmail()).isEqualTo(response.getMember().getEmail());
      assertThat(response.getMember().getRole()).isEqualTo(Role.USER);
    }

    @Test
    @DisplayName("실패 : LOGIN_FAILED_MEMBER_NOT_FOUND")
    void loginMember_LOGIN_FAILED_MEMBER_NOT_FOUND() {
      MemberEntity member = EntityCreator.createMember(1L);
      Request loginInput = Request.builder()
          .email(member.getEmail())
          .password(member.getPassword())
          .build();
      //given
      when(memberRepository.findByEmail(member.getEmail()))
          .thenReturn(Optional.empty());

      //when
      //then
      MemberException memberException = assertThrows(MemberException.class, () -> authService.loginMember(loginInput));
      assertThat(memberException.getErrorCode()).isEqualTo(ErrorCode.LOGIN_FAILED_MEMBER_NOT_FOUND);
    }

    @Test
    @DisplayName("실패 : LOGIN_FAILED_PASSWORD_INCORRECT")
    void loginMember_LOGIN_FAILED_PASSWORD_INCORRECT() {
      MemberEntity member = EntityCreator.createMember(1L);
      String plainPassword = member.getPassword();
      String encPassword = PasswordUtils.encPassword(member.getPassword()+"!!!");
      member.setPassword(encPassword);

      Request loginInput = Request.builder()
          .email(member.getEmail())
          .password(plainPassword)
          .build();
      //given
      when(memberRepository.findByEmail(member.getEmail()))
          .thenReturn(Optional.of(member));

      //when
      //then
      MemberException memberException = assertThrows(MemberException.class, () -> authService.loginMember(loginInput));
      assertThat(memberException.getErrorCode()).isEqualTo(ErrorCode.LOGIN_FAILED_PASSWORD_INCORRECT);
    }

    @Test
    @DisplayName("실패 : EMAIL_AUTH_REQUIRED")
    void loginMember_EMAIL_AUTH_REQUIRED() {
      MemberEntity member = EntityCreator.createMember(1L);
      String plainPassword = member.getPassword();
      String encPassword = PasswordUtils.encPassword(member.getPassword());
      member.setPassword(encPassword);
      member.setRole(Role.UNAUTH);

      Request loginInput = Request.builder()
          .email(member.getEmail())
          .password(plainPassword)
          .build();
      //given
      when(memberRepository.findByEmail(member.getEmail()))
          .thenReturn(Optional.of(member));

      //when
      //then
      MemberException memberException = assertThrows(MemberException.class, () -> authService.loginMember(loginInput));
      assertThat(memberException.getErrorCode()).isEqualTo(ErrorCode.EMAIL_AUTH_REQUIRED);
    }
  }


  @Test
  @DisplayName("로그아웃")
  void logoutMember(){
    String email = "test@test.com";
    String accessToken = "test.accessToken";
    //given
    when(tokenRepository.deleteRefreshToken(email))
        .thenReturn(true);

    //when
    MemberLogoutResponse response = authService.logoutMember(email, accessToken);

    //then
    verify(tokenRepository,times(1)).addBlacklistAccessToken(accessToken, email);
    assertThat(response.getEmail()).isEqualTo(email);
    assertThat(response.isLogoutResult()).isTrue();
  }

}