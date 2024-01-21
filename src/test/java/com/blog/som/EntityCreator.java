package com.blog.som;

import com.blog.som.domain.member.entity.MemberEntity;
import com.blog.som.domain.member.type.Role;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class EntityCreator {

  public static MemberEntity createMember(Long id) {
    return MemberEntity.builder()
        .memberId(id)
        .email("test" + id + "@test.com")
        .password("password" + id)
        .nickname("nickname" + id)
        .phoneNumber("010" + id + "00" + "5678")
        .birthDate(LocalDate.now())
        .profileImage("test-profile-image" + id + ".jpg")
        .registeredAt(LocalDateTime.now())
        .role(Role.USER)
        .build();
  }
}
