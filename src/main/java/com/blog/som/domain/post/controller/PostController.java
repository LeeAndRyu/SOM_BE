package com.blog.som.domain.post.controller;


import com.blog.som.domain.member.security.userdetails.LoginMember;
import com.blog.som.domain.post.dto.PostDeleteResponse;
import com.blog.som.domain.post.dto.PostDto;
import com.blog.som.domain.post.dto.PostEditRequest;
import com.blog.som.domain.post.dto.PostWriteRequest;
import com.blog.som.domain.post.service.PostService;
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
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Api(tags = "게시글(post)")
@RequiredArgsConstructor
@RestController
public class PostController {

  private final PostService postService;

  @ApiOperation("게시글 작성")
  @PreAuthorize("hasAnyRole('ROLE_USER')")
  @PostMapping("/post")
  public ResponseEntity<PostDto> writePost(@RequestBody PostWriteRequest request,
      @AuthenticationPrincipal LoginMember loginMember) {

    PostDto postDto = postService.writePost(request, loginMember.getMemberId());

    return ResponseEntity.ok(postDto);
  }

  @ApiOperation("게시글 조회")
  @GetMapping("/post/{postId}")
  public ResponseEntity<PostDto> getPost(@PathVariable Long postId,
      @RequestHeader(value = "Custom-Access-User", required = false, defaultValue = "") String accessUserAgent) {

    PostDto postDto = postService.getPost(postId, accessUserAgent);

    return ResponseEntity.ok(postDto);
  }

  @ApiOperation("게시글 수정")
  @PreAuthorize("hasAnyRole('ROLE_USER')")
  @PutMapping("/post/{postId}")
  public ResponseEntity<PostDto> editPost(@PathVariable Long postId,
      @RequestBody PostEditRequest postEditRequest,
      @AuthenticationPrincipal LoginMember loginMember) {

    PostDto postDto = postService.editPost(postEditRequest, postId, loginMember.getMemberId());

    return ResponseEntity.ok(postDto);
  }

  @ApiOperation("게시글 삭제")
  @PreAuthorize("hasAnyRole('ROLE_USER')")
  @DeleteMapping("/post/{postId}")
  public ResponseEntity<PostDeleteResponse> deletePost(@PathVariable Long postId,
      @AuthenticationPrincipal LoginMember loginMember) {

    PostDeleteResponse response = postService.deletePost(postId, loginMember.getMemberId());

    return ResponseEntity.ok(response);
  }

  @ApiOperation(value = "게시글에 포함된 이미지 리스트", notes = "게시글 수정 전에 사용하고, 수정 Request에 포함시킨다.")
  @GetMapping("/post/{postId}/images")
  public ResponseEntity<List<String>> getImagesFromPost(@PathVariable Long postId) {

    List<String> imageList = postService.getImagesFromPost(postId);

    return ResponseEntity.ok(imageList);
  }
}
