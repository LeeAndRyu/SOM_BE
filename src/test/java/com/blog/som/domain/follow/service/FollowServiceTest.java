package com.blog.som.domain.follow.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.any;
import static org.mockito.BDDMockito.never;
import static org.mockito.BDDMockito.times;
import static org.mockito.BDDMockito.verify;
import static org.mockito.BDDMockito.when;

import com.blog.som.EntityCreator;
import com.blog.som.domain.follow.dto.FollowCancelResponse;
import com.blog.som.domain.follow.dto.FollowDto;
import com.blog.som.domain.follow.entity.FollowEntity;
import com.blog.som.domain.follow.repository.FollowRepository;
import com.blog.som.domain.member.entity.MemberEntity;
import com.blog.som.domain.member.repository.MemberRepository;
import com.blog.som.global.constant.ResponseConstant;
import com.blog.som.global.exception.ErrorCode;
import com.blog.som.global.exception.custom.BlogException;
import com.blog.som.global.exception.custom.FollowException;
import com.blog.som.global.exception.custom.MemberException;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@Slf4j
@ExtendWith(MockitoExtension.class)
class FollowServiceTest {

  @Mock
  private FollowRepository followRepository;
  @Mock
  private MemberRepository memberRepository;

  @InjectMocks
  private FollowServiceImpl followService;


  @Nested
  @DisplayName("팔로우 하기")
  class DoFollow {

    @Test
    @DisplayName("성공")
    void doFollow() {
      MemberEntity fromMember = EntityCreator.createMember(1L);
      MemberEntity toMember = EntityCreator.createMember(2L);
      FollowEntity followEntity = EntityCreator.createFollowEntity(10L, fromMember, toMember);

      //given
      when(memberRepository.findById(1L))
          .thenReturn(Optional.of(fromMember));
      when(memberRepository.findByAccountName(toMember.getAccountName()))
          .thenReturn(Optional.of(toMember));
      when(followRepository.existsByFromMemberAndToMember(fromMember, toMember))
          .thenReturn(false);
      when(followRepository.save(new FollowEntity(fromMember, toMember)))
          .thenReturn(followEntity);
      //when
      FollowDto followDto = followService.doFollow(1L, toMember.getAccountName());

      //then
      verify(memberRepository, times(2)).save(any(MemberEntity.class));
      assertThat(followDto.getFromMemberId()).isEqualTo(1L);
      assertThat(followDto.getToMemberId()).isEqualTo(2L);
      assertThat(followDto.getToMemberBlogName()).isEqualTo(toMember.getBlogName());
    }

    @Test
    @DisplayName("실패 : MEMBER_NOT_FOUND")
    void doFollow_MEMBER_NOT_FOUND() {
      MemberEntity fromMember = EntityCreator.createMember(1L);
      MemberEntity toMember = EntityCreator.createMember(2L);
      FollowEntity followEntity = EntityCreator.createFollowEntity(10L, fromMember, toMember);

      //given
      when(memberRepository.findById(1L))
          .thenReturn(Optional.empty());

      //when
      //then
      MemberException memberException = assertThrows(MemberException.class,
          () -> followService.doFollow(1L, toMember.getAccountName()));
      assertThat(memberException.getErrorCode()).isEqualTo(ErrorCode.MEMBER_NOT_FOUND);
    }

    @Test
    @DisplayName("실패 : BLOG_NOT_FOUND")
    void doFollow_BLOG_NOT_FOUND() {
      MemberEntity fromMember = EntityCreator.createMember(1L);
      MemberEntity toMember = EntityCreator.createMember(2L);
      FollowEntity followEntity = EntityCreator.createFollowEntity(10L, fromMember, toMember);

      //given
      when(memberRepository.findById(1L))
          .thenReturn(Optional.of(fromMember));
      when(memberRepository.findByAccountName(toMember.getAccountName()))
          .thenReturn(Optional.empty());

      //when
      //then
      BlogException blogException = assertThrows(BlogException.class,
          () -> followService.doFollow(1L, toMember.getAccountName()));
      verify(memberRepository, never()).save(fromMember);
      assertThat(blogException.getErrorCode()).isEqualTo(ErrorCode.BLOG_NOT_FOUND);
    }

    @Test
    @DisplayName("실패 : ALREADY_FOLLOWED")
    void doFollow_ALREADY_FOLLOWED() {
      MemberEntity fromMember = EntityCreator.createMember(1L);
      MemberEntity toMember = EntityCreator.createMember(2L);
      FollowEntity followEntity = EntityCreator.createFollowEntity(10L, fromMember, toMember);

      //given
      when(memberRepository.findById(1L))
          .thenReturn(Optional.of(fromMember));
      when(memberRepository.findByAccountName(toMember.getAccountName()))
          .thenReturn(Optional.of(toMember));
      when(followRepository.existsByFromMemberAndToMember(fromMember, toMember))
          .thenReturn(true);

      //when
      //then
      FollowException followException = assertThrows(FollowException.class,
          () -> followService.doFollow(1L, toMember.getAccountName()));
      verify(memberRepository, never()).save(fromMember);
      assertThat(followException.getErrorCode()).isEqualTo(ErrorCode.ALREADY_FOLLOWED);
    }
  }

  @Nested
  @DisplayName("팔로우 취소")
  class CancelFollow {

    @Test
    @DisplayName("성공")
    void cancelFollow() {
      MemberEntity fromMember = EntityCreator.createMember(1L);
      MemberEntity toMember = EntityCreator.createMember(2L);
      FollowEntity followEntity = EntityCreator.createFollowEntity(10L, fromMember, toMember);

      //given
      when(memberRepository.findById(1L))
          .thenReturn(Optional.of(fromMember));
      when(memberRepository.findByAccountName(toMember.getAccountName()))
          .thenReturn(Optional.of(toMember));
      when(followRepository.findByFromMemberAndToMember(fromMember, toMember))
          .thenReturn(Optional.of(followEntity));

      //when
      FollowCancelResponse response = followService.cancelFollow(1L, toMember.getAccountName());

      //then
      verify(followRepository, times(1)).delete(followEntity);
      verify(memberRepository, times(2)).save(any(MemberEntity.class));
      assertThat(response.getFromMemberId()).isEqualTo(fromMember.getMemberId());
      assertThat(response.getToMemberId()).isEqualTo(toMember.getMemberId());
      assertThat(response.getMessage()).isEqualTo(ResponseConstant.FOLLOW_CANCEL_COMPLETE);
    }

    @Test
    @DisplayName("실패 : MEMBER_NOT_FOUND")
    void cancelFollow_MEMBER_NOT_FOUND() {
      MemberEntity fromMember = EntityCreator.createMember(1L);
      MemberEntity toMember = EntityCreator.createMember(2L);
      FollowEntity followEntity = EntityCreator.createFollowEntity(10L, fromMember, toMember);

      //given
      when(memberRepository.findById(1L))
          .thenReturn(Optional.empty());

      //when
      //then
      MemberException memberException = assertThrows(MemberException.class,
          () -> followService.cancelFollow(1L, toMember.getAccountName()));
      assertThat(memberException.getErrorCode()).isEqualTo(ErrorCode.MEMBER_NOT_FOUND);
    }

    @Test
    @DisplayName("실패 : BLOG_NOT_FOUND")
    void cancelFollow_BLOG_NOT_FOUND() {
      MemberEntity fromMember = EntityCreator.createMember(1L);
      MemberEntity toMember = EntityCreator.createMember(2L);
      FollowEntity followEntity = EntityCreator.createFollowEntity(10L, fromMember, toMember);

      //given
      when(memberRepository.findById(1L))
          .thenReturn(Optional.of(fromMember));
      when(memberRepository.findByAccountName(toMember.getAccountName()))
          .thenReturn(Optional.empty());

      //when
      //then
      BlogException blogException = assertThrows(BlogException.class,
          () -> followService.cancelFollow(1L, toMember.getAccountName()));
      verify(memberRepository, never()).save(fromMember);
      assertThat(blogException.getErrorCode()).isEqualTo(ErrorCode.BLOG_NOT_FOUND);
    }

    @Test
    @DisplayName("실패 : FOLLOW_NOT_FOUND")
    void cancelFollow_FOLLOW_NOT_FOUND() {
      MemberEntity fromMember = EntityCreator.createMember(1L);
      MemberEntity toMember = EntityCreator.createMember(2L);
      FollowEntity followEntity = EntityCreator.createFollowEntity(10L, fromMember, toMember);

      //given
      when(memberRepository.findById(1L))
          .thenReturn(Optional.of(fromMember));
      when(memberRepository.findByAccountName(toMember.getAccountName()))
          .thenReturn(Optional.of(toMember));
      when(followRepository.findByFromMemberAndToMember(fromMember, toMember))
          .thenReturn(Optional.empty());

      //when
      //then
      FollowException followException = assertThrows(FollowException.class,
          () -> followService.cancelFollow(1L, toMember.getAccountName()));
      verify(memberRepository, never()).save(fromMember);
      assertThat(followException.getErrorCode()).isEqualTo(ErrorCode.FOLLOW_NOT_FOUND);
    }
  }

}