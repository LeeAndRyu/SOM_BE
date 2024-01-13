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




  //global
  INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "내부 서버 오류가 발생 했습니다.");


  private final HttpStatus statusCode;
  private final String description;

}
