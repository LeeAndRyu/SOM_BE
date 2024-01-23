package com.blog.som.domain.post.controller;


import com.blog.som.domain.member.security.userdetails.LoginMember;
import com.blog.som.domain.post.dto.PostDto;
import com.blog.som.domain.post.dto.PostEditRequest;
import com.blog.som.domain.post.dto.PostWriteRequest;
import com.blog.som.domain.post.service.PostService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Api(tags = "게시글(post)")
@RequiredArgsConstructor
@RestController
public class PostController {

  private final PostService postService;

  @ApiOperation("게시글 작성")
  @PostMapping("/post")
  public ResponseEntity<PostDto> writePost(@RequestBody PostWriteRequest request,
      @AuthenticationPrincipal LoginMember loginMember) {

    PostDto postDto = postService.writePost(request, loginMember.getMemberId());

    return ResponseEntity.ok(postDto);
  }

  @ApiOperation("게시글 조회")
  @GetMapping("/post/{postId}")
  public ResponseEntity<PostDto> getPost(@PathVariable Long postId,
      @RequestHeader(value = "Custom-Access-User", defaultValue = "") String accessUserAgent){

    PostDto postDto = postService.getPost(postId, accessUserAgent);

    return ResponseEntity.ok(postDto);
  }

  @ApiOperation("게시글 수정")
  @PutMapping("/post/{postId}")
  public ResponseEntity<PostDto> editPost(@PathVariable Long postId,
      @RequestBody PostEditRequest postEditRequest,
      @AuthenticationPrincipal LoginMember loginMember){

    PostDto postDto = postService.editPost(postEditRequest, postId, loginMember.getMemberId());

    return ResponseEntity.ok(postDto);
  }

}
