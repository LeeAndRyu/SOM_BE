package com.blog.som.global.redis.email;

public interface EmailAuthRepository {

  void saveEmailAuthUuid(String uuid, String email);

  String getEmailByUuid(String uuid);
}
