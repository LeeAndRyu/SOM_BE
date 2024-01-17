package com.blog.som.global.redis.email;

public interface EmailAuthRepository {

  void saveEmailAuthUuid(String uuid, Long id);

  Long getEmailAuthMemberId(String uuid);
}
