package com.blog.som.domain.follow.dto;

import com.blog.som.domain.follow.entity.FollowEntity;
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
public class FollowDto {

  private Long followId;

  private Long fromMemberId;

  private Long toMemberId;

  private String toMemberBlogName;

  private LocalDateTime followAt;

  public static FollowDto fromEntity(FollowEntity follow){
    return FollowDto.builder()
        .followId(follow.getFollowId())
        .fromMemberId(follow.getFromMember().getMemberId())
        .toMemberId(follow.getToMember().getMemberId())
        .toMemberBlogName(follow.getToMember().getBlogName())
        .followAt(follow.getFollowAt())
        .build();
  }

}
