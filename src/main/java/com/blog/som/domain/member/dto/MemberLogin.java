package com.blog.som.domain.member.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class MemberLogin {

  private MemberLogin(){}

  @Getter
  @Setter
  @AllArgsConstructor
  @Builder
  public static class Request{
    private String email;
    private String password;
  }

  @Getter
  @Setter
  @AllArgsConstructor
  @NoArgsConstructor
  @Builder
  public static class Response{
    private TokenResponse tokenResponse;
    private MemberDto member;
  }

}
