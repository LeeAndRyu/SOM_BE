package com.blog.som.domain.likes.service;

import com.blog.som.domain.likes.dto.LikesResponse;

public interface LikesService {

  LikesResponse doLikes(Long postId, Long loginMemberId);

  LikesResponse cancelLikes(Long postId, Long loginMemberId);

  LikesResponse memberLikesPost(Long postId, Long loginMemberId);

}
