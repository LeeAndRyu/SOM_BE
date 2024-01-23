package com.blog.som.domain.post.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.any;
import static org.mockito.BDDMockito.never;
import static org.mockito.BDDMockito.times;
import static org.mockito.BDDMockito.verify;
import static org.mockito.BDDMockito.when;

import com.blog.som.EntityCreator;
import com.blog.som.domain.member.entity.MemberEntity;
import com.blog.som.domain.member.repository.MemberRepository;
import com.blog.som.domain.post.dto.PostDeleteResponse;
import com.blog.som.domain.post.dto.PostDto;
import com.blog.som.domain.post.dto.PostEditRequest;
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
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
  static String TAG_3 = "tag3";

  @Nested
  @DisplayName("게시글 작성")
  class WritePost {

    private PostWriteRequest createRequest(int id, List<String> tagList) {
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
    void writePost() {
      MemberEntity member = EntityCreator.createMember(1L);
      PostEntity post = EntityCreator.createPost(10L, member);

      List<String> tagList = new ArrayList<>(Arrays.asList(TAG_1, TAG_2));
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
    void writePos_MEMBER_NOT_FOUND() {
      MemberEntity member = EntityCreator.createMember(1L);

      List<String> tagList = new ArrayList<>(Arrays.asList(TAG_1, TAG_2));
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
  class GetPost {

    @Test
    @DisplayName("성공 : 조회수 증가")
    void getPost() {
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
    void getPost_never_add_vew() {
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
    void getPost_POST_NOT_FOUND() {
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

  @Nested
  @DisplayName("editPost")
  class EditPost {

    private PostEditRequest createRequest(List<String> tags) {
      return PostEditRequest.builder()
          .title("edit-test-title")
          .content("edit-test-content")
          .thumbnail("test-thumbnail.jpg")
          .introduction("edit-test-introduction")
          .tags(tags)
          .build();
    }


    /**
     * - 기존:TAG_1,TAG_2 - 변경:TAG_1,TAG_2 - 기존 태그와 변경된 태그가 일치할 때
     */
    @Test
    @DisplayName("성공 : 태그 그대로")
    void editPost() {
      MemberEntity member = EntityCreator.createMember(1L);
      PostEntity post = EntityCreator.createPost(10L, member);

      TagEntity tag1 = EntityCreator.createTag(101L, TAG_1, member);
      TagEntity tag2 = EntityCreator.createTag(102L, TAG_2, member);
      PostTagEntity postTag1 = EntityCreator.createPostTag(1001L, post, tag1);
      PostTagEntity postTag2 = EntityCreator.createPostTag(1002L, post, tag2);
      //기존 태그
      List<PostTagEntity> postTagEntityList = new ArrayList<>(Arrays.asList(postTag1, postTag2));
      //변경된 태그
      List<String> list = new ArrayList<>(Arrays.asList(TAG_1, TAG_2));
      PostEditRequest request = createRequest(list);

      //given
      when(postRepository.findById(10L))
          .thenReturn(Optional.of(post));
      when(postTagRepository.findAllByPost(post))
          .thenReturn(postTagEntityList);

      //when
      PostDto postDto = postService.editPost(request, 10L, 1L);

      //then
      verify(postRepository, times(1)).save(post);
      verify(postTagRepository, never()).delete(any(PostTagEntity.class));
      verify(tagRepository, never()).findByTagNameAndMember(TAG_1, member);
      verify(tagRepository, never()).findByTagNameAndMember(TAG_2, member);

      assertThat(postDto.getTags()).containsAll(list);
      assertThat(postDto.getTitle()).isEqualTo(request.getTitle());
      assertThat(postDto.getContent()).isEqualTo(request.getContent());
      assertThat(postDto.getThumbnail()).isEqualTo(request.getThumbnail());
      assertThat(postDto.getIntroduction()).isEqualTo(request.getIntroduction());
    }


    /**
     * - 기존:TAG_1,TAG_2 - 변경:TAG_1,TAG_3 - TAG2 count=1 - 기존에 회원은 TAG_3 갖고있지 않음
     */
    @Test
    @DisplayName("성공 : 태그 하나 변경")
    void editPost_change_one_tag() {
      MemberEntity member = EntityCreator.createMember(1L);
      PostEntity post = EntityCreator.createPost(10L, member);

      TagEntity tag1 = EntityCreator.createTag(101L, TAG_1, member);
      TagEntity tag2 = EntityCreator.createTag(102L, TAG_2, member);//count=1
      TagEntity tag3 = EntityCreator.createTag(103L, TAG_3, member);
      PostTagEntity postTag1 = EntityCreator.createPostTag(1001L, post, tag1);
      PostTagEntity postTag2 = EntityCreator.createPostTag(1002L, post, tag2);
      PostTagEntity postTag3 = EntityCreator.createPostTag(1003L, post, tag3);

      //기존 태그
      List<PostTagEntity> postTagEntityList = new ArrayList<>(Arrays.asList(postTag1, postTag2));
      //변경된 태그
      List<String> list = new ArrayList<>(Arrays.asList(TAG_1, TAG_3));
      PostEditRequest request = createRequest(list);

      //given
      when(postRepository.findById(10L))
          .thenReturn(Optional.of(post));
      when(postTagRepository.findAllByPost(post))
          .thenReturn(postTagEntityList);
      when(tagRepository.findByTagNameAndMember(TAG_3, member)) //기존에 TAG3 없음
          .thenReturn(Optional.empty());

      //when
      PostDto postDto = postService.editPost(request, 10L, 1L);

      //then
      verify(postRepository, times(1)).save(post);
      verify(postTagRepository, times(1)).delete(postTag2);
      verify(tagRepository, times(1)).delete(tag2);
      //handleNewTags
      verify(tagRepository, times(1)).save(any(TagEntity.class));
      verify(postTagRepository, times(1)).save(any(PostTagEntity.class));

      assertThat(postDto.getTags()).containsAll(list);
      assertThat(postDto.getTitle()).isEqualTo(request.getTitle());
      assertThat(postDto.getContent()).isEqualTo(request.getContent());
      assertThat(postDto.getThumbnail()).isEqualTo(request.getThumbnail());
      assertThat(postDto.getIntroduction()).isEqualTo(request.getIntroduction());
    }

    /**
     * - 기존:TAG_1,TAG_2 - 변경:TAG_1,TAG_3 - TAG2 count=3 - 기존에 회원은 TAG_3 갖고있지 않음
     */
    @Test
    @DisplayName("성공 : 태그 하나 변경, 변경된 태그 count > 1")
    void editPost_change_one_tag_() {
      MemberEntity member = EntityCreator.createMember(1L);
      PostEntity post = EntityCreator.createPost(10L, member);

      TagEntity tag1 = EntityCreator.createTag(101L, TAG_1, member);
      TagEntity tag2 = EntityCreator.createTag(102L, TAG_2, member);//count=3
      tag2.setCount(3);
      TagEntity tag3 = EntityCreator.createTag(103L, TAG_3, member);
      PostTagEntity postTag1 = EntityCreator.createPostTag(1001L, post, tag1);
      PostTagEntity postTag2 = EntityCreator.createPostTag(1002L, post, tag2);
      PostTagEntity postTag3 = EntityCreator.createPostTag(1003L, post, tag3);

      //기존 태그
      List<PostTagEntity> postTagEntityList = new ArrayList<>(Arrays.asList(postTag1, postTag2));
      //변경된 태그
      List<String> list = new ArrayList<>(Arrays.asList(TAG_1, TAG_3));
      PostEditRequest request = createRequest(list);

      //given
      when(postRepository.findById(10L))
          .thenReturn(Optional.of(post));
      when(postTagRepository.findAllByPost(post))
          .thenReturn(postTagEntityList);
      when(tagRepository.findByTagNameAndMember(TAG_3, member)) //기존에 TAG3 없음
          .thenReturn(Optional.empty());

      //when
      PostDto postDto = postService.editPost(request, 10L, 1L);

      //then
      verify(postRepository, times(1)).save(post);
      verify(postTagRepository, times(1)).delete(postTag2);
      verify(tagRepository, never()).delete(tag2);
      verify(tagRepository, times(2)).save(any(TagEntity.class));
      verify(postTagRepository, times(1)).save(any(PostTagEntity.class));

      assertThat(postDto.getTags()).containsAll(list);
      assertThat(postDto.getTitle()).isEqualTo(request.getTitle());
      assertThat(postDto.getContent()).isEqualTo(request.getContent());
      assertThat(postDto.getThumbnail()).isEqualTo(request.getThumbnail());
      assertThat(postDto.getIntroduction()).isEqualTo(request.getIntroduction());
    }

    @Test
    @DisplayName("실패 : POST_NOT_FOUND")
    void editPost_POST_NOT_FOUND() {
      MemberEntity member = EntityCreator.createMember(1L);
      PostEntity post = EntityCreator.createPost(10L, member);
      List<String> list = new ArrayList<>(Arrays.asList(TAG_1, TAG_3));
      PostEditRequest request = createRequest(list);
      //given
      when(postRepository.findById(11L))
          .thenReturn(Optional.empty());

      //when
      //then
      PostException postException =
          assertThrows(PostException.class, () -> postService.editPost(request, 11L, 1L));
      assertThat(postException.getErrorCode()).isEqualTo(ErrorCode.POST_NOT_FOUND);

    }

    @Test
    @DisplayName("실패 : POST_EDIT_NO_AUTHORITY")
    void editPost_POST_EDIT_NO_AUTHORITY() {
      MemberEntity member = EntityCreator.createMember(1L);
      PostEntity post = EntityCreator.createPost(10L, member);
      List<String> list = new ArrayList<>(Arrays.asList(TAG_1, TAG_3));
      PostEditRequest request = createRequest(list);
      //given
      when(postRepository.findById(10L))
          .thenReturn(Optional.of(post));

      //when
      //then
      PostException postException =
          assertThrows(PostException.class, () -> postService.editPost(request, 10L, 2L));
      assertThat(postException.getErrorCode()).isEqualTo(ErrorCode.POST_EDIT_NO_AUTHORITY);

    }
  }

  @Nested
  @DisplayName("게시글 삭제")
  class DeletePost {

    @Test
    @DisplayName("성공 - tag.count=1")
    void deletePost() {
      MemberEntity member = EntityCreator.createMember(1L);
      PostEntity post = EntityCreator.createPost(10L, member);

      TagEntity tag1 = EntityCreator.createTag(101L, TAG_1, member);
      tag1.setCount(1);
      PostTagEntity postTag1 = EntityCreator.createPostTag(1001L, post, tag1);

      List<PostTagEntity> postTagEntityList = new ArrayList<>(Arrays.asList(postTag1));

      //given
      when(postRepository.findById(10L))
          .thenReturn(Optional.of(post));
      when(postTagRepository.findAllByPost(post))
          .thenReturn(postTagEntityList);

      //when
      PostDeleteResponse response = postService.deletePost(10L, 1L);

      //then
      verify(postTagRepository, times(1)).delete(postTag1);
      verify(tagRepository, times(1)).delete(tag1);
      verify(postRepository, times(1)).delete(post);

      assertThat(response.getPostId()).isEqualTo(10L);
      assertThat(response.getPostTitle()).isEqualTo(post.getTitle());
    }

    @Test
    @DisplayName("성공 - tag.count > 1")
    void deletePost_tag_count_3() {
      MemberEntity member = EntityCreator.createMember(1L);
      PostEntity post = EntityCreator.createPost(10L, member);

      TagEntity tag1 = EntityCreator.createTag(101L, TAG_1, member);
      tag1.setCount(3);
      PostTagEntity postTag1 = EntityCreator.createPostTag(1001L, post, tag1);

      List<PostTagEntity> postTagEntityList = new ArrayList<>(Arrays.asList(postTag1));

      //given
      when(postRepository.findById(10L))
          .thenReturn(Optional.of(post));
      when(postTagRepository.findAllByPost(post))
          .thenReturn(postTagEntityList);

      //when
      PostDeleteResponse response = postService.deletePost(10L, 1L);

      //then
      verify(postTagRepository, times(1)).delete(postTag1);
      verify(tagRepository, never()).delete(tag1);
      verify(tagRepository, times(1)).save(tag1);
      verify(postRepository, times(1)).delete(post);

      assertThat(response.getPostId()).isEqualTo(10L);
      assertThat(response.getPostTitle()).isEqualTo(post.getTitle());
    }

    @Test
    @DisplayName("실패 : POST_NOT_FOUND")
    void deletePost_POST_POST_NOT_FOUND() {
      MemberEntity member = EntityCreator.createMember(1L);
      PostEntity post = EntityCreator.createPost(10L, member);

      //given
      when(postRepository.findById(11L))
          .thenReturn(Optional.empty());

      //when
      PostException postException =
          assertThrows(PostException.class, () -> postService.deletePost(11L, 1L));
      assertThat(postException.getErrorCode()).isEqualTo(ErrorCode.POST_NOT_FOUND);
    }

    @Test
    @DisplayName("실패 : POST_DELETE_NO_AUTHORITY")
    void deletePost_POST_DELETE_NO_AUTHORITY() {
      MemberEntity member = EntityCreator.createMember(1L);
      PostEntity post = EntityCreator.createPost(10L, member);

      //given
      when(postRepository.findById(10L))
          .thenReturn(Optional.of(post));

      //when
      PostException postException =
          assertThrows(PostException.class, () -> postService.deletePost(10L, 2L));
      assertThat(postException.getErrorCode()).isEqualTo(ErrorCode.POST_DELETE_NO_AUTHORITY);
    }

  }

}