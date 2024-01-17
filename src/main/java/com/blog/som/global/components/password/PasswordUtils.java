package com.blog.som.global.components.password;

import org.springframework.stereotype.Component;

@Component
public class PasswordUtils {

  private PasswordUtils(){}

  public static String encPassword(String plainText) {
    if (plainText == null || plainText.isEmpty()) {
      return "";
    }
    //TODO : Password Encoding
    return plainText;
  }
}
