package com.blog.som.domain.member.controller;

import com.blog.som.domain.member.dto.MemberRegister;
import com.blog.som.domain.member.dto.MemberRegister.Response;
import com.blog.som.domain.member.service.MemberService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = "회원(Member)")
@RequiredArgsConstructor
@RestController
public class MemberController {

  private final MemberService memberService;

  @ApiOperation("회원 가입")
  @PostMapping("/member/register")
  public ResponseEntity<MemberRegister.Response> registerMember(@RequestBody MemberRegister.Request request) {
    Response response = memberService.registerMember(request);
    return ResponseEntity.ok(response);
  }


}
