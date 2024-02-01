package com.blog.som.global.util;

import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Component;

public class PasswordUtils {

  private PasswordUtils(){}

  public static boolean equalsPlainTextAndHashed(String plainText, String hashed) {
    if (plainText == null || plainText.isEmpty()) {
      return false;
    }
    if (hashed == null || hashed.isEmpty()) {
      return false;
    }

    return BCrypt.checkpw(plainText, hashed);
  }

  public static String encPassword(String plainText) {
    if (plainText == null || plainText.isEmpty()) {
      return "";
    }
    return BCrypt.hashpw(plainText, BCrypt.gensalt());
  }
}
