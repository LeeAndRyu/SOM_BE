package com.blog.som.domain.follow.dto;

import com.blog.som.domain.follow.entity.FollowEntity;
import com.blog.som.global.constant.ResponseConstant;
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
public class FollowCancelResponse {
  private Long fromMemberId;
  private Long toMemberId;
  private String message;

  public static FollowCancelResponse fromEntity(FollowEntity follow){
    return FollowCancelResponse.builder()
        .fromMemberId(follow.getFromMember().getMemberId())
        .toMemberId(follow.getToMember().getMemberId())
        .message(ResponseConstant.FOLLOW_CANCEL_COMPLETE)
        .build();
  }
}
