package com.blog.som.domain.post.service;


import com.blog.som.domain.post.dto.PostDeleteResponse;
import com.blog.som.domain.post.dto.PostDto;
import com.blog.som.domain.post.dto.PostEditRequest;
import com.blog.som.domain.post.dto.PostWriteRequest;

public interface PostService {

  /**
   * 게시글 작성
   */
  PostDto writePost(PostWriteRequest request, Long memberId);

  /**
   * 게시글 조회
   */
  PostDto getPost(Long postId, String accessUserAgent);

  /**
   * 게시글 수정
   */
  PostDto editPost(PostEditRequest postEditRequest, Long postId, Long loginMemberId);

  /**
   * 게시글 삭제
   */
  PostDeleteResponse deletePost(Long postId, Long loginMemberId);

}
