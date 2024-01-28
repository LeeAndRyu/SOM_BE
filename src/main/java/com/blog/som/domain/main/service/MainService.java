package com.blog.som.domain.main.service;

import com.blog.som.domain.blog.dto.BlogPostList;

public interface MainService {

  BlogPostList getMainPageList(String sort, int page);
}
