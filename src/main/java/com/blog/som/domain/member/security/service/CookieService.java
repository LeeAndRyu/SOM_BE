package com.blog.som.domain.member.security.service;


import com.blog.som.domain.member.security.token.TokenConstant;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class CookieService {

  private final String TOKEN_COOKIE_NAME = "connect.sid";

  public void setCookieForLogin(HttpServletResponse response, String accessToken){
    Cookie cookie = new Cookie(TOKEN_COOKIE_NAME, accessToken);
    cookie.setHttpOnly(true);
    cookie.setPath("/");
    cookie.setMaxAge(Math.toIntExact(TokenConstant.ACCESS_TOKEN_EXPIRE_TIME));
    response.addCookie(cookie);
    log.info("setCookie : {}",cookie);
  }

  public void expireCookieForLogout(HttpServletResponse response){
    Cookie cookie = new Cookie(TOKEN_COOKIE_NAME, "");
    cookie.setHttpOnly(true);
    cookie.setPath("/");
    cookie.setMaxAge(0);
    response.addCookie(cookie);
    log.info("setCookie : {}", cookie);
  }
}
