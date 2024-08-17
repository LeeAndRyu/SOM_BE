package com.blog.som.domain.post.repository;

import com.blog.som.domain.member.repository.MemberRepository;
import com.blog.som.domain.mybatis.BlogPostWithTagString;
import com.blog.som.domain.mybatis.MyBatisBlogPostRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
class PostRepositoryTest {

    @Autowired
    MemberRepository memberRepository;
    @Autowired
    PostRepository postRepository;
    @Autowired
    MyBatisBlogPostRepository myBatisBlogPostRepository;

    @Test
    void findByMember() {
        List<BlogPostWithTagString> byMemberId = myBatisBlogPostRepository.findByMemberId(1L, "hot", "", 0, 10);
        for (BlogPostWithTagString blogPostWithTagString : byMemberId) {
            System.out.println(blogPostWithTagString);
        }
    }
}