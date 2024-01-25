package com.blog.som.domain.member.service;

import com.blog.som.domain.member.dto.MemberDto;
import com.blog.som.domain.member.dto.MemberEditRequest;
import com.blog.som.domain.member.dto.MemberPasswordEdit;
import com.blog.som.domain.member.dto.MemberRegister;
import com.blog.som.domain.member.dto.RegisterEmailInput;
import org.springframework.web.multipart.MultipartFile;

public interface MemberService {

  /**
   * 회원 가입 이전에 이메일 중복 확인 결과 반환, 중복이 아닐 시 회원 가입 메일 발송
   */
  MemberRegister.EmailDuplicateResponse emailDuplicateCheckAndStartRegister(RegisterEmailInput input);

  /**
   * 회원 가입
   */
  MemberDto registerMember(MemberRegister.Request request, String code);

  /**
   * 회원 정보 수정
   */
  MemberDto editMemberInfo(Long memberId, MemberEditRequest request);

  /**
   * 비밀번호 수정
   */
  MemberPasswordEdit.Response editMemberPassword(Long memberId, MemberPasswordEdit.Request request);

  /**
   * 프로필 사진 등록 또는 수정
   */
  MemberDto updateProfileImage(Long memberId, MultipartFile profileImage);

  /**
   * 프로필 사진 삭제
   */
  MemberDto deleteProfileImage(Long memberId);
}
