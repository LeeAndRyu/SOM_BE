package com.blog.som.domain.member.security.service;


import com.blog.som.domain.member.dto.MemberDto;
import com.blog.som.domain.member.dto.MemberLogin;
import com.blog.som.domain.member.dto.MemberLogoutResponse;
import com.blog.som.domain.member.entity.MemberEntity;
import com.blog.som.domain.member.repository.MemberRepository;
import com.blog.som.domain.member.type.Role;
import com.blog.som.global.components.mail.MailSender;
import com.blog.som.global.components.mail.SendMailDto;
import com.blog.som.global.components.password.PasswordUtils;
import com.blog.som.global.exception.ErrorCode;
import com.blog.som.global.exception.custom.MemberException;
import com.blog.som.global.redis.token.TokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class AuthService{

  private final MemberRepository memberRepository;
  private final MailSender mailSender;
  private final TokenRepository tokenRepository;

  public MemberDto loginMember(MemberLogin.Request loginInput) {
    MemberEntity member = memberRepository.findByEmail(loginInput.getEmail())
        .orElseThrow(() -> new MemberException(ErrorCode.LOGIN_FAILED_MEMBER_NOT_FOUND));

    //비밀번호 확인
    if (!PasswordUtils.equalsPlainTextAndHashed(loginInput.getPassword(), member.getPassword())) {
      throw new MemberException(ErrorCode.LOGIN_FAILED_PASSWORD_INCORRECT);
    }

    //UNAUTH 상태일 시 예외 발생
    if (Role.UNAUTH.equals(member.getRole())) {
      mailSender.sendMailForRegister(new SendMailDto(member));
      throw new MemberException(ErrorCode.EMAIL_AUTH_REQUIRED);
    }

    return MemberDto.fromEntity(member);
  }

  public MemberDto saveRefreshToken(String email, String refreshToken){
    MemberEntity member = memberRepository.findByEmail(email)
        .orElseThrow(() -> new MemberException(ErrorCode.MEMBER_NOT_FOUND));
    tokenRepository.saveRefreshToken(email, refreshToken);
    return MemberDto.fromEntity(member);
  }

  public MemberLogoutResponse logoutMember(String email, String accessToken){
    //accessToken blacklist 추가
    tokenRepository.addBlacklistAccessToken(accessToken, email);
    //refreshToken 삭제
    boolean result = tokenRepository.deleteRefreshToken(email);

    return new MemberLogoutResponse(email, result);
  }

  public boolean checkRefreshToken(String email, String refreshToken){
    return tokenRepository.checkRefreshToken(email, refreshToken);
  }

}
