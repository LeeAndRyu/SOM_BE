package com.blog.som.domain.post.service;


import com.blog.som.domain.post.dto.PostDto;
import com.blog.som.domain.post.dto.PostWriteRequest;

public interface PostService {

  PostDto writePost(PostWriteRequest request, Long memberId);

  PostDto getPost(Long postId);

}
