package com.blog.som.domain.member.controller;

import com.blog.som.domain.member.dto.MemberDto;
import com.blog.som.domain.member.dto.MemberEditRequest;
import com.blog.som.domain.member.dto.MemberPasswordEdit;
import com.blog.som.domain.member.dto.MemberRegister;
import com.blog.som.domain.member.dto.MemberRegister.EmailDuplicateResponse;
import com.blog.som.domain.member.dto.RegisterEmailInput;
import com.blog.som.domain.member.security.userdetails.LoginMember;
import com.blog.som.domain.member.service.MemberService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Api(tags = "회원(Member)")
@RequiredArgsConstructor
@RestController
public class MemberController {

  private final MemberService memberService;

  @ApiOperation(value = "회원가입 시작", notes = "이메일 입력, 중복체크 , 중복 아닐 시 이메일 발송")
  @PostMapping("/register/check-email")
  public ResponseEntity<EmailDuplicateResponse> startRegister(@RequestBody RegisterEmailInput email) {
    EmailDuplicateResponse emailDuplicateResponse = memberService.emailDuplicateCheckAndStartRegister(email);
    return ResponseEntity.ok(emailDuplicateResponse);
  }

  @ApiOperation(value = "회원 가입", notes = "이메일 버튼 클릭 후, 추가 정보 입력하여 POST 요청")
  @PostMapping("/register")
  public ResponseEntity<MemberDto> register(@RequestParam String code,
      @RequestBody MemberRegister.Request request) {
    MemberDto memberDto = memberService.registerMember(request, code);
    return ResponseEntity.ok(memberDto);
  }

  @ApiOperation(value = "회원 정보 수정", notes = "비밀번호 제외")
  @PreAuthorize("hasAnyRole('ROLE_USER')")
  @PutMapping("/member")
  public ResponseEntity<MemberDto> editMemberInfo(
      @RequestBody MemberEditRequest request,
      @AuthenticationPrincipal LoginMember loginMember) {

    MemberDto result = memberService.editMemberInfo(loginMember.getMemberId(), request);
    return ResponseEntity.ok(result);
  }

  @ApiOperation(value = "회원 비밀번호 수정")
  @PreAuthorize("hasAnyRole('ROLE_USER')")
  @PutMapping("/member/edit-password")
  public ResponseEntity<MemberPasswordEdit.Response> editMemberPassword(
      @RequestBody MemberPasswordEdit.Request request,
      @AuthenticationPrincipal LoginMember loginMember) {
    MemberPasswordEdit.Response response =
        memberService.editMemberPassword(loginMember.getMemberId(), request);
    return ResponseEntity.ok(response);
  }

  @ApiOperation(value = "프로필 사진 변경(등록)", notes = "기존의 것이 있으면 덮어쓴다.")
  @PreAuthorize("hasAnyRole('ROLE_USER')")
  @PutMapping("/member/profile-image")
  public ResponseEntity<MemberDto> editProfileImage(@RequestPart(required = false) MultipartFile profileImage,
      @AuthenticationPrincipal LoginMember loginMember) {
    MemberDto memberDto = memberService.updateProfileImage(loginMember.getMemberId(), profileImage);
    return ResponseEntity.ok(memberDto);
  }

  @ApiOperation(value = "프로필 사진 삭제", notes = "프로필 사진을 null로 수정")
  @PreAuthorize("hasAnyRole('ROLE_USER')")
  @DeleteMapping("/member/profile-image/remove")
  public ResponseEntity<MemberDto> deleteProfileImage(@AuthenticationPrincipal LoginMember loginMember) {
    MemberDto memberDto = memberService.deleteProfileImage(loginMember.getMemberId());
    return ResponseEntity.ok(memberDto);
  }
}
