package com.blog.som.domain.member.dto;

import com.blog.som.domain.member.entity.MemberEntity;
import com.blog.som.domain.member.type.Role;
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

  private String accountName;

  private String blogName;

  private String introduction;

  private String profileImage;

  private int followingCount;

  private int followerCount;

  private LocalDateTime registeredAt;

  private Role role;

  public static MemberDto fromEntity(MemberEntity member) {
    return MemberDto.builder()
        .memberId(member.getMemberId())
        .email(member.getEmail())
        .nickname(member.getNickname())
        .accountName(member.getAccountName())
        .blogName(member.getBlogName())
        .introduction(member.getIntroduction())
        .profileImage(member.getProfileImage())
        .followingCount(member.getFollowingCount())
        .followerCount(member.getFollowerCount())
        .registeredAt(member.getRegisteredAt())
        .role(member.getRole())
        .build();
  }
}
