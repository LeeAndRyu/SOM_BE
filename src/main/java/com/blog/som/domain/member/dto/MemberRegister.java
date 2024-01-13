package com.blog.som.domain.member.dto;


import com.blog.som.domain.member.entity.MemberEntity;
import com.blog.som.global.constant.ResponseConstant;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class MemberRegister {

  private MemberRegister(){}

  @Getter
  @Setter
  @AllArgsConstructor
  @Builder
  public static class Request {

    private String email;
    private String password;
    private String nickname;
    private String phoneNumber;
    private LocalDate birthDate;

    public static MemberEntity toEntity(Request request, String encodedPassword) {
      return MemberEntity.builder()
          .email(request.getEmail())
          .password(encodedPassword)
          .nickname(request.getNickname())
          .phoneNumber(request.getPhoneNumber().replaceAll("-", ""))
          .birthDate(request.getBirthDate())
          .build();
    }
  }

  @Getter
  @Setter
  @AllArgsConstructor
  @NoArgsConstructor
  @Builder
  public static class Response {

    private Long memberId;
    private String email;
    private String nickname;
    private String message;

    public static Response fromEntity(MemberEntity member) {
      return Response.builder()
          .memberId(member.getMemberId())
          .email(member.getEmail())
          .nickname(member.getNickname())
          .message(ResponseConstant.MEMBER_REGISTER_COMPLETE)
          .build();
    }

  }

}
