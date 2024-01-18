package com.blog.som.domain.member.security.token;

public class TokenConstant {

  private TokenConstant(){}

  public static final String BEARER = "Bearer ";

  public static final String KEY_ROLES = "roles";

  public static final long ACCESS_TOKEN_EXPIRE_TIME = (long) 1000 * 60 * 60 * 24;// 24시간 (실제로는 30분정도로 수정해야함)

  public static final long REFRESH_TOKEN_EXPIRE_TIME = (long) 1000 * 60 * 60 * 24 * 30;// 한 달

  public static final String REFRESH_TOKEN_PREFIX = "RT-";

  public static final String ACCESS_TOKEN_BLACKLIST_PREFIX = "AT-";

  public static final String REFRESH_TOKEN_EMAIL_KEY_PREFIX = "RT-";


}
