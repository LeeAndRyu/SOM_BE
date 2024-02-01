package com.blog.som.domain.member.dto;


import com.blog.som.domain.member.entity.MemberEntity;
import com.blog.som.domain.member.type.Role;
import com.blog.som.global.util.PasswordUtils;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

public class MemberRegister {

  private MemberRegister() {
  }

  @Getter
  @Setter
  @AllArgsConstructor
  @Builder
  public static class EmailDuplicateResponse {

    private boolean reuslt;
    private String email;
  }

  @Getter
  @Setter
  @AllArgsConstructor
  @Builder
  public static class Request {

    private String password;
    private String nickname;
    private String accountName;
    private String introduction;

    public static MemberEntity toEntity(String email, Request request) {
      return MemberEntity.builder()
          .email(email)
          .password(PasswordUtils.encPassword(request.getPassword()))
          .nickname(request.getNickname())
          .accountName(request.getAccountName())
          .blogName(request.accountName + ".som")
          .introduction(request.getIntroduction())
          .role(Role.USER)
          .build();
    }
  }
}
