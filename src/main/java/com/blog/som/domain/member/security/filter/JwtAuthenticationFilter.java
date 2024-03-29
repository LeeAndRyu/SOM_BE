package com.blog.som.domain.member.security.filter;

import com.blog.som.domain.member.security.token.JwtTokenService;
import com.blog.som.global.redis.token.TokenRepository;
import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * Authorization 헤더를 확인해서 Token으로 권한을 확인 후 SecurityContext에 권한을 넣어주는 필터
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  public static final String TOKEN_HEADER = "Authorization";

  private final JwtTokenService jwtTokenService;
  private final TokenRepository tokenRepository;

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
      FilterChain filterChain) throws ServletException, IOException {
    String token = jwtTokenService.resolveTokenFromRequest(request.getHeader(TOKEN_HEADER));

    if (StringUtils.hasText(token) && jwtTokenService.validateToken(token)
    && !tokenRepository.isAccessTokenBlacklist(token)) {
      //토큰 유효성 검증 성공
      Authentication auth = jwtTokenService.getAuthentication(token);
      SecurityContextHolder.getContext().setAuthentication(auth);
    } else {
      log.info("토큰 유효성 검증 실패 !!!");
      Authentication auth = jwtTokenService.getAnonymousAuthentication();
      SecurityContextHolder.getContext().setAuthentication(auth);
    }
    filterChain.doFilter(request, response);
  }


}
