package com.blog.som.domain.member.security.errorhandling;

import com.blog.som.global.exception.ErrorCode;
import com.blog.som.global.exception.custom.CustomSecurityException;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


@Slf4j
@RestController
public class SecurityExceptionController {

  @ApiOperation(value = "ACCESS_DENIED 오류 발생 시", notes = "로그인은 되어있으나 접근 권한이 없을 때")
  @GetMapping("/exception/auth-denied")
  public void accessDenied() {
    log.info("ACCESS_DENIED - SecurityController");
    throw new CustomSecurityException(ErrorCode.ACCESS_DENIED);
  }

  @ApiOperation(value = "LOGIN_REQUIRED 오류 발생 시", notes = "로그인 되지 않은 상태일 때")
  @GetMapping("/exception/unauthorized")
  public void unauthorized() {
    log.info("LOGIN_REQUIRED - SecurityController");
    throw new CustomSecurityException(ErrorCode.LOGIN_REQUIRED);
  }
}