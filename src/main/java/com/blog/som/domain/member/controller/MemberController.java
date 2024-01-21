package com.blog.som.domain.member.controller;

import com.blog.som.domain.member.dto.EmailAuthResult;
import com.blog.som.domain.member.dto.MemberDto;
import com.blog.som.domain.member.dto.MemberEditRequest;
import com.blog.som.domain.member.dto.MemberPasswordEdit;
import com.blog.som.domain.member.dto.MemberRegister;
import com.blog.som.domain.member.dto.MemberRegister.Response;
import com.blog.som.domain.member.security.userdetails.LoginMember;
import com.blog.som.domain.member.service.MemberService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
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

  @ApiOperation("회원 가입")
  @PostMapping("/register")
  public ResponseEntity<MemberRegister.Response> registerMember(@RequestBody MemberRegister.Request request) {

    Response response = memberService.registerMember(request);
    return ResponseEntity.ok(response);
  }

  @ApiOperation("이메일 인증")
  @GetMapping("/auth/email-auth")
  public ResponseEntity<EmailAuthResult> emailAuth(@RequestParam String key){

    EmailAuthResult emailAuthResult = memberService.emailAuth(key);
    return ResponseEntity.ok(emailAuthResult);
  }

  @ApiOperation(value = "회원 정보 수정", notes = "비밀번호 제외")
  @PutMapping("/member")
  public ResponseEntity<MemberDto> editMemberInfo(
      @RequestBody MemberEditRequest request,
      @AuthenticationPrincipal LoginMember loginMember){

    MemberDto result = memberService.editMemberInfo(loginMember.getMemberId(), request);
    return ResponseEntity.ok(result);
  }

  @ApiOperation(value = "회원 비밀번호 수정")
  @PutMapping("/member/edit-password")
  public ResponseEntity<MemberPasswordEdit.Response> editMemberPassword(
      @RequestBody MemberPasswordEdit.Request request,
      @AuthenticationPrincipal LoginMember loginMember){
    MemberPasswordEdit.Response response =
        memberService.editMemberPassword(loginMember.getMemberId(), request);
    return ResponseEntity.ok(response);
  }

  @ApiOperation(value = "프로필 사진 변경(등록)", notes = "기존의 것이 있으면 덮어쓴다.")
  @PutMapping("/member/profile-image")
  public ResponseEntity<MemberDto> editProfileImage(@RequestPart(required = false) MultipartFile profileImage,
      @AuthenticationPrincipal LoginMember loginMember){
    MemberDto memberDto = memberService.updateProfileImage(loginMember.getMemberId(), profileImage);
    return ResponseEntity.ok(memberDto);
  }

  @ApiOperation(value = "프로필 사진 삭제", notes = "(TODO)프로필 사진을 null로 수정")
  @DeleteMapping("/member/profile-image/remove")
  public ResponseEntity<?> deleteProfileImage(){

    return ResponseEntity.ok(null);
  }


}
