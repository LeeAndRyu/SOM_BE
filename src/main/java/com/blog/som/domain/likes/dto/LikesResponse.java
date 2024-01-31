package com.blog.som.domain.likes.dto;

import com.blog.som.domain.likes.constant.LikesConstant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


public class LikesResponse {

  @Getter
  @Setter
  public static class ToggleResult{
    private Long memberId;
    private Long postId;
    private String message;

    public ToggleResult(boolean result, Long memberId, Long postId) {
      if(result){
        this.memberId = memberId;
        this.postId = postId;
        this.message = LikesConstant.LIKES_COMPLETE;
      }else{
        this.memberId = memberId;
        this.postId = postId;
        this.message = LikesConstant.LIKES_CANCELED;
      }

    }
  }

  @Getter
  @Setter
  public static class MemberLikesPost{
    private boolean result;
    private Long memberId;
    private Long postId;
    private String message;

    public MemberLikesPost(boolean result, Long memberId, Long postId) {
      if(result){
        this.result = result;
        this.memberId = memberId;
        this.postId = postId;
        this.message = LikesConstant.LIKES_EXISTS;
      }else{
        this.result = result;
        this.memberId = memberId;
        this.postId = postId;
        this.message = LikesConstant.LIKES_DOESNT_EXISTS;
      }

    }
  }





}
