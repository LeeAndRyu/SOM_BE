package com.blog.som.domain.post.service;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import com.blog.som.EntityCreator;
import com.blog.som.domain.member.entity.MemberEntity;
import com.blog.som.domain.member.repository.MemberRepository;
import com.blog.som.domain.post.dto.PostDto;
import com.blog.som.domain.post.dto.PostWriteRequest;
import com.blog.som.domain.post.entity.PostEntity;
import com.blog.som.domain.post.repository.PostRepository;
import com.blog.som.domain.tag.entity.PostTagEntity;
import com.blog.som.domain.tag.entity.TagEntity;
import com.blog.som.domain.tag.repository.PostTagRepository;
import com.blog.som.domain.tag.repository.TagRepository;
import com.blog.som.global.exception.ErrorCode;
import com.blog.som.global.exception.custom.MemberException;
import com.blog.som.global.exception.custom.PostException;
import com.blog.som.global.redis.email.CacheRepository;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@Slf4j
@ExtendWith(MockitoExtension.class)
class PostServiceTest {

  @Mock
  private PostRepository postRepository;
  @Mock
  private MemberRepository memberRepository;
  @Mock
  private TagRepository tagRepository;
  @Mock
  private PostTagRepository postTagRepository;
  @Mock
  private CacheRepository cacheRepository;

  @InjectMocks
  private PostServiceImpl postService;

  static String TAG_1 = "tag1";
  static String TAG_2 = "tag2";

  @Nested
  @DisplayName("게시글 작성")
  class WritePost{

    private PostWriteRequest createRequest(int id, List<String> tagList){
      return PostWriteRequest.builder()
          .title("test-title" + id)
          .content("test-content" + id)
          .thumbnail("test-thumbnail" + id + ".jpg")
          .introduction("test-introduction" + id)
          .tags(tagList)
          .build();
    }

    @Test
    @DisplayName("성공")
    void writePost(){
      MemberEntity member = EntityCreator.createMember(1L);
      PostEntity post = EntityCreator.createPost(10L, member);

      List<String> tagList = new ArrayList<>(Arrays.asList(TAG_1,TAG_2));
      PostWriteRequest request = createRequest(10, tagList);

      TagEntity tag1 = EntityCreator.createTag(100L, TAG_1, member);
      TagEntity tag2 = EntityCreator.createTag(101L, TAG_2, member);

      //given
      when(memberRepository.findById(1L))
          .thenReturn(Optional.of(member));
      when(postRepository.save(PostWriteRequest.toEntity(request, member)))
          .thenReturn(post);
      when(tagRepository.findByTagNameAndMember(tagList.get(0), member))
          .thenReturn(Optional.of(tag1));
      when(tagRepository.findByTagNameAndMember(tagList.get(1), member))
          .thenReturn(Optional.empty());

      //when
      PostDto result = postService.writePost(request, 1L);

      //then
      assertThat(result.getTitle()).isEqualTo(request.getTitle());
      assertThat(result.getContent()).isEqualTo(request.getContent());
      assertThat(result.getTags()).containsAll(tagList);
    }

    @Test
    @DisplayName("실패 : MEMBER_NOT_FOUND")
    void writePos_MEMBER_NOT_FOUND(){
      MemberEntity member = EntityCreator.createMember(1L);

      List<String> tagList = new ArrayList<>(Arrays.asList(TAG_1,TAG_2));
      PostWriteRequest request = createRequest(10, tagList);

      //given
      when(memberRepository.findById(1L))
          .thenReturn(Optional.empty());

      //when
      //then
      MemberException memberException =
          assertThrows(MemberException.class, () -> postService.writePost(request, 1L));
      assertThat(memberException.getErrorCode()).isEqualTo(ErrorCode.MEMBER_NOT_FOUND);
    }
  }

  @Nested
  @DisplayName("게시글 조회")
  class GetPost{

    @Test
    @DisplayName("성공 : 조회수 증가")
    void getPost(){
      MemberEntity member = EntityCreator.createMember(1L);
      PostEntity post = EntityCreator.createPost(10L, member);
      TagEntity tag1 = EntityCreator.createTag(100L, TAG_1, member);
      TagEntity tag2 = EntityCreator.createTag(101L, TAG_2, member);
      PostTagEntity postTag1 = EntityCreator.createPostTag(1000L, post, tag1);
      PostTagEntity postTag2 = EntityCreator.createPostTag(1001L, post, tag2);

      String userAgent = "CHROME/123";

      //given
      when(postRepository.findById(10L))
          .thenReturn(Optional.of(post));
      when(postTagRepository.findAllByPost(post))
          .thenReturn(new ArrayList<>(Arrays.asList(postTag1, postTag2)));
      when(cacheRepository.canAddView(userAgent))
          .thenReturn(true);

      //when
      PostDto postDto = postService.getPost(10L, userAgent);

      //then
      verify(postRepository, times(1)).save(post);
      assertThat(postDto.getPostId()).isEqualTo(post.getPostId());
      assertThat(postDto.getTitle()).isEqualTo(post.getTitle());
    }

    @Test
    @DisplayName("성공 : 조회수 증가 X")
    void getPost_never_add_vew(){
      MemberEntity member = EntityCreator.createMember(1L);
      PostEntity post = EntityCreator.createPost(10L, member);
      TagEntity tag1 = EntityCreator.createTag(100L, TAG_1, member);
      TagEntity tag2 = EntityCreator.createTag(101L, TAG_2, member);
      PostTagEntity postTag1 = EntityCreator.createPostTag(1000L, post, tag1);
      PostTagEntity postTag2 = EntityCreator.createPostTag(1001L, post, tag2);

      String userAgent = "CHROME/123";

      //given
      when(postRepository.findById(10L))
          .thenReturn(Optional.of(post));
      when(postTagRepository.findAllByPost(post))
          .thenReturn(new ArrayList<>(Arrays.asList(postTag1, postTag2)));
      when(cacheRepository.canAddView(userAgent))
          .thenReturn(false);

      //when
      PostDto postDto = postService.getPost(10L, userAgent);

      //then
      verify(postRepository, never()).save(post);
      assertThat(postDto.getPostId()).isEqualTo(post.getPostId());
      assertThat(postDto.getTitle()).isEqualTo(post.getTitle());
    }

    @Test
    @DisplayName("실패 : POST_NOT_FOUND")
    void getPost_POST_NOT_FOUND(){
      MemberEntity member = EntityCreator.createMember(1L);
      PostEntity post = EntityCreator.createPost(10L, member);
      String userAgent = "CHROME/123";

      //given
      when(postRepository.findById(10L))
          .thenReturn(Optional.empty());

      //when
      //then
      PostException postException =
          assertThrows(PostException.class, () -> postService.getPost(10L, userAgent));
      assertThat(postException.getErrorCode()).isEqualTo(ErrorCode.POST_NOT_FOUND);

    }

  }

}