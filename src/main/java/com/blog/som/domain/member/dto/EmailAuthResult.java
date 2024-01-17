package com.blog.som.domain.member.dto;

import com.blog.som.domain.member.entity.MemberEntity;
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
public class EmailAuthResult {

  private boolean result;
  private String message;
  private MemberDto memberDto;

  public EmailAuthResult(boolean result, MemberEntity member) {
    this.result = result;
    if(result){
      this.message = ResponseConstant.EMAIL_AUTH_COMPLETE;
    }else{
      this.message = ResponseConstant.EMAIL_AUTH_ALREADY_COMPLETED;
    }
    this.memberDto = MemberDto.fromEntity(member);
  }
}
