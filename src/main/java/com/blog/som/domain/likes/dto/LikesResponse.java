package com.blog.som.domain.likes.dto;

import com.blog.som.domain.likes.constant.LikesConstant;
import com.blog.som.domain.likes.type.LikesStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


public class LikesResponse {

  @Getter
  @Setter
  @AllArgsConstructor
  @NoArgsConstructor
  @Builder
  public static class ToggleResult {

    private LikesStatus likesStatus;
    private Long memberId;
    private Long postId;
    private String message;

    public ToggleResult(boolean result, Long memberId, Long postId) {

      if (result) {
        this.likesStatus = LikesStatus.LIKES;
        this.memberId = memberId;
        this.postId = postId;
        this.message = LikesConstant.LIKES_COMPLETE;
      } else {
        this.likesStatus = LikesStatus.NOT_LIKES;
        this.memberId = memberId;
        this.postId = postId;
        this.message = LikesConstant.LIKES_CANCELED;
      }
    }

    public static ToggleResult unAuth() {
      return ToggleResult.builder()
          .likesStatus(LikesStatus.NOT_LOGGED_IN)
          .memberId(0L)
          .postId(0L)
          .message("")
          .build();
    }
  }

  @Getter
  @Setter
  @AllArgsConstructor
  @NoArgsConstructor
  @Builder
  public static class MemberLikesPost {

    private LikesStatus likesStatus;
    private Long memberId;
    private Long postId;
    private String message;

    public MemberLikesPost(boolean result, Long memberId, Long postId) {
      if (result) {
        this.likesStatus = LikesStatus.LIKES;
        this.memberId = memberId;
        this.postId = postId;
        this.message = LikesConstant.LIKES_EXISTS;
      } else {
        this.likesStatus = LikesStatus.NOT_LIKES;
        this.memberId = memberId;
        this.postId = postId;
        this.message = LikesConstant.LIKES_DOESNT_EXISTS;
      }

    }

    public static MemberLikesPost unAuth() {
      return MemberLikesPost.builder()
          .likesStatus(LikesStatus.NOT_LOGGED_IN)
          .memberId(0L)
          .postId(0L)
          .message("")
          .build();
    }
  }


}
