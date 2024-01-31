package com.blog.som.domain.likes.controller;

import com.blog.som.domain.likes.dto.LikesResponse;
import com.blog.som.domain.likes.service.LikesService;
import com.blog.som.domain.member.security.userdetails.LoginMember;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Api(tags = "좋아요(likes)")
@RequiredArgsConstructor
@RestController
public class LikesController {

  private final LikesService likesService;

  @ApiOperation("좋아요 누르기")
  @PreAuthorize("hasAnyRole('ROLE_USER')")
  @PostMapping("/post/{postId}/likes")
  public ResponseEntity<LikesResponse> doLikes(@PathVariable Long postId,
      @AuthenticationPrincipal LoginMember loginMember) {
    LikesResponse likesResponse = likesService.doLikes(postId, loginMember.getMemberId());
    return ResponseEntity.ok(likesResponse);
  }

  @ApiOperation("좋아요 취소")
  @PreAuthorize("hasAnyRole('ROLE_USER')")
  @DeleteMapping("/post/{postId}/likes")
  public ResponseEntity<LikesResponse> cancelLikes(@PathVariable Long postId,
      @AuthenticationPrincipal LoginMember loginMember) {
    LikesResponse likesResponse = likesService.cancelLikes(postId, loginMember.getMemberId());
    return ResponseEntity.ok(likesResponse);
  }

  @ApiOperation("좋아요 여부 확인")
  @GetMapping("/post/{postId}/likes")
  public ResponseEntity<LikesResponse> memberLikesPost(@PathVariable Long postId,
      @AuthenticationPrincipal LoginMember loginMember) {
    LikesResponse likesResponse = likesService.memberLikesPost(postId, loginMember.getMemberId());
    return ResponseEntity.ok(likesResponse);
  }

}
