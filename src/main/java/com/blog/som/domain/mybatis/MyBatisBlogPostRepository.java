package com.blog.som.domain.mybatis;

import com.blog.som.domain.blog.repository.BlogPostRepository;
import com.blog.som.domain.post.entity.PostEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;

@Slf4j
@Repository
@RequiredArgsConstructor
public class MyBatisBlogPostRepository implements BlogPostRepository {

    private final BlogPostMapper blogPostMapper;

    @Override
    public PostEntity findById(Long postId) {
        return blogPostMapper.findById(postId);
    }

    @Override
    public List<BlogPostWithTagString> findByMemberId(Long memberId, String sort, String value, int pageStart, int pageSize) {
        List<BlogPostWithTagString> findList = blogPostMapper.findPostsByMemberId(memberId, sort, value, pageStart, pageSize);
        log.info("find Data : {}", findList);
        return findList;
    }

}
