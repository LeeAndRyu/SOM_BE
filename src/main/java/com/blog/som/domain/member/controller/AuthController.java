package com.blog.som.domain.member.controller;

import com.blog.som.domain.member.dto.MemberLogin;
import com.blog.som.domain.member.dto.MemberLogin.Response;
import com.blog.som.domain.member.dto.MemberLogoutResponse;
import com.blog.som.domain.member.security.service.AuthService;
import com.blog.som.domain.member.security.service.CookieService;
import com.blog.som.domain.member.security.token.JwtTokenService;
import com.blog.som.domain.member.security.userdetails.LoginMember;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
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
  private final CookieService cookieService;

  @ApiOperation(value = "로그인, JWT token 발행", notes = "accessToken, refreshToken, member 정보 반환")
  @PostMapping("/login")
  public ResponseEntity<MemberLogin.Response> login(@RequestBody MemberLogin.Request request,
      HttpServletResponse httpServletResponse) {

    Response response = authService.loginMember(request);

    cookieService.setCookieForLogin(httpServletResponse, response.getTokenResponse().getAccessToken());

    return ResponseEntity.ok(response);
  }

  @ApiOperation(value = "로그아웃", notes = "accessToken blacklist 처리")
  @PostMapping("/logout")
  public ResponseEntity<MemberLogoutResponse> logout(
      @AuthenticationPrincipal LoginMember loginMember,
      @RequestHeader("Authorization") String bearerToken,
      HttpServletResponse httpServletResponse) {

    MemberLogoutResponse memberLogoutResponse =
        authService.logoutMember(loginMember.getEmail(), jwtTokenService.resolveTokenFromRequest(bearerToken));

    cookieService.expireCookieForLogout(httpServletResponse);
    return ResponseEntity.ok(memberLogoutResponse);
  }

  @ApiOperation(value = "토큰 재발급", notes = "[RefreshToken] header에 Bearer {refreshToken}을 받으면 AT와 RT를 모두 재발급")
  @GetMapping("/reissue")
  public ResponseEntity<MemberLogin.Response> reissueToken(
      @AuthenticationPrincipal LoginMember loginMember,
      @RequestHeader("RefreshToken") String refreshToken,
      HttpServletResponse httpServletResponse
      ){
    Response response = authService.reissueTokens(loginMember.getEmail(), loginMember.getRole(), refreshToken);
    cookieService.setCookieForLogin(httpServletResponse, response.getTokenResponse().getAccessToken());

    return ResponseEntity.ok(response);
  }

}
