package com.blog.som.domain.tag.repository;

import com.blog.som.domain.post.entity.PostEntity;
import com.blog.som.domain.post.repository.PostRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class PostTagRepositoryTest {

    @Autowired
    PostTagRepository postTagRepository;

    @Autowired
    PostRepository postRepository;

    @Test
    void findPostTagNamesByPost() {
        PostEntity post = postRepository.findById(3L).get();
        List<String> list = postTagRepository.findPostTagNamesByPost(post);
        System.out.println(list);
    }
}