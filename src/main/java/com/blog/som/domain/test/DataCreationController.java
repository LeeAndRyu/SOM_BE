package com.blog.som.domain.test;

import com.blog.som.domain.member.entity.MemberEntity;
import com.blog.som.domain.member.repository.MemberRepository;
import com.blog.som.domain.post.entity.PostEntity;
import com.blog.som.domain.post.repository.PostRepository;
import com.blog.som.domain.tag.entity.PostTagEntity;
import com.blog.som.domain.tag.entity.TagEntity;
import com.blog.som.domain.tag.repository.PostTagRepository;
import com.blog.som.domain.tag.repository.TagRepository;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * 위에서부터 테스트용 게시글, tag, post_tag를 세팅할 수 있다.
 * jsonplaceholder사이트의 데이터를 사용한다.
 */
@RequiredArgsConstructor
@RestController
public class DataCreationController {

    private final PostRepository postRepository;
    private final MemberRepository memberRepository;
    private final TagRepository tagRepository;
    private final PostTagRepository postTagRepository;


    @ApiOperation("jsonplaceholder에서 post 임시 데이터 저장")
    @GetMapping("/test/post")
    public ResponseEntity<?> savePosts(){
        RestAPI restAPI = new RestAPI();

        MemberEntity member = memberRepository.findById(1L).get();

        List<PostEntity> saveList = new ArrayList<>();
        List<Map<String, Object>> posts = restAPI.getPosts();
        for (Map<String, Object> post : posts) {
            PostEntity postEntity = PostEntity.builder()
                    .member(member)
                    .title(post.get("title").toString())
                    .content(post.get("body").toString())
                    .thumbnail(restAPI.getRandomThumbnail())
                    .build();

            postEntity.setIntroduction("test introduction: " + postEntity.getContent().substring(0, 10));
            saveList.add(postEntity);
        }
        postRepository.saveAll(saveList);
        return ResponseEntity.ok("complete");
    }

    @ApiOperation("tag 세팅")
    @GetMapping("/test/tags")
    public ResponseEntity<?> saveTags(){
        MemberEntity member = memberRepository.findById(1L).get();
        List<TagEntity> saveList = new ArrayList<>();
        for(int i = 1; i <= 10; i++){
           saveList.add(TagEntity.builder()
                    .member(member)
                    .tagName("test_tag_" + i)
                    .build());
        }
        tagRepository.saveAll(saveList);
        return ResponseEntity.ok("complete");
    }

    @Transactional
    @ApiOperation("post tag 세팅")
    @GetMapping("/test/posttags")
    public ResponseEntity<?> savePostTags(){
        MemberEntity member = memberRepository.findById(1L).get();
        List<PostTagEntity> saveList = new ArrayList<>();
        List<TagEntity> tags = tagRepository.findAll();
        List<PostEntity> postList = postRepository.findAll();
        for (PostEntity post : postList) {
            int n = new Random().nextInt(10);
            saveList.add(PostTagEntity.builder()
                    .post(post)
                    .tag(tags.get(n))
                    .member(member)
                    .postCreatedTime(post.getRegisteredAt())
                    .build());
        }
        postTagRepository.saveAll(saveList);
        return ResponseEntity.ok("complete");
    }


}
