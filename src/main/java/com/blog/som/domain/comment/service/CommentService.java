package com.blog.som.domain.comment.service;

import com.blog.som.domain.comment.dto.CommentDto;
import java.util.List;

public interface CommentService {

  CommentDto writeComment(Long postId, Long loginMemberId, String commentString);

  List<CommentDto> getComments(Long postId);

  CommentDto updateComment(Long commentId, Long loginMemberId, String commentString);

  CommentDto deleteComment(Long commentId, Long loginMemberId);

}
