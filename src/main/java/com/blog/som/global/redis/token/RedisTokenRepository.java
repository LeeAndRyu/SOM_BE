package com.blog.som.global.redis.token;

import com.blog.som.domain.member.security.token.TokenConstant;
import java.time.Duration;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Repository;

@Slf4j
@RequiredArgsConstructor
@Repository
public class RedisTokenRepository implements TokenRepository {

  private final RedisTemplate redisTemplate;

  @Override
  public void addBlacklistAccessToken(String accessToken, String email) {
    ValueOperations<String, String> values = redisTemplate.opsForValue();
    values.set(
        TokenConstant.ACCESS_TOKEN_BLACKLIST_PREFIX + accessToken,
        email,
        Duration.ofMillis(TokenConstant.ACCESS_TOKEN_EXPIRE_TIME)
    );
  }

  @Override
  public boolean deleteRefreshToken(String email) {
    return redisTemplate.delete(TokenConstant.REFRESH_TOKEN_EMAIL_KEY_PREFIX + email);
  }
}
