package com.blog.som.domain.member.dto;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MemberEditRequest {
  private String nickname;

  private String phoneNumber;

  private LocalDate birthDate;

}
