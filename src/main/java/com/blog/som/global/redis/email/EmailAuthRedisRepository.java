package com.blog.som.global.redis.email;

import com.blog.som.global.constant.TimeConstant;
import com.blog.som.global.exception.ErrorCode;
import com.blog.som.global.exception.custom.MemberException;
import java.time.Duration;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Repository;

@Slf4j
@RequiredArgsConstructor
@Repository
public class EmailAuthRedisRepository implements EmailAuthRepository{

  private final RedisTemplate redisTemplate;


  @Override
  public void saveEmailAuthUuid(String uuid, String email) {
    ValueOperations<String, String> emailAuths = redisTemplate.opsForValue();
    emailAuths.set(uuid, email, Duration.ofMinutes(TimeConstant.EMAIL_AUTH_MINUTE));
  }

  @Override
  public String getEmailByUuid(String uuid) {
    ValueOperations<String, String> emailAuths = redisTemplate.opsForValue();

    if(Boolean.FALSE.equals(redisTemplate.hasKey(uuid))){
      log.info("[email auth fail - email-auth key doesnt exist]");
      throw new MemberException(ErrorCode.EMAIL_AUTH_TIME_OUT);
    }

    return emailAuths.get(uuid);
  }

}
