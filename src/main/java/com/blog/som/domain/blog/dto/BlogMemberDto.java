package com.blog.som.domain.blog.dto;

import com.blog.som.domain.member.entity.MemberEntity;
import com.blog.som.domain.follow.type.FollowStatus;
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
public class BlogMemberDto {
  private String blogName;
  private String profileImage;
  private String nickname;
  private String introduction;

  private int followerCount;
  private int followingCount;

  private FollowStatus followStatus;

  public static BlogMemberDto fromEntity(MemberEntity member){
    return BlogMemberDto.builder()
        .blogName(member.getBlogName())
        .profileImage(member.getProfileImage())
        .nickname(member.getNickname())
        .introduction(member.getIntroduction())
        .followerCount(member.getFollowerCount())
        .followingCount(member.getFollowingCount())
        .build();
  }
}
