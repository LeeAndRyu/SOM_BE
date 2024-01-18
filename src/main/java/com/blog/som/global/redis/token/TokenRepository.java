package com.blog.som.global.redis.token;

public interface TokenRepository {

  void addBlacklistAccessToken(String accessToken, String email);

  boolean deleteRefreshToken(String email);
}
