package com.blog.som.domain.follow.service;

import com.blog.som.domain.follow.dto.FollowCancelResponse;
import com.blog.som.domain.follow.dto.FollowDto;

public interface FollowService {

  /**
   * follow 하기
   */
  FollowDto doFollow(Long fromMemberId, String blogAccountName);

  /**
   * follow 취소
   */
  FollowCancelResponse cancelFollow(Long fromMemberId, String blogAccountName);
}
