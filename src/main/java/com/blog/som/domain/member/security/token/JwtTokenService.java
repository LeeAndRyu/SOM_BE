package com.blog.som.domain.member.security.token;

import static com.blog.som.domain.member.security.token.TokenConstant.ACCESS_TOKEN_EXPIRE_TIME;
import static com.blog.som.domain.member.security.token.TokenConstant.BEARER;
import static com.blog.som.domain.member.security.token.TokenConstant.KEY_ROLES;
import static com.blog.som.domain.member.security.token.TokenConstant.REFRESH_TOKEN_EXPIRE_TIME;

import com.blog.som.domain.member.dto.TokenResponse;
import com.blog.som.domain.member.security.service.CustomUserDetailsService;
import com.blog.som.domain.member.type.Role;
import com.blog.som.global.exception.ErrorCode;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtTokenService {

  private final CustomUserDetailsService userDetailsService;

  @Value("${spring.jwt.secret}")
  private String secretKey;

  public TokenResponse generateTokenResponse(String email, Role role) {
    Claims claims = Jwts.claims().setSubject(email);
    claims.put(KEY_ROLES, this.getRoles(role));

    String accessToken = this.generateToken(claims, ACCESS_TOKEN_EXPIRE_TIME);
    String refreshToken = this.generateToken(claims, REFRESH_TOKEN_EXPIRE_TIME);

    return new TokenResponse(accessToken, refreshToken);
  }

  private String generateToken(Claims claims, long expiredTime) {
    Date now = new Date();
    Date expired = new Date(now.getTime() + expiredTime);
    return Jwts.builder()
        .setClaims(claims)
        .setIssuedAt(now)
        .setExpiration(expired)
        .signWith(SignatureAlgorithm.HS512, this.secretKey)
        .compact();
  }

  private List<String> getRoles(Role role) {
    List<String> roles = new ArrayList<>();
    roles.add("ROLE_" + role);
    return roles;
  }

  public String resolveTokenFromRequest(String token) {
    if (StringUtils.hasText(token) && token.startsWith(BEARER)) {
      log.info("resolve complete");
      return token.substring(BEARER.length());
    }
    log.info("resolve fail");
    return null;
  }

  public boolean validateToken(String token) {
    if (!StringUtils.hasText(token)) {
      return false;
    }
    //token의 만료 시간이 현재시간 보다 이후 일 때 true 반환
    return this.parseClaims(token).getExpiration().after(new Date());
  }

  public Authentication getAuthentication(String token) {
    String username = this.parseClaims(token).getSubject();
    UserDetails userDetails = userDetailsService.loadUserByUsername(username);
    return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
  }

  public String getUsernameByToken(String token){
    return this.parseClaims(token).getSubject();
  }

  /**
   * 토큰이 유효한지 확인
   */
  private Claims parseClaims(String token) {
    try {
      return Jwts.parser().setSigningKey(this.secretKey).parseClaimsJws(token).getBody();
    } catch (ExpiredJwtException e) {
      throw new JwtException(ErrorCode.TOKEN_TIME_OUT.getDescription());
    } catch (SignatureException e) {
      throw new JwtException(ErrorCode.JWT_TOKEN_WRONG_TYPE.getDescription());
    } catch (MalformedJwtException e) {
      throw new JwtException(ErrorCode.JWT_TOKEN_MALFORMED.getDescription());
    }
  }

}