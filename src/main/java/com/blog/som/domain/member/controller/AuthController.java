package com.blog.som.domain.member.controller;

import com.blog.som.domain.member.dto.MemberDto;
import com.blog.som.domain.member.dto.MemberLogin;
import com.blog.som.domain.member.dto.MemberLogin.Response;
import com.blog.som.domain.member.dto.TokenResponse;
import com.blog.som.domain.member.security.service.AuthService;
import com.blog.som.domain.member.security.token.JwtTokenService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = "인증(Login, Logout, Token 등)")
@RequiredArgsConstructor
@RestController
public class AuthController {

  private final AuthService authService;
  private final JwtTokenService jwtTokenService;

  @ApiOperation(value = "로그인, JWT token 발행", notes = "accessToken, refreshToken, member 정도 반환")
  @PostMapping("/login")
  public ResponseEntity<Response> login(@RequestBody MemberLogin.Request request) {

    MemberDto member = authService.loginMember(request);
    TokenResponse tokenResponse = jwtTokenService.generateTokenResponse(member.getEmail(), member.getRole());

    return ResponseEntity.ok(new MemberLogin.Response(tokenResponse, member));
  }

}
