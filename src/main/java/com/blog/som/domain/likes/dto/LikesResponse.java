package com.blog.som.domain.likes.dto;

import com.blog.som.domain.likes.constant.LikesConstant;
import com.blog.som.domain.likes.type.LikesStatus;
import lombok.Getter;
import lombok.Setter;


public class LikesResponse {

  @Getter
  @Setter
  public static class ToggleResult {

    private boolean result;
    private Long memberId;
    private Long postId;
    private String message;

    public ToggleResult(boolean result, Long memberId, Long postId) {
      this.result = result;
      if (result) {
        this.memberId = memberId;
        this.postId = postId;
        this.message = LikesConstant.LIKES_COMPLETE;
      } else {
        this.memberId = memberId;
        this.postId = postId;
        this.message = LikesConstant.LIKES_CANCELED;
      }

    }
  }

  @Getter
  @Setter
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
  }


}
