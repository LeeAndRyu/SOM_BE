package com.blog.som.domain.member.service;

import com.blog.som.domain.member.dto.MemberDto;
import com.blog.som.domain.member.dto.MemberEditRequest;
import com.blog.som.domain.member.dto.MemberPasswordEdit;
import com.blog.som.domain.member.dto.MemberRegister.EmailDuplicateResponse;
import com.blog.som.domain.member.dto.MemberRegister.Request;
import com.blog.som.domain.member.entity.MemberEntity;
import com.blog.som.domain.member.repository.MemberRepository;
import com.blog.som.global.components.mail.MailSender;
import com.blog.som.global.components.mail.SendMailDto;
import com.blog.som.global.components.password.PasswordUtils;
import com.blog.som.global.constant.ResponseConstant;
import com.blog.som.global.exception.ErrorCode;
import com.blog.som.global.exception.custom.MemberException;
import com.blog.som.global.redis.email.CacheRepository;
import com.blog.som.global.s3.S3ImageService;
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
  private final CacheRepository cacheRepository;
  private final S3ImageService s3ImageService;

  @Override
  public EmailDuplicateResponse emailDuplicateCheckAndStartRegister(String email) {
    if (memberRepository.existsByEmail(email)) {
      return new EmailDuplicateResponse(true, email);
    }

    mailSender.sendMailForRegister(new SendMailDto(email));
    return new EmailDuplicateResponse(false, email);
  }

  @Override
  public MemberDto registerMember(Request request, String code) {

    String email = cacheRepository.getEmailByUuid(code);

    if (memberRepository.existsByEmail(email)) {
      throw new MemberException(ErrorCode.EMAIL_AUTH_ALREADY_COMPLETE);
    }

    if(memberRepository.existsByAccountName(request.getAccountName())){
      throw new MemberException(ErrorCode.ACCOUNT_NAME_ALREADY_EXISTS);
    }

    //회원 저장
    MemberEntity savedMember = memberRepository.save(
        Request.toEntity(email, request));

    return MemberDto.fromEntity(savedMember);
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
    if (!PasswordUtils.equalsPlainTextAndHashed(request.getCurrentPassword(), member.getPassword())) {
      throw new MemberException(ErrorCode.MEMBER_PASSWORD_INCORRECT);
    }
    if (!request.getNewPassword().equals(request.getNewPasswordCheck())) {
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
    if (profileImage.isEmpty()) {
      log.info("[updateProfileImage] - input image is Empty");
      return MemberDto.fromEntity(member);
    }

    if (!Objects.isNull(member.getProfileImage())) {
      s3ImageService.deleteImageFromS3(member.getProfileImage());
    }
    String newImageAddress = s3ImageService.upload(profileImage);
    member.setProfileImage(newImageAddress);
    memberRepository.save(member);

    return MemberDto.fromEntity(member);
  }

  @Override
  public MemberDto deleteProfileImage(Long memberId) {
    MemberEntity member = memberRepository.findById(memberId)
        .orElseThrow(() -> new MemberException(ErrorCode.MEMBER_NOT_FOUND));

    if (Objects.isNull(member.getProfileImage())) {
      return MemberDto.fromEntity(member);
    }

    s3ImageService.deleteImageFromS3(member.getProfileImage());
    member.setProfileImage(null);
    memberRepository.save(member);

    return MemberDto.fromEntity(member);
  }
}
