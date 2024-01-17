package com.blog.som.global.redis.email;

import com.blog.som.global.constant.TimeConstant;
import com.blog.som.global.exception.ErrorCode;
import com.blog.som.global.exception.custom.MemberException;
import java.time.Duration;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class EmailAuthRedisRepository implements EmailAuthRepository{

  private final RedisTemplate redisTemplate;


  @Override
  public void saveEmailAuthUuid(String uuid, Long id) {
    ValueOperations<String, String> emailAuths = redisTemplate.opsForValue();
    emailAuths.set(uuid, String.valueOf(id), Duration.ofMinutes(TimeConstant.EMAIL_AUTH_MINUTE));
  }

  @Override
  public Long getEmailAuthMemberId(String uuid) {
    ValueOperations<String, String> emailAuths = redisTemplate.opsForValue();

    if(Boolean.FALSE.equals(redisTemplate.hasKey(uuid))){
      throw new MemberException(ErrorCode.EMAIL_AUTH_TIME_OUT);
    }

    return Long.valueOf(emailAuths.get(uuid));
  }

  @Override
  public void deleteEmailAuthUuid(String uuid){
    redisTemplate.delete(uuid);
  }
}
