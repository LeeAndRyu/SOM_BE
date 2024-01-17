package com.blog.som.domain.member.dto;

import com.blog.som.domain.member.entity.MemberEntity;
import com.blog.som.domain.member.type.Role;
import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MemberDto {

  private Long memberId;

  private String email;

  private String nickname;

  private String phoneNumber;

  private LocalDate birthDate;

  private String profileImage;

  private LocalDateTime registeredAt;

  private LocalDateTime emailAuthAt;

  private Role role;

  public static MemberDto fromEntity(MemberEntity member){
    return MemberDto.builder()
        .memberId(member.getMemberId())
        .email(member.getEmail())
        .nickname(member.getNickname())
        .phoneNumber(member.getPhoneNumber())
        .birthDate(member.getBirthDate())
        .profileImage(member.getProfileImage())
        .registeredAt(member.getRegisteredAt())
        .emailAuthAt(member.getEmailAuthAt())
        .role(member.getRole())
        .build();
  }
}
