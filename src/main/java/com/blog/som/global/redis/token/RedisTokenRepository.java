package com.blog.som.global.redis.token;

import com.blog.som.domain.member.security.token.TokenConstant;
import com.blog.som.global.exception.ErrorCode;
import com.blog.som.global.exception.custom.CustomSecurityException;
import java.time.Duration;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

@Slf4j
@RequiredArgsConstructor
@Repository
public class RedisTokenRepository implements TokenRepository {

  private final RedisTemplate<String, String> redisTemplate;

  @Override
  public void saveRefreshToken(String email, String refreshToken) {
    ValueOperations<String, String> values = redisTemplate.opsForValue();
    values.set(
        TokenConstant.REFRESH_TOKEN_PREFIX + email,
        refreshToken,
        Duration.ofMillis(TokenConstant.REFRESH_TOKEN_EXPIRE_TIME)
    );
  }

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
  public boolean isAccessTokenBlacklist(String accessToken) {
    return Boolean.TRUE.equals(redisTemplate.hasKey(TokenConstant.ACCESS_TOKEN_BLACKLIST_PREFIX + accessToken));
  }

  @Override
  public boolean deleteRefreshToken(String email) {
    return Boolean.TRUE.equals(redisTemplate.delete(TokenConstant.REFRESH_TOKEN_EMAIL_KEY_PREFIX + email));
  }

  @Override
  public void checkRefreshToken(String email, String refreshToken) {
    //해당 email에 대한 데이터가 존재하지 않을 때
    if(Boolean.FALSE.equals(redisTemplate.hasKey(TokenConstant.REFRESH_TOKEN_EMAIL_KEY_PREFIX + email))){
      throw new CustomSecurityException(ErrorCode.JWT_REFRESH_TOKEN_NOT_FOUND);
    }
    ValueOperations<String, String> values = redisTemplate.opsForValue();
    String rt = values.get(TokenConstant.REFRESH_TOKEN_EMAIL_KEY_PREFIX + email);
    //RT가 존재하지 않을 때
    if(!StringUtils.hasText(rt)){
      throw new CustomSecurityException(ErrorCode.JWT_REFRESH_TOKEN_NOT_FOUND);
    }
    //해당 유저의 RT가 정확할 때
    if(refreshToken.equals(rt)){
      return;
    }
    throw new CustomSecurityException(ErrorCode.REFRESH_TOKEN_NOT_COINCIDENCE);
  }
}
