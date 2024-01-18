package com.blog.som.domain.member.security.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import com.blog.som.EntityCreator;
import com.blog.som.domain.member.entity.MemberEntity;
import com.blog.som.domain.member.repository.MemberRepository;
import com.blog.som.global.exception.ErrorCode;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@Slf4j
@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {

  @Mock
  private MemberRepository memberRepository;

  @InjectMocks
  private CustomUserDetailsService userDetailsService;

  @Nested
  @DisplayName("loadUserByUsername")
  class LoadUserByUsername{
    @Test
    @DisplayName("성공")
    void loadUserByUsername(){
      MemberEntity member = EntityCreator.createMember(1L);
      String username = member.getEmail();
      //given
      when(memberRepository.findByEmail(username))
          .thenReturn(Optional.of(member));

      //when
      UserDetails userDetails = userDetailsService.loadUserByUsername(username);

      //then
      assertThat(userDetails.getUsername()).isEqualTo(username);
      assertThat(userDetails.getPassword()).isEqualTo(member.getPassword());


    }

    @Test
    @DisplayName("실패 : UsernameNotFoundException")
    void loadUserByUsername_UsernameNotFoundException(){
      MemberEntity member = EntityCreator.createMember(1L);
      String username = member.getEmail();
      //given
      when(memberRepository.findByEmail(username))
          .thenReturn(Optional.empty());

      //when
      //then
      UsernameNotFoundException usernameNotFoundException =
          assertThrows(UsernameNotFoundException.class, () -> userDetailsService.loadUserByUsername(username));
      assertThat(usernameNotFoundException.getMessage()).isEqualTo(ErrorCode.MEMBER_NOT_FOUND.getDescription());
    }
  }
}