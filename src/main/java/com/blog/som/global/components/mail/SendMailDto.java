package com.blog.som.global.components.mail;

import com.blog.som.domain.member.entity.MemberEntity;
import java.util.UUID;
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

    public SendMailDto(MemberEntity member) {
        this.email = member.getEmail();
        this.nickname = member.getNickname();
        this.authKey = UUID.randomUUID().toString();
    }
}
