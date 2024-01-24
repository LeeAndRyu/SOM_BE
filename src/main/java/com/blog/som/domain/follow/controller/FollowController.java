package com.blog.som.domain.follow.controller;

import com.blog.som.domain.follow.dto.FollowDto;
import com.blog.som.domain.follow.service.FollowService;
import com.blog.som.domain.member.security.userdetails.LoginMember;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Api(tags = "게시글(post)")
@RequiredArgsConstructor
@RestController
public class FollowController {

  private final FollowService followService;

  @ApiOperation("팔로우 하기")
  @PreAuthorize("hasAnyRole('ROLE_USER')")
  @GetMapping("/follow/{accountName}")
  public ResponseEntity<FollowDto> doFollow(@PathVariable String accountName,
      @AuthenticationPrincipal LoginMember loginMember){

    FollowDto follow = followService.doFollow(loginMember.getMemberId(), accountName);

    return ResponseEntity.ok(follow);
  }


}
