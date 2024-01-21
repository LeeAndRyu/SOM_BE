package com.blog.som.domain.member.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class MemberPasswordEdit {

  @Getter
  @Setter
  @AllArgsConstructor
  @NoArgsConstructor
  @Builder
  public static class Request{
    private String currentPassword;
    private String newPassword;
    private String newPasswordCheck;
  }

  @Getter
  @AllArgsConstructor
  public static class Response{
    private Long memberId;
    private String message;
  }
}
