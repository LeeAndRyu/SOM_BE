package com.blog.som.domain.member.security.service;



import com.blog.som.domain.member.dto.MemberDto;
import com.blog.som.domain.member.dto.MemberLogin;
import com.blog.som.domain.member.entity.MemberEntity;
import com.blog.som.domain.member.repository.MemberRepository;
import com.blog.som.domain.member.security.userdetails.LoginMember;
import com.blog.som.domain.member.type.Role;
import com.blog.som.global.components.mail.MailSender;
import com.blog.som.global.components.mail.SendMailDto;
import com.blog.som.global.components.password.PasswordUtils;
import com.blog.som.global.exception.ErrorCode;
import com.blog.som.global.exception.custom.MemberException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class AuthService implements UserDetailsService {

  private final MemberRepository memberRepository;
  private final MailSender mailSender;

  public MemberDto loginMember(MemberLogin.Request input) {
    MemberEntity member = memberRepository.findByEmail(input.getEmail())
        .orElseThrow(() -> new MemberException(ErrorCode.LOGIN_FAILED_USER_NOT_FOUND));

    //비밀번호 확인
    if (!PasswordUtils.equalsPlainTextAndHashed(input.getPassword(), member.getPassword())) {
      throw new MemberException(ErrorCode.LOGIN_FAILED_PASSWORD_INCORRECT);
    }

    //UNAUTH 상태일 시 예외 발생
    if (Role.UNAUTH.equals(member.getRole())) {
      mailSender.sendMailForRegister(new SendMailDto(member));
      throw new MemberException(ErrorCode.EMAIL_AUTH_REQUIRED);
    }

    return MemberDto.fromEntity(member);
  }


  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    MemberEntity member = memberRepository.findByEmail(username)
        .orElseThrow(() -> new UsernameNotFoundException("회원 정보가 존재하지 않습니다."));
    log.info("인증 성공[ ID : {} ]", member.getEmail());

    return new LoginMember(member);
  }
}
