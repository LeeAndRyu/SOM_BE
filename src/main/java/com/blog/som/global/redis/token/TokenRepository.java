package com.blog.som.global.redis.token;

public interface TokenRepository {

  void saveRefreshToken(String email, String refreshToken);

  void addBlacklistAccessToken(String accessToken, String email);

  boolean isAccessTokenBlacklist(String accessToken);

  boolean deleteRefreshToken(String email);

  void checkRefreshToken(String email, String refreshToken);

}
