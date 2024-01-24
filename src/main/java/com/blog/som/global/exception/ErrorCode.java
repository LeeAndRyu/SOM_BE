package com.blog.som.global.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode{
  //Member 관련
  MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "회원을 찾을 수 없습니다."),
  EMAIL_AUTH_TIME_OUT(HttpStatus.BAD_REQUEST, "이메일 인증 키가 만료되었거나, 잘못된 요청 입니다."),
  EMAIL_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "이미 존재하는 이메일입니다."),
  MEMBER_PASSWORD_INCORRECT(HttpStatus.BAD_REQUEST, "비밀번호가 틀립니다."),
  PASSWORD_CHECK_INCORRECT(HttpStatus.BAD_REQUEST, "비밀번호 확인이 일치하지 않습니다."),
  ACCOUNT_NAME_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "이미 존재하는 계정 명 입니다."),

  //Post 관련
  POST_NOT_FOUND(HttpStatus.NOT_FOUND, "게시글을 찾을 수 없습니다."),
  POST_EDIT_NO_AUTHORITY(HttpStatus.FORBIDDEN, "게시글 수정 권한이 없습니다."),
  POST_DELETE_NO_AUTHORITY(HttpStatus.FORBIDDEN, "게시글 삭제 권한이 없습니다."),

  //Blog 관련
  BLOG_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 블로그가 존재하지 않습니다."),
  BLOG_POSTS_INVALID_QUERY(HttpStatus.BAD_REQUEST, "BLOG_POSTS_잘못된 쿼리 입니다."),
  TAG_NOT_FOUND(HttpStatus.BAD_REQUEST, "tag를 찾을 수 없습니다."),

  //Follow 관련
  ALREADY_FOLLOWED(HttpStatus.CONFLICT, "이미 팔로우 된 블로그입니다."),


  //S3 image upload
  EMPTY_FILE_EXCEPTION(HttpStatus.BAD_REQUEST, "빈 파일 입니다."),
  NO_FILE_EXTENTION(HttpStatus.BAD_REQUEST, "파일 확장자가 존재하지 않습니다."),
  INVALID_FILE_EXTENTION(HttpStatus.BAD_REQUEST, "유효하지 않은 파일 확장자 입니다."),
  PUT_OBJECT_EXCEPTION(HttpStatus.INTERNAL_SERVER_ERROR, "Amazon S3에 파일을 업로드 하는데 실패했습니다."),
  IO_EXCEPTION_ON_IMAGE_UPLOAD(HttpStatus.INTERNAL_SERVER_ERROR, "image upload 도중 image.getInputStream() 또는 IOUtils.toByteArray(is)에서 에러가 발생했습니다."),
  ADDRESS_URL_ERROR_ON_IMAGE_DELETE(HttpStatus.BAD_REQUEST, "image url에 문제가 있습니다."),


  //Security
  LOGIN_FAILED_MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "계정이 존재하지 않습니다."),
  LOGIN_FAILED_PASSWORD_INCORRECT(HttpStatus.UNAUTHORIZED, "로그인에 실패하였씁니다. 비밀번호가 틀립니다."),
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
