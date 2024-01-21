package com.blog.som.domain.member.service;

import com.blog.som.domain.member.dto.EmailAuthResult;
import com.blog.som.domain.member.dto.MemberDto;
import com.blog.som.domain.member.dto.MemberEditRequest;
import com.blog.som.domain.member.dto.MemberPasswordEdit;
import com.blog.som.domain.member.dto.MemberRegister.Request;
import com.blog.som.domain.member.dto.MemberRegister.Response;
import com.blog.som.domain.member.entity.MemberEntity;
import com.blog.som.domain.member.repository.MemberRepository;
import com.blog.som.domain.member.type.Role;
import com.blog.som.global.components.mail.MailSender;
import com.blog.som.global.components.mail.SendMailDto;
import com.blog.som.global.components.password.PasswordUtils;
import com.blog.som.global.constant.ResponseConstant;
import com.blog.som.global.exception.ErrorCode;
import com.blog.som.global.exception.custom.MemberException;
import com.blog.som.global.redis.email.EmailAuthRepository;
import com.blog.som.global.s3.S3ImageService;
import java.time.LocalDateTime;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RequiredArgsConstructor
@Service
public class MemberServiceImpl implements MemberService {

  private final MemberRepository memberRepository;
  private final MailSender mailSender;
  private final EmailAuthRepository emailAuthRepository;
  private final S3ImageService s3ImageService;

  @Override
  public Response registerMember(Request request) {

    if (memberRepository.existsByEmail(request.getEmail())) {
      throw new MemberException(ErrorCode.MEMBER_ALREADY_EXISTS);
    }
    //회원 저장
    MemberEntity savedMember = memberRepository.save(
        Request.toEntity(request, PasswordUtils.encPassword(request.getPassword())));

    //메일 전송
    mailSender.sendMailForRegister(
        new SendMailDto(savedMember));


    return Response.fromEntity(savedMember);
  }

  @Override
  public EmailAuthResult emailAuth(String key) {
    //key -> memberId
    String email = emailAuthRepository.getEmailByUuid(key);

    MemberEntity member = memberRepository.findByEmail(email)
        .orElseThrow(() -> new MemberException(ErrorCode.EMAIL_AUTH_WRONG_KEY));

    //Role!=UNAUTH -> 이미 인증 완료된 유저
    if (!Role.UNAUTH.equals(member.getRole())) {
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

  @Override
  public MemberDto editMemberInfo(Long memberId, MemberEditRequest request) {
    MemberEntity member = memberRepository.findById(memberId)
        .orElseThrow(() -> new MemberException(ErrorCode.MEMBER_NOT_FOUND));
    member.editMember(request);

    MemberEntity saved = memberRepository.save(member);

    return MemberDto.fromEntity(saved);
  }

  @Override
  public MemberPasswordEdit.Response editMemberPassword(Long memberId, MemberPasswordEdit.Request request) {
    MemberEntity member = memberRepository.findById(memberId)
        .orElseThrow(() -> new MemberException(ErrorCode.MEMBER_NOT_FOUND));
    if(!PasswordUtils.equalsPlainTextAndHashed(request.getCurrentPassword(), member.getPassword())){
      throw new MemberException(ErrorCode.MEMBER_PASSWORD_INCORRECT);
    }
    if(!request.getNewPassword().equals(request.getNewPasswordCheck())){
      throw new MemberException(ErrorCode.PASSWORD_CHECK_INCORRECT);
    }

    member.setPassword(PasswordUtils.encPassword(request.getNewPassword()));
    memberRepository.save(member);

    return new MemberPasswordEdit.Response(member.getMemberId(), ResponseConstant.PASSWORD_EDIT_COMPLETE);
  }

  @Override
  public MemberDto updateProfileImage(Long memberId, MultipartFile profileImage) {
    MemberEntity member = memberRepository.findById(memberId)
        .orElseThrow(() -> new MemberException(ErrorCode.MEMBER_NOT_FOUND));
    if(profileImage.isEmpty()){
      log.info("[updateProfileImage] - input image is Empty");
      return MemberDto.fromEntity(member);
    }

    if(!Objects.isNull(member.getProfileImage())){
      s3ImageService.deleteImageFromS3(member.getProfileImage());
    }
    String newImageAddress = s3ImageService.upload(profileImage);
    member.setProfileImage(newImageAddress);
    memberRepository.save(member);

    return MemberDto.fromEntity(member);
  }
}
