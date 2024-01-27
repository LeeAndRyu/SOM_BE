package com.blog.som.global.redis.email;

public interface CacheRepository {

  void saveEmailAuthUuid(String uuid, String email);

  String getEmailByUuid(String uuid);

  boolean canAddView(String accessUserAgent, Long postId);
}
