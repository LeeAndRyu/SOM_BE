package com.blog.som.domain.likes.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.when;

import com.blog.som.EntityCreator;
import com.blog.som.domain.likes.constant.LikesConstant;
import com.blog.som.domain.likes.dto.LikesResponse;
import com.blog.som.domain.likes.dto.LikesResponse.ToggleResult;
import com.blog.som.domain.likes.entity.LikesEntity;
import com.blog.som.domain.likes.repository.LikesRepository;
import com.blog.som.domain.likes.type.LikesStatus;
import com.blog.som.domain.member.entity.MemberEntity;
import com.blog.som.domain.member.repository.MemberRepository;
import com.blog.som.domain.notification.service.NotificationService;
import com.blog.som.domain.post.entity.PostEntity;
import com.blog.som.domain.post.repository.PostRepository;
import com.blog.som.global.exception.ErrorCode;
import com.blog.som.global.exception.custom.MemberException;
import com.blog.som.global.exception.custom.PostException;
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
class LikesServiceTest {

  @Mock
  private PostRepository postRepository;
  @Mock
  private MemberRepository memberRepository;
  @Mock
  private LikesRepository likesRepository;
  @Mock
  private NotificationService notificationService;

  @InjectMocks
  private LikesServiceImpl likesService;

  @Nested
  @DisplayName("좋아요 토글 - 누르기 / 취소")
  class ToggleLikes {

    @Test
    @DisplayName("좋아요 누르기")
    void toggleLikes_do_likes() {
      MemberEntity member = EntityCreator.createMember(1L);
      PostEntity post = EntityCreator.createPost(10L, member);
      LikesEntity likes = EntityCreator.createLikes(100L, member, post);

      //given
      when(postRepository.findById(10L))
          .thenReturn(Optional.of(post));
      when(memberRepository.findById(1L))
          .thenReturn(Optional.of(member));
      when(likesRepository.findByMemberAndPost(member, post))
          .thenReturn(Optional.empty());

      //when
      ToggleResult toggleResult = likesService.toggleLikes(10L, 1L);

      //then
      assertThat(toggleResult.getLikesStatus()).isEqualTo(LikesStatus.LIKES);
      assertThat(toggleResult.getMemberId()).isEqualTo(1L);
      assertThat(toggleResult.getPostId()).isEqualTo(10L);
      assertThat(toggleResult.getMessage()).isEqualTo(LikesConstant.LIKES_COMPLETE);

      assertThat(post.getLikes()).isEqualTo(1);
    }

    @Test
    @DisplayName("좋아요 취소")
    void toggleLikes_cancel_likes() {
      MemberEntity member = EntityCreator.createMember(1L);
      PostEntity post = EntityCreator.createPost(10L, member);
      LikesEntity likes = EntityCreator.createLikes(100L, member, post);
      post.setLikes(1);

      //given
      when(postRepository.findById(10L))
          .thenReturn(Optional.of(post));
      when(memberRepository.findById(1L))
          .thenReturn(Optional.of(member));
      when(likesRepository.findByMemberAndPost(member, post))
          .thenReturn(Optional.of(likes));

      //when
      ToggleResult toggleResult = likesService.toggleLikes(10L, 1L);

      //then
      assertThat(toggleResult.getLikesStatus()).isEqualTo(LikesStatus.NOT_LIKES);
      assertThat(toggleResult.getMemberId()).isEqualTo(1L);
      assertThat(toggleResult.getPostId()).isEqualTo(10L);
      assertThat(toggleResult.getMessage()).isEqualTo(LikesConstant.LIKES_CANCELED);

      assertThat(post.getLikes()).isEqualTo(0);
    }

    @Test
    @DisplayName("실패 : POST_NOT_FOUND")
    void toggleLikes_POST_NOT_FOUND() {
      MemberEntity member = EntityCreator.createMember(1L);
      PostEntity post = EntityCreator.createPost(10L, member);

      //given
      when(postRepository.findById(10L))
          .thenReturn(Optional.empty());

      //when
      //then
      PostException postException = assertThrows(PostException.class,
          () -> likesService.toggleLikes(10L, 1L));

      assertThat(postException.getErrorCode()).isEqualTo(ErrorCode.POST_NOT_FOUND);
    }

    @Test
    @DisplayName("실패 : MEMBER_NOT_FOUND")
    void toggleLikes_MEMBER_NOT_FOUND() {
      MemberEntity member = EntityCreator.createMember(1L);
      PostEntity post = EntityCreator.createPost(10L, member);
      LikesEntity likes = EntityCreator.createLikes(100L, member, post);

      //given
      when(postRepository.findById(10L))
          .thenReturn(Optional.of(post));
      when(memberRepository.findById(1L))
          .thenReturn(Optional.empty());

      //when
      //then
      MemberException memberException = assertThrows(MemberException.class,
          () -> likesService.toggleLikes(10L, 1L));

      assertThat(memberException.getErrorCode()).isEqualTo(ErrorCode.MEMBER_NOT_FOUND);
    }
  }

  @Nested
  @DisplayName("좋아요 누름 여부 조회")
  class MemberLikesPost {

    @Test
    @DisplayName("성공 - LIKES_EXISTS")
    void memberLikesPost_LIKES_EXISTS() {
      MemberEntity member = EntityCreator.createMember(1L);
      PostEntity post = EntityCreator.createPost(10L, member);

      //given
      when(postRepository.findById(10L))
          .thenReturn(Optional.of(post));
      when(memberRepository.findById(1L))
          .thenReturn(Optional.of(member));
      when(likesRepository.existsByMemberAndPost(member, post))
          .thenReturn(true);

      //when
      LikesResponse.MemberLikesPost result = likesService.memberLikesPost(10L, 1L);

      //then
      assertThat(result.getLikesStatus()).isEqualTo(LikesStatus.LIKES);
      assertThat(result.getMemberId()).isEqualTo(1L);
      assertThat(result.getPostId()).isEqualTo(10L);
      assertThat(result.getMessage()).isEqualTo(LikesConstant.LIKES_EXISTS);
    }

    @Test
    @DisplayName("성공 - LIKES_DOESNT_EXISTS")
    void memberLikesPost_LIKES_DOESNT_EXISTS() {
      MemberEntity member = EntityCreator.createMember(1L);
      PostEntity post = EntityCreator.createPost(10L, member);

      //given
      when(postRepository.findById(10L))
          .thenReturn(Optional.of(post));
      when(memberRepository.findById(1L))
          .thenReturn(Optional.of(member));
      when(likesRepository.existsByMemberAndPost(member, post))
          .thenReturn(false);

      //when
      LikesResponse.MemberLikesPost result = likesService.memberLikesPost(10L, 1L);

      //then
      assertThat(result.getLikesStatus()).isEqualTo(LikesStatus.NOT_LIKES);
      assertThat(result.getMemberId()).isEqualTo(1L);
      assertThat(result.getPostId()).isEqualTo(10L);
      assertThat(result.getMessage()).isEqualTo(LikesConstant.LIKES_DOESNT_EXISTS);
    }

    @Test
    @DisplayName("실패 : POST_NOT_FOUND")
    void toggleLikes_POST_NOT_FOUND() {
      //given
      when(postRepository.findById(10L))
          .thenReturn(Optional.empty());

      //when
      //then
      PostException postException = assertThrows(PostException.class,
          () -> likesService.toggleLikes(10L, 1L));

      assertThat(postException.getErrorCode()).isEqualTo(ErrorCode.POST_NOT_FOUND);
    }

    @Test
    @DisplayName("실패 : MEMBER_NOT_FOUND")
    void toggleLikes_MEMBER_NOT_FOUND() {
      MemberEntity member = EntityCreator.createMember(1L);
      PostEntity post = EntityCreator.createPost(10L, member);

      //given
      when(postRepository.findById(10L))
          .thenReturn(Optional.of(post));
      when(memberRepository.findById(1L))
          .thenReturn(Optional.empty());

      //when
      //then
      MemberException memberException = assertThrows(MemberException.class,
          () -> likesService.memberLikesPost(10L, 1L));

      assertThat(memberException.getErrorCode()).isEqualTo(ErrorCode.MEMBER_NOT_FOUND);
    }
  }

}