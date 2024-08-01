package com.blog.som.domain.blog.repository;


import com.blog.som.domain.mybatis.BlogPostWithTagString;
import com.blog.som.domain.post.entity.PostEntity;

import java.util.List;

public interface BlogPostRepository {

    PostEntity findById(Long postId);
    List<BlogPostWithTagString> findByMemberId(Long memberId, String sort, String value, int page, int pageSize);

}
