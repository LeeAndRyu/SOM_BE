package com.blog.som.domain.comment.service;

import com.blog.som.domain.comment.dto.CommentDto;
import com.blog.som.domain.comment.entity.CommentEntity;
import com.blog.som.domain.comment.repository.CommentRepository;
import com.blog.som.domain.member.entity.MemberEntity;
import com.blog.som.domain.member.repository.MemberRepository;
import com.blog.som.domain.notification.dto.NotificationCreateDto;
import com.blog.som.domain.notification.service.NotificationService;
import com.blog.som.domain.post.entity.PostEntity;
import com.blog.som.domain.post.repository.PostRepository;
import com.blog.som.global.exception.ErrorCode;
import com.blog.som.global.exception.custom.CommentException;
import com.blog.som.global.exception.custom.MemberException;
import com.blog.som.global.exception.custom.PostException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class CommentServiceImpl implements CommentService {

  private final PostRepository postRepository;
  private final MemberRepository memberRepository;
  private final CommentRepository commentRepository;
  private final NotificationService notificationService;

  @Override
  public CommentDto writeComment(Long postId, Long loginMemberId, String commentString) {
    PostEntity post = postRepository.findById(postId)
        .orElseThrow(() -> new PostException(ErrorCode.POST_NOT_FOUND));
    MemberEntity member = memberRepository.findById(loginMemberId)
        .orElseThrow(() -> new MemberException(ErrorCode.MEMBER_NOT_FOUND));

    CommentEntity comment = commentRepository.save(new CommentEntity(member, post, commentString));

    post.addComments();
    postRepository.save(post);

    notificationService.notify(
        post.getMember(), member,
        NotificationCreateDto.comment(member.getNickname(), post, comment));

    return CommentDto.fromEntity(comment);
  }

  @Override
  public List<CommentDto> getComments(Long postId) {
    PostEntity post = postRepository.findById(postId)
        .orElseThrow(() -> new PostException(ErrorCode.POST_NOT_FOUND));

    List<CommentEntity> commentList = commentRepository.findByPostOrderByRegisteredAtDesc(post);

    return commentList.stream()
        .map(CommentDto::fromEntity)
        .toList();
  }

  @Override
  public CommentDto updateComment(Long commentId, Long loginMemberId, String commentString) {
    CommentEntity comment = commentRepository.findById(commentId)
        .orElseThrow(() -> new CommentException(ErrorCode.COMMENT_NOT_FOUND));

    if (!comment.getMember().getMemberId().equals(loginMemberId)) {
      throw new CommentException(ErrorCode.COMMENT_UPDATE_NO_AUTHORITY);
    }

    comment.setContent(commentString);
    CommentEntity saved = commentRepository.save(comment);

    return CommentDto.fromEntity(saved);
  }

  @Override
  public CommentDto deleteComment(Long commentId, Long loginMemberId) {
    CommentEntity comment = commentRepository.findById(commentId)
        .orElseThrow(() -> new CommentException(ErrorCode.COMMENT_NOT_FOUND));

    //댓글 작성자 또는 게시글 작성자
    if (comment.getMember().getMemberId().equals(loginMemberId) ||
        comment.getPost().getMember().getMemberId().equals(loginMemberId)) {
      commentRepository.delete(comment);

      PostEntity post = comment.getPost();
      post.minusComments();
      postRepository.save(post);

      return CommentDto.fromEntity(comment);
    }

    throw new CommentException(ErrorCode.COMMENT_DELETE_NO_AUTHORITY);
  }
}
