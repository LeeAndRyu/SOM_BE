package com.blog.som.domain.member.controller;

import com.blog.som.domain.member.dto.EmailAuthResult;
import com.blog.som.domain.member.dto.MemberRegister;
import com.blog.som.domain.member.dto.MemberRegister.Response;
import com.blog.som.domain.member.service.MemberService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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

  @ApiOperation(value = "회원 정보 수정", notes = "(TODO)비밀번호 제외")
  @PutMapping("/member/{memberId}")
  public ResponseEntity<?> editMemberInfo(@PathVariable Long memberId){
    return ResponseEntity.ok(null);
  }

  @ApiOperation(value = "회원 비밀번호 수정", notes = "(TODO)")
  @PutMapping("/member/{memberId}/edit-password")
  public ResponseEntity<?> editMemberPassword(@PathVariable Long memberId){
    return ResponseEntity.ok(null);
  }

  @ApiOperation(value = "프로필 사진 변경(등록)", notes = "(TODO)기존의 것이 있으면 덮어쓰기")
  @PutMapping("/member/{memberId}/profile-image")
  public ResponseEntity<?> editProfileImage(@PathVariable Long memberId){
    return ResponseEntity.ok(null);
  }

  @ApiOperation(value = "프로필 사진 삭제", notes = "(TODO)프로필 사진을 null로 수정")
  @DeleteMapping("/member/{memberId}/profile-image/remove")
  public ResponseEntity<?> deleteProfileImage(@PathVariable Long memberId){
    return ResponseEntity.ok(null);
  }


}
