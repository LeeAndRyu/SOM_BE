package com.blog.som.global.redis.email;

import com.blog.som.global.constant.TimeConstant;
import com.blog.som.global.exception.ErrorCode;
import com.blog.som.global.exception.custom.MemberException;
import com.blog.som.global.redis.constant.RedisConstant;
import java.time.Duration;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Repository;

@Slf4j
@RequiredArgsConstructor
@Repository
public class CacheRedisRepository implements CacheRepository {

  private final RedisTemplate redisTemplate;


  @Override
  public void saveEmailAuthUuid(String uuid, String email) {
    ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
    valueOperations.set(uuid, email, Duration.ofMinutes(TimeConstant.EMAIL_AUTH_MINUTE));
  }

  @Override
  public String getEmailByUuid(String uuid) {
    ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();

    if(Boolean.FALSE.equals(redisTemplate.hasKey(uuid))){
      log.info("[email auth fail - email-auth key doesnt exist]");
      throw new MemberException(ErrorCode.EMAIL_AUTH_TIME_OUT);
    }

    return valueOperations.get(uuid);
  }

  @Override
  public boolean canAddView(String accessUserAgent, Long postId) {
    String key = RedisConstant.VIEW_PREFIX + accessUserAgent + postId;
    if(redisTemplate.hasKey(key)){
      return false;
    }
    ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();

    valueOperations.set(key, "view", Duration.ofMinutes(TimeConstant.ADD_VIEW_MINUTE));

    return true;
  }

}
