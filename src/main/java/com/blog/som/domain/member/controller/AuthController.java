package com.blog.som.domain.member.controller;

import com.blog.som.domain.member.dto.MemberDto;
import com.blog.som.domain.member.dto.MemberLogin;
import com.blog.som.domain.member.dto.MemberLogin.Response;
import com.blog.som.domain.member.dto.MemberLogoutResponse;
import com.blog.som.domain.member.dto.TokenResponse;
import com.blog.som.domain.member.security.service.AuthService;
import com.blog.som.domain.member.security.token.JwtTokenService;
import com.blog.som.domain.member.security.userdetails.LoginMember;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = "인증 관련 (login, logout, token)")
@RequiredArgsConstructor
@RestController
public class AuthController {

  private final AuthService authService;
  private final JwtTokenService jwtTokenService;

  @ApiOperation(value = "로그인, JWT token 발행", notes = "accessToken, refreshToken, member 정보 반환")
  @PostMapping("/login")
  public ResponseEntity<MemberLogin.Response> login(@RequestBody MemberLogin.Request request) {

    MemberDto member = authService.loginMember(request);

    TokenResponse tokenResponse = jwtTokenService.generateTokenResponse(member.getEmail(), member.getRole());

    authService.saveRefreshToken(member.getEmail(), tokenResponse.getRefreshToken());

    return ResponseEntity.ok(new MemberLogin.Response(tokenResponse, member));
  }

  @ApiOperation(value = "로그아웃", notes = "accessToken blacklist 처리")
  @PostMapping("/logout")
  public ResponseEntity<MemberLogoutResponse> logout(
      @AuthenticationPrincipal LoginMember loginMember,
      @RequestHeader("Authorization") String bearerToken) {

    MemberLogoutResponse memberLogoutResponse =
        authService.logoutMember(loginMember.getEmail(), jwtTokenService.resolveTokenFromRequest(bearerToken));

    return ResponseEntity.ok(memberLogoutResponse);
  }

  @ApiOperation(value = "토큰 재발급", notes = "[RefreshToken] header에 Bearer {refreshToken}을 받으면 AT와 RT를 모두 재발급")
  @PostMapping("/reissue")
  public ResponseEntity<MemberLogin.Response> reissueToken(
      @AuthenticationPrincipal LoginMember loginMember,
      @RequestHeader("RefreshToken") String refreshToken
      ){

    //refreshToken이 일치하는지 확인
    authService.checkRefreshToken(loginMember.getEmail(), jwtTokenService.resolveTokenFromRequest(refreshToken));
    TokenResponse tokenResponse = jwtTokenService.generateTokenResponse(loginMember.getEmail(), loginMember.getRole());
    MemberDto memberDto = authService.saveRefreshToken(loginMember.getEmail(), tokenResponse.getRefreshToken());

    return ResponseEntity.ok(new Response(tokenResponse, memberDto));
  }

}
