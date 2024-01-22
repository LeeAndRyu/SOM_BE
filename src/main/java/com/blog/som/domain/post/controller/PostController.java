package com.blog.som.domain.post.controller;


import com.blog.som.domain.member.security.userdetails.LoginMember;
import com.blog.som.domain.post.dto.PostWriteRequest;
import com.blog.som.domain.post.service.PostService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = "게시글(post)")
@RequiredArgsConstructor
@RestController
public class PostController {

  private final PostService postService;

  @ApiOperation("게시글 작성")
  @GetMapping("/write")
  public ResponseEntity<?> writePost(@RequestBody PostWriteRequest request,
      @AuthenticationPrincipal LoginMember loginMember) {

    return ResponseEntity.ok(null);
  }

}
