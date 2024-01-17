package com.blog.som.domain.member.service;

import com.blog.som.domain.member.dto.EmailAuthResult;
import com.blog.som.domain.member.dto.MemberRegister.Request;
import com.blog.som.domain.member.dto.MemberRegister.Response;
import com.blog.som.domain.member.entity.MemberEntity;
import com.blog.som.domain.member.repository.MemberRepository;
import com.blog.som.domain.member.type.Role;
import com.blog.som.global.components.password.PasswordUtils;
import com.blog.som.global.constant.ResponseConstant;
import com.blog.som.global.exception.ErrorCode;
import com.blog.som.global.exception.custom.MemberException;
import com.blog.som.global.components.mail.MailComponent;
import com.blog.som.global.components.mail.SendMailDto;
import com.blog.som.global.redis.email.EmailAuthRepository;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class MemberServiceImpl implements MemberService {

  private final MemberRepository memberRepository;
  private final MailComponent mailComponent;
  private final EmailAuthRepository emailAuthRepository;

  @Override
  public Response registerMember(Request request) {

    if(memberRepository.existsByEmail(request.getEmail())){
      throw new MemberException(ErrorCode.MEMBER_ALREADY_EXISTS);
    }
    //회원 저장
    MemberEntity savedMember = memberRepository.save(
        Request.toEntity(request, PasswordUtils.encPassword(request.getPassword())));

    //메일 전송
    String uuid = UUID.randomUUID().toString();
    mailComponent.sendMailForRegister(
        new SendMailDto(savedMember.getEmail(), savedMember.getNickname(), uuid));

    //Redis 에 저장 (timeout = 10분)
    emailAuthRepository.saveEmailAuthUuid(uuid, savedMember.getMemberId());

    return Response.fromEntity(savedMember);
  }

  @Override
  public EmailAuthResult emailAuth(String key) {
    //key -> memberId
    Long memberId = emailAuthRepository.getEmailAuthMemberId(key);

    MemberEntity member = memberRepository.findById(memberId)
        .orElseThrow(() -> new MemberException(ErrorCode.EMAIL_AUTH_WRONG_KEY));

    //Role!=UNAUTH -> 이미 인증 완료된 유저
    if(!Role.UNAUTH.equals(member.getRole())){
      log.info("[email auth fail - already completed] memberId={}", member.getMemberId());
      return new EmailAuthResult(false, member);
    }
    //이메일 인증 성공
    log.info("[email auth complete] memberId={}", member.getMemberId());
    member.setRole(Role.USER);
    member.setEmailAuthAt(LocalDateTime.now());
    memberRepository.save(member);

    return new EmailAuthResult(true, member);
  }


}
