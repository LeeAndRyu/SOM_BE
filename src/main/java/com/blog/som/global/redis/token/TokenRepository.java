package com.blog.som.global.redis.token;

public interface TokenRepository {

  void saveRefreshToken(String email, String refreshToken);

  void addBlacklistAccessToken(String accessToken, String email);

  boolean deleteRefreshToken(String email);
}
