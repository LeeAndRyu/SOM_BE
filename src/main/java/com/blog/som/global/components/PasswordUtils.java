package com.blog.som.global.components;

import org.springframework.stereotype.Component;

@Component
public class PasswordUtils {

  private PasswordUtils(){}

  public static String encPassword(String plainText) {
    if (plainText == null || plainText.length() < 1) {
      return "";
    }
    //TODO : Password Encoding
    return plainText;
  }
}
