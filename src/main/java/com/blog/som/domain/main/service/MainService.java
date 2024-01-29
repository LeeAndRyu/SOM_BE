package com.blog.som.domain.main.service;

import com.blog.som.domain.blog.dto.BlogPostList;

public interface MainService {

  BlogPostList getAllPostListHot(int page);

  BlogPostList getAllPostListLatest(int page);

  BlogPostList searchAllPostByTitleOrIntroduction(String query, int page);

  BlogPostList searchAllPostByContent(String query, int page);

  BlogPostList searchAllPostByTag(String query, int page);
}
