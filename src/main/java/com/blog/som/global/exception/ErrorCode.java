package com.blog.som.global.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {
  //Member 관련
  MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "회원을 찾을 수 없습니다."),
  MEMBER_ALREADY_EXISTS(HttpStatus.CONFLICT, "이미 회원가입 된 이메일입니다."),
  EMAIL_AUTH_TIME_OUT(HttpStatus.NOT_FOUND, "이메일 인증 키가 만료되었거나, 잘못된 요청 입니다."),
  EMAIL_AUTH_WRONG_KEY(HttpStatus.NOT_FOUND, "이메일 인증 키에 문제가 있습니다."),
  EMAIL_AUTH_ALREADY_COMPLETE(HttpStatus.NOT_FOUND, "이미 이메일 인증 완료 된 회원입니다."),
  EMAIL_AUTH_REQUIRED(HttpStatus.NOT_FOUND, "이메일 인증이 완료되지 않았습니다. 다시 이메일이 발송되었습니다."),



  //Security
  LOGIN_FAILED_MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "계정이 존재하지 않습니다."),
  LOGIN_FAILED_PASSWORD_INCORRECT(HttpStatus.UNAUTHORIZED, "비밀번호가 틀립니다."),
  ACCESS_DENIED(HttpStatus.FORBIDDEN, "접근 권한이 없습니다."),
  LOGIN_REQUIRED(HttpStatus.UNAUTHORIZED, "로그인이 되지 않았습니다."),

  JWT_REFRESH_TOKEN_NOT_FOUND(HttpStatus.BAD_REQUEST, "존재하지 않는 RefreshToken 입니다. 다시 로그인 해주세요."),
  JWT_TOKEN_ALREADY_LOGGED_OUT(HttpStatus.UNAUTHORIZED, "로그아웃된 인증 정보입니다."),
  TOKEN_TIME_OUT(HttpStatus.FORBIDDEN, "토큰이 만료되었습니다."),
  JWT_TOKEN_WRONG_TYPE(HttpStatus.FORBIDDEN, "JWT 토큰 형식에 문제가 있습니다."),
  JWT_TOKEN_MALFORMED(HttpStatus.FORBIDDEN, "JWT 토큰이 변조되었습니다."),
  NO_JWT_TOKEN(HttpStatus.FORBIDDEN, "JWT 토큰이 존재하지 않습니다."),
  REFRESH_TOKEN_NOT_COINCIDENCE(HttpStatus.FORBIDDEN, "RefreshToken이 일치하지 않습니다."),


  //global
  INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "내부 서버 오류가 발생 했습니다.");


  private final HttpStatus status;
  private final String description;

}
