package com.blog.som.domain.blog.service;


import com.blog.som.domain.blog.dto.BlogMemberDto;
import com.blog.som.domain.blog.dto.BlogPostList;

public interface BlogService {
  BlogMemberDto getBlogMember(String accountName);
  BlogPostList getBlogPostListBySortType(String accountName, String sort, int page);
  BlogPostList getBlogPostListByTag(String accountName, String tagName, int page);
  BlogPostList getBlogPostListByQuery(String accountName, String query, int page);
}
