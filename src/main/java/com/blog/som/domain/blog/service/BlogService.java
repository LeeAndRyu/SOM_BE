package com.blog.som.domain.blog.service;


import com.blog.som.domain.blog.dto.BlogMemberDto;
import com.blog.som.domain.blog.dto.BlogPostList;
import com.blog.som.domain.blog.dto.BlogTagListDto;
import com.blog.som.domain.follow.type.FollowStatus;

public interface BlogService {

  /**
   * 블로그 회원 정보 조회
   */
  BlogMemberDto getBlogMember(String accountName);

  /**
   * 블로그 태그 리스트 조회
   */
  BlogTagListDto getBlogTags(String accountName);


  /**
   * 블로그 명 (accountName)이 유효한 지 검증
   */
  void validateAccountName(String accountName);

  /**
   * 로그인 유저의 팔로우 여부
   */
  FollowStatus getFollowStatus(Long memberId, String accountName);

  /**
   * 정렬 방식에 따른 postList 조회
   * - sort=latest(default) : 최신 순
   * - sort=hot : 조회수 순
   */
  BlogPostList getAllBlogPostListBySortType(String accountName, String sort, int page);

  /**
   * tagName 태그를 가진 postList 조회
   * - 정렬 : 최신 순
   */
  BlogPostList getBlogPostListByTag(String accountName, String tagName, int page);

  /**
   * title 또는 introduction에 "query"를 포함한 postList 조회
   * - 정렬 : 최신 순
   */
  BlogPostList getBlogPostListByQuery(String accountName, String query, int page);
}
