package com.blog.som.domain.comment.controller;

import com.blog.som.domain.comment.dto.CommentDto;
import com.blog.som.domain.comment.dto.CommentInput;
import com.blog.som.domain.comment.service.CommentService;
import com.blog.som.domain.member.security.userdetails.LoginMember;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Api(tags = "댓글(comment)")
@RequiredArgsConstructor
@RestController
public class CommentController {

  private final CommentService commentService;

  @ApiOperation("댓글 작성")
  @PreAuthorize("hasAnyRole('ROLE_USER')")
  @PostMapping("/post/{postId}/comment")
  public ResponseEntity<CommentDto> writeComment(@PathVariable Long postId,
      @RequestBody CommentInput input,
      @AuthenticationPrincipal LoginMember loginMember) {

    CommentDto result = commentService.writeComment(postId, loginMember.getMemberId(), input.getContent());

    return ResponseEntity.ok(result);
  }

  @ApiOperation("댓글 조회")
  @GetMapping("/post/{postId}/comment")
  public ResponseEntity<List<CommentDto>> getComments(@PathVariable Long postId) {

    List<CommentDto> comments = commentService.getComments(postId);

    return ResponseEntity.ok(comments);
  }

  @ApiOperation("댓글 수정")
  @PreAuthorize("hasAnyRole('ROLE_USER')")
  @PutMapping("/comment/{commentId}")
  public ResponseEntity<CommentDto> updateComment(@PathVariable Long commentId,
      @RequestBody CommentInput input,
      @AuthenticationPrincipal LoginMember loginMember) {

    CommentDto result =
        commentService.updateComment(commentId, loginMember.getMemberId(), input.getContent());

    return ResponseEntity.ok(result);
  }

  @ApiOperation("댓글 삭제")
  @PreAuthorize("hasAnyRole('ROLE_USER')")
  @DeleteMapping("/comment/{commentId}")
  public ResponseEntity<CommentDto> deleteComment(@PathVariable Long commentId,
      @AuthenticationPrincipal LoginMember loginMember) {

    CommentDto result = commentService.deleteComment(commentId, loginMember.getMemberId());

    return ResponseEntity.ok(result);
  }

}


