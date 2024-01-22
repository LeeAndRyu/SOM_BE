package com.blog.som.global.components.mail;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class SendMailDto {

  private String email;
  private String code;

  public SendMailDto(String email) {
    this.email = email;
    this.code = UUID.randomUUID().toString();
  }
}
