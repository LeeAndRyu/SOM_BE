package com.blog.som.domain.blog.service;


import com.blog.som.domain.blog.dto.BlogMemberDto;
import com.blog.som.domain.blog.dto.BlogPostList;

public interface BlogService {

  /**
   * 블로그 회원 정보 조회
   */
  BlogMemberDto getBlogMember(String accountName);

  /**
   * 정렬 방식에 따른 postList 조회
   * - sort=latest(default) : 최신 순
   * - sort=hot : 조회수 순
   */
  BlogPostList getBlogPostListBySortType(String accountName, String sort, int page);

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
