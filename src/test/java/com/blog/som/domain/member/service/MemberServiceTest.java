package com.blog.som.domain.member.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.blog.som.EntityCreator;
import com.blog.som.domain.member.dto.EmailAuthResult;
import com.blog.som.domain.member.dto.MemberRegister;
import com.blog.som.domain.member.dto.MemberRegister.Request;
import com.blog.som.domain.member.dto.MemberRegister.Response;
import com.blog.som.domain.member.entity.MemberEntity;
import com.blog.som.domain.member.repository.MemberRepository;
import com.blog.som.domain.member.type.Role;
import com.blog.som.global.components.mail.MailSender;
import com.blog.som.global.components.password.PasswordUtils;
import com.blog.som.global.constant.ResponseConstant;
import com.blog.som.global.exception.ErrorCode;
import com.blog.som.global.exception.custom.MemberException;
import com.blog.som.global.redis.email.EmailAuthRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@Slf4j
@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

  @Mock
  private MemberRepository memberRepository;
  @Mock
  private EmailAuthRepository emailAuthRepository;
  @Mock
  private MailSender mailSender;


  @InjectMocks
  private MemberServiceImpl memberService;


  @Nested
  @DisplayName("회원 가입")
  class RegisterMember {

    private MemberRegister.Request createRequest(MemberEntity member) {
      return MemberRegister.Request.builder()
          .email(member.getEmail())
          .password(member.getPassword())
          .nickname(member.getNickname())
          .phoneNumber(member.getPhoneNumber())
          .birthDate(member.getBirthDate())
          .build();
    }

    @Test
    @DisplayName("성공")
    void registerMember() {
      //given
      MemberEntity member = EntityCreator.createMember(1L);
      Request request = this.createRequest(member);

      when(memberRepository.existsByEmail(request.getEmail()))
          .thenReturn(false);
      when(memberRepository.save(Request.toEntity(request, PasswordUtils.encPassword(request.getPassword()))))
          .thenReturn(member);

      //when
      Response response = memberService.registerMember(request);

      //then
      assertThat(response.getMemberId()).isEqualTo(member.getMemberId());
      assertThat(response.getEmail()).isEqualTo(member.getEmail());
      assertThat(response.getNickname()).isEqualTo(member.getNickname());
      assertThat(response.getMessage()).isEqualTo(ResponseConstant.MEMBER_REGISTER_COMPLETE);
    }

    @Test
    @DisplayName("MEMBER_ALREADY_EXISTS")
    void registerMember_MEMBER_ALREADY_EXISTS() {
      //given
      MemberEntity member = EntityCreator.createMember(1L);
      Request request = this.createRequest(member);

      when(memberRepository.existsByEmail(request.getEmail()))
          .thenReturn(true);
      //when
      //then
      MemberException memberException =
          assertThrows(MemberException.class, () -> memberService.registerMember(request));
      assertThat(memberException.getErrorCode()).isEqualTo(ErrorCode.MEMBER_ALREADY_EXISTS);
    }
  }

  @Nested
  @DisplayName("이메일 인증")
  class EmailAuth {

    private static final String testUUID = "test-random-uuid-123123";

    @Test
    @DisplayName("성공 - 이메일 인증 완료")
    void emailAuth() {
      MemberEntity member = EntityCreator.createMember(1L);
      member.setRole(Role.UNAUTH);

      //given
      when(emailAuthRepository.getEmailByUuid(testUUID))
          .thenReturn(member.getEmail());
      when(memberRepository.findByEmail(member.getEmail()))
          .thenReturn(Optional.of(member));

      //when
      EmailAuthResult emailAuthResult = memberService.emailAuth(testUUID);

      //then
      verify(memberRepository, times(1)).save(member);
      assertThat(emailAuthResult.isResult()).isTrue();
      assertThat(emailAuthResult.getMessage()).isEqualTo(ResponseConstant.EMAIL_AUTH_COMPLETE);
      assertThat(emailAuthResult.getMemberDto().getMemberId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("실패 - 이미 인증 완료된 유저")
    void emailAuth_EMAIL_AUTH_ALREADY_COMPLETED() {
      MemberEntity member = EntityCreator.createMember(1L);
      member.setRole(Role.USER);

      //given
      when(emailAuthRepository.getEmailByUuid(testUUID))
          .thenReturn(member.getEmail());
      when(memberRepository.findByEmail(member.getEmail()))
          .thenReturn(Optional.of(member));

      //when
      EmailAuthResult emailAuthResult = memberService.emailAuth(testUUID);

      //then
      verify(memberRepository, never()).save(member);
      assertThat(emailAuthResult.isResult()).isFalse();
      assertThat(emailAuthResult.getMessage()).isEqualTo(ResponseConstant.EMAIL_AUTH_ALREADY_COMPLETED);
      assertThat(emailAuthResult.getMemberDto().getMemberId()).isEqualTo(1L);
    }

  }

}