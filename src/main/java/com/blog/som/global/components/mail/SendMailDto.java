package com.blog.som.global.components.mail;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class SendMailDto {
    private String email;
    private String nickname;
    private String authKey;
}
