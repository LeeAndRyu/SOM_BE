package com.blog.som.domain.likes.controller;

import com.blog.som.domain.likes.dto.LikesResponse.MemberLikesPost;
import com.blog.som.domain.likes.dto.LikesResponse.ToggleResult;
import com.blog.som.domain.likes.service.LikesService;
import com.blog.som.domain.member.security.userdetails.LoginMember;
import com.blog.som.domain.member.type.Role;
import com.blog.som.domain.post.mongo.service.MongoPostService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
  private final MongoPostService mongoPostService;

  @ApiOperation(value = "좋아요 누르기 / 누르기 취소", notes = "토글 형식으로 좋아요 누르기 / 취소가 반복된다.")
  @PostMapping("/post/{postId}/likes")
  public ResponseEntity<ToggleResult> toggleLikes(@PathVariable Long postId,
      @AuthenticationPrincipal LoginMember loginMember) {

    if (Role.UNAUTH.equals(loginMember.getRole())) {
      return ResponseEntity.ok(ToggleResult.unAuth());
    }

    ToggleResult toggleResult = likesService.toggleLikes(postId, loginMember.getMemberId());

    mongoPostService.updatePostDocumentLikes(toggleResult.getLikesStatus(), postId);

    return ResponseEntity.ok(toggleResult);
  }

  @ApiOperation("좋아요 여부 확인")
  @GetMapping("/post/{postId}/likes")
  public ResponseEntity<MemberLikesPost> memberLikesPost(@PathVariable Long postId,
      @AuthenticationPrincipal LoginMember loginMember) {

    if (Role.UNAUTH.equals(loginMember.getRole())) {
      return ResponseEntity.ok(MemberLikesPost.unAuth());
    }

    MemberLikesPost memberLikesPost = likesService.memberLikesPost(postId, loginMember.getMemberId());

    return ResponseEntity.ok(memberLikesPost);
  }

}
