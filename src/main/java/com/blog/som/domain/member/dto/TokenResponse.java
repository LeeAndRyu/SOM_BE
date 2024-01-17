package com.blog.som.domain.member.dto;

import com.blog.som.domain.member.security.token.TokenConstant;
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
public class TokenResponse {
  private String accessToken;
  private String refreshToken;
  private String tokenType;

  public TokenResponse(String accessToken, String refreshToken) {
    this.accessToken = accessToken;
    this.refreshToken = refreshToken;
    this.tokenType = TokenConstant.BEARER;
  }
}
