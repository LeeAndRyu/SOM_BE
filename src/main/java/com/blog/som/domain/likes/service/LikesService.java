package com.blog.som.domain.likes.service;

import com.blog.som.domain.likes.dto.LikesResponse;

public interface LikesService {

  LikesResponse.ToggleResult toggleLikes(Long postId, Long loginMemberId);

  LikesResponse.MemberLikesPost memberLikesPost(Long postId, Long loginMemberId);

}
