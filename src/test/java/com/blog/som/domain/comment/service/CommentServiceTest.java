package com.blog.som.domain.comment.service;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import com.blog.som.EntityCreator;
import com.blog.som.domain.comment.dto.CommentDto;
import com.blog.som.domain.comment.entity.CommentEntity;
import com.blog.som.domain.comment.repository.CommentRepository;
import com.blog.som.domain.follow.repository.FollowRepository;
import com.blog.som.domain.member.entity.MemberEntity;
import com.blog.som.domain.member.repository.MemberRepository;
import com.blog.som.domain.notification.service.NotificationService;
import com.blog.som.domain.post.entity.PostEntity;
import com.blog.som.domain.post.repository.PostRepository;
import com.blog.som.global.exception.ErrorCode;
import com.blog.som.global.exception.custom.CommentException;
import com.blog.som.global.exception.custom.MemberException;
import com.blog.som.global.exception.custom.PostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@Slf4j
@ExtendWith(MockitoExtension.class)
class CommentServiceTest {

  @Mock
  private PostRepository postRepository;
  @Mock
  private MemberRepository memberRepository;
  @Mock
  private CommentRepository commentRepository;
  @Mock
  private NotificationService notificationService;

  @InjectMocks
  private CommentServiceImpl commentService;

  @Nested
  @DisplayName("댓글 작성")
  class WriteComment{
    @Test
    @DisplayName("성공")
    void writeComment(){
      MemberEntity member = EntityCreator.createMember(1L);
      PostEntity post = EntityCreator.createPost(10L, member);
      CommentEntity comment = EntityCreator.createComment(100L, member, post);
      //given
      when(postRepository.findById(10L))
          .thenReturn(Optional.of(post));
      when(memberRepository.findById(1L))
          .thenReturn(Optional.of(member));
      when(commentRepository.save(new CommentEntity(member, post, comment.getContent())))
          .thenReturn(comment);
      //when
      CommentDto commentDto = commentService.writeComment(10L, 1L, comment.getContent());

      //then
      assertThat(commentDto.getCommentId()).isEqualTo(100L);
      assertThat(commentDto.getContent()).isEqualTo(comment.getContent());
      assertThat(commentDto.getWriterId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("실패 : POST_NOT_FOUND")
    void writeComment_POST_NOT_FOUND(){
      MemberEntity member = EntityCreator.createMember(1L);
      PostEntity post = EntityCreator.createPost(10L, member);
      CommentEntity comment = EntityCreator.createComment(100L, member, post);
      //given
      when(postRepository.findById(10L))
          .thenReturn(Optional.empty());
      //when
      //then
      PostException postException = assertThrows(PostException.class,
          () -> commentService.writeComment(10L, 1L, comment.getContent()));
      assertThat(postException.getErrorCode()).isEqualTo(ErrorCode.POST_NOT_FOUND);
    }

    @Test
    @DisplayName("실패 : MEMBER_NOT_FOUND")
    void writeComment_MEMBER_NOT_FOUND(){
      MemberEntity member = EntityCreator.createMember(1L);
      PostEntity post = EntityCreator.createPost(10L, member);
      CommentEntity comment = EntityCreator.createComment(100L, member, post);
      //given
      when(postRepository.findById(10L))
          .thenReturn(Optional.of(post));
      when(memberRepository.findById(1L))
          .thenReturn(Optional.empty());

      MemberException memberException = assertThrows(MemberException.class,
          () -> commentService.writeComment(10L, 1L, comment.getContent()));
      assertThat(memberException.getErrorCode()).isEqualTo(ErrorCode.MEMBER_NOT_FOUND);
    }
  }

  @Nested
  @DisplayName("댓글 리스트 조회")
  class GetComments{
    @Test
    @DisplayName("성공")
    void getComments(){
      MemberEntity member = EntityCreator.createMember(1L);
      PostEntity post = EntityCreator.createPost(10L, member);

      List<CommentEntity> comments = new ArrayList<>();
      for(int i = 0; i < 10; i++){
        comments.add(EntityCreator.createComment(100L + i, member, post));
      }

      //given
      when(postRepository.findById(10L))
          .thenReturn(Optional.of(post));
      when(commentRepository.findByPostOrderByRegisteredAtDesc(post))
          .thenReturn(comments);

      //when
      List<CommentDto> result = commentService.getComments(10L);

      //then
      for (CommentDto commentDto : result) {
        assertThat(commentDto.getPostId()).isEqualTo(10L);
      }
    }

    @Test
    @DisplayName("실패 : POST_NOT_FOUND")
    void getComments_POST_NOT_FOUND(){
      MemberEntity member = EntityCreator.createMember(1L);
      PostEntity post = EntityCreator.createPost(10L, member);

      List<CommentEntity> comments = new ArrayList<>();
      for(int i = 0; i < 10; i++){
        comments.add(EntityCreator.createComment(100L + i, member, post));
      }

      //given
      when(postRepository.findById(10L))
          .thenReturn(Optional.empty());

      //when
      //then
      PostException postException = assertThrows(PostException.class,
          () -> commentService.getComments(10L));
      assertThat(postException.getErrorCode()).isEqualTo(ErrorCode.POST_NOT_FOUND);
    }
  }

  @Nested
  @DisplayName("댓글 수정")
  class UpdateComment{
    @Test
    @DisplayName("성공")
    void updateComment(){
      MemberEntity writer = EntityCreator.createMember(1L);
      PostEntity post = EntityCreator.createPost(10L, writer);
      CommentEntity comment = EntityCreator.createComment(100L, writer, post);
      String updatedComment = "updated comment";
      //given
      when(commentRepository.findById(100L))
          .thenReturn(Optional.of(comment));
      when(commentRepository.save(comment))
          .thenReturn(comment);

      //when
      CommentDto commentDto = commentService.updateComment(100L, 1L, updatedComment);

      //then
      assertThat(commentDto.getContent()).isEqualTo(updatedComment) ;
    }

    @Test
    @DisplayName("실패 : COMMENT_UPDATE_NO_AUTHORITY")
    void updateComment_COMMENT_UPDATE_NO_AUTHORITY(){
      MemberEntity writer = EntityCreator.createMember(1L);
      MemberEntity whoIsNotWriter = EntityCreator.createMember(2L);
      PostEntity post = EntityCreator.createPost(10L, writer);
      CommentEntity comment = EntityCreator.createComment(100L, writer, post);
      String updatedComment = "updated comment";

      //given
      when(commentRepository.findById(100L))
          .thenReturn(Optional.of(comment));

      //when
      verify(commentRepository, never()).save(comment);
      CommentException commentException = assertThrows(CommentException.class,
          () -> commentService.updateComment(100L, 2L, updatedComment));
      assertThat(commentException.getErrorCode()).isEqualTo(ErrorCode.COMMENT_UPDATE_NO_AUTHORITY);
    }

  }

  @Nested
  @DisplayName("댓글 삭제")
  class DeleteComment{
    @Test
    @DisplayName("성공")
    void deleteComment(){
      MemberEntity writer = EntityCreator.createMember(1L);
      PostEntity post = EntityCreator.createPost(10L, writer);
      CommentEntity comment = EntityCreator.createComment(100L, writer, post);
      //given
      when(commentRepository.findById(100L))
          .thenReturn(Optional.of(comment));

      //when
      CommentDto commentDto = commentService.deleteComment(100L, 1L);

      //then
      verify(commentRepository, times(1)).delete(comment);
      verify(postRepository, times(1)).save(post);
      assertThat(commentDto.getCommentId()).isEqualTo(100L);
    }

    @Test
    @DisplayName("실패 : COMMENT_NOT_FOUND")
    void deleteComment_COMMENT_NOT_FOUND(){
      MemberEntity writer = EntityCreator.createMember(1L);
      PostEntity post = EntityCreator.createPost(10L, writer);
      CommentEntity comment = EntityCreator.createComment(100L, writer, post);

      //given
      when(commentRepository.findById(100L))
          .thenReturn(Optional.empty());
      //when
      //then
      CommentException commentException = assertThrows(CommentException.class,
          () -> commentService.deleteComment(100L, 1L));
      assertThat(commentException.getErrorCode()).isEqualTo(ErrorCode.COMMENT_NOT_FOUND);
    }

    @Test
    @DisplayName("실패 : COMMENT_DELETE_NO_AUTHORITY")
    void deleteComment_COMMENT_DELETE_NO_AUTHORITY(){
      MemberEntity writer = EntityCreator.createMember(1L);
      PostEntity post = EntityCreator.createPost(10L, writer);
      CommentEntity comment = EntityCreator.createComment(100L, writer, post);
      //given
      when(commentRepository.findById(100L))
          .thenReturn(Optional.of(comment));

      //when
      //then
      CommentException commentException = assertThrows(CommentException.class,
          () -> commentService.deleteComment(100L, 2L));
      assertThat(commentException.getErrorCode()).isEqualTo(ErrorCode.COMMENT_DELETE_NO_AUTHORITY);
      verify(commentRepository, never()).delete(comment);
      verify(postRepository, never()).save(post);
    }

  }

}