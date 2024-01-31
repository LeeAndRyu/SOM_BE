package com.blog.som.domain.blog.service;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import com.blog.som.EntityCreator;
import com.blog.som.domain.blog.constant.FollowConstant;
import com.blog.som.domain.blog.dto.BlogMemberDto;
import com.blog.som.domain.blog.dto.BlogPostDto;
import com.blog.som.domain.blog.dto.BlogPostList;
import com.blog.som.domain.blog.dto.BlogTagListDto;
import com.blog.som.domain.follow.service.FollowService;
import com.blog.som.domain.member.entity.MemberEntity;
import com.blog.som.domain.member.repository.MemberRepository;
import com.blog.som.domain.post.entity.PostEntity;
import com.blog.som.domain.post.repository.PostRepository;
import com.blog.som.domain.tag.entity.PostTagEntity;
import com.blog.som.domain.tag.entity.TagEntity;
import com.blog.som.domain.tag.repository.PostTagRepository;
import com.blog.som.domain.tag.repository.TagRepository;
import com.blog.som.global.constant.NumberConstant;
import com.blog.som.global.exception.ErrorCode;
import com.blog.som.global.exception.custom.BlogException;
import java.util.ArrayList;
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
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

@Slf4j
@ExtendWith(MockitoExtension.class)
class BlogServiceImplTest {

  @Mock
  private MemberRepository memberRepository;
  @Mock
  private PostRepository postRepository;
  @Mock
  private TagRepository tagRepository;
  @Mock
  private PostTagRepository postTagRepository;
  @Mock
  private FollowService followService;

  @InjectMocks
  private BlogServiceImpl blogService;


  @Nested
  @DisplayName("Blog 메인 페이지 조회 - 회원")
  class GetBlogMember{
    @Test
    @DisplayName("성공")
    void getBlogMember(){
      MemberEntity member = EntityCreator.createMember(1L);
      String accountName=  member.getAccountName();

      //given
      when(memberRepository.findByAccountName(accountName))
          .thenReturn(Optional.of(member));

      //when
      BlogMemberDto result = blogService.getBlogMember(accountName);

      //then
      assertThat(result.getBlogName()).isEqualTo(member.getBlogName());
      assertThat(result.getProfileImage()).isEqualTo(member.getProfileImage());
      assertThat(result.getNickname()).isEqualTo(member.getNickname());
      assertThat(result.getIntroduction()).isEqualTo(member.getIntroduction());
      assertThat(result.getFollowerCount()).isEqualTo(member.getFollowerCount());
      assertThat(result.getFollowingCount()).isEqualTo(member.getFollowingCount());
      assertThat(result.getLoginMemberFollowStatus()).isNull();
    }

    @Test
    @DisplayName("실패 : BLOG_NOT_FOUND")
    void getBlogMember_BLOG_NOT_FOUND(){
      MemberEntity member = EntityCreator.createMember(1L);
      String accountName=  member.getAccountName();

      //given
      when(memberRepository.findByAccountName(accountName))
          .thenReturn(Optional.empty());

      //when
      //then
      BlogException blogException =
          assertThrows(BlogException.class, () -> blogService.getBlogMember(accountName));
      assertThat(blogException.getErrorCode()).isEqualTo(ErrorCode.BLOG_NOT_FOUND);
    }
  }

  @Nested
  @DisplayName("Blog 태그 목록 조회")
  class GetBlogTags{
    @Test
    @DisplayName("성공")
    void getBlogTags(){
      MemberEntity member = EntityCreator.createMember(1L);
      String accountName=  member.getAccountName();
      List<TagEntity> list = new ArrayList<>();
      for(int i = 1 ; i <= 5; i++){
        list.add(EntityCreator.createTag(10L + i, "tag" + i, member));
      }
      //given
      when(memberRepository.findByAccountName(accountName))
          .thenReturn(Optional.of(member));
      when(tagRepository.findAllByMember(member))
          .thenReturn(list);
      when(postRepository.countByMember(member))
          .thenReturn(5);
      //when
      BlogTagListDto blogTags = blogService.getBlogTags(accountName);

      //then
      assertThat(blogTags.getTotalPostCount()).isEqualTo(5);
      assertThat(blogTags.getTagList().size()).isEqualTo(5);
    }

    @Test
    @DisplayName("실패")
    void getBlogTags_BLOG_NOT_FOUND(){
      MemberEntity member = EntityCreator.createMember(1L);
      String accountName=  member.getAccountName();

      //given
      when(memberRepository.findByAccountName(accountName))
          .thenReturn(Optional.empty());

      //when
      //then
      BlogException blogException =
          assertThrows(BlogException.class, () -> blogService.getBlogTags(accountName));
      assertThat(blogException.getErrorCode()).isEqualTo(ErrorCode.BLOG_NOT_FOUND);
    }
  }

  @Nested
  @DisplayName("Blog 메인 페이지 조회 - 팔로우 여부")
  class GetFollowStatus{
    @Test
    @DisplayName("성공 : 팔로우 상태")
    void getFollowStatus_FOLLOWED(){
      MemberEntity fromMember = EntityCreator.createMember(1L);
      MemberEntity toMember = EntityCreator.createMember(2L);
      String toMemberAccountName = toMember.getAccountName();

      //given
      when(followService.isFollowing(1L, toMemberAccountName))
          .thenReturn(true);
      //when
      String followStatus = blogService.getFollowStatus(1L, toMemberAccountName);

      //then
      assertThat(followStatus).isEqualTo(FollowConstant.FOLLOWED);
    }

    @Test
    @DisplayName("성공 : 팔로우 하지 않는 상태")
    void getFollowStatus_NOT_FOLLOWED(){
      MemberEntity fromMember = EntityCreator.createMember(1L);
      MemberEntity toMember = EntityCreator.createMember(2L);
      String toMemberAccountName = toMember.getAccountName();

      //given
      when(followService.isFollowing(1L, toMemberAccountName))
          .thenReturn(false);
      //when
      String followStatus = blogService.getFollowStatus(1L, toMemberAccountName);

      //then
      assertThat(followStatus).isEqualTo(FollowConstant.NOT_FOLLOWED);
    }
  }

  @Nested
  @DisplayName("Blog 메인 페이지 조회 - 정렬 방법")
  class GetBlogPostListBySortType{
    @Test
    @DisplayName("성공 : sort=latest_latest")
    void getBlogPostListBySortType(){
      MemberEntity member = EntityCreator.createMember(1L);
      String accountName = member.getAccountName();
      String sort = "latest";
      int page = 1;
      PageRequest pageRequest =
          PageRequest.of(page - 1, NumberConstant.DEFAULT_PAGE_SIZE, Sort.by("registeredAt").descending());

      List<PostEntity> postList = new ArrayList<>();

      for (int i = 1; i <= 5; i++){
        postList.add(EntityCreator.createPost(1000L + i, member));
      }

      //given
      when(memberRepository.findByAccountName(accountName))
          .thenReturn(Optional.of(member));
      when(postRepository.findByMember(member, pageRequest))
          .thenReturn(new PageImpl<>(postList));

      //when
      BlogPostList blogPostList = blogService.getAllBlogPostListBySortType(accountName, sort, page);

      //then
      verify(postTagRepository, times(postList.size())).findAllByPost(any(PostEntity.class));

      for(BlogPostDto bp : blogPostList.getPostList()){
        assertThat(bp.getMemberId()).isEqualTo(member.getMemberId());
        assertThat(bp.getAccountName()).isEqualTo(member.getAccountName());
      }
      assertThat(blogPostList.getPageDto().getCurrentPage()).isEqualTo(1);
      assertThat(blogPostList.getPageDto().getTotalElement()).isEqualTo(5);
    }

    @Test
    @DisplayName("성공 : sort=hot")
    void getBlogPostListBySortType_hot(){
      MemberEntity member = EntityCreator.createMember(1L);
      String accountName = member.getAccountName();
      String sort = "hot";
      int page = 1;
      PageRequest pageRequest =
          PageRequest.of(page - 1, NumberConstant.DEFAULT_PAGE_SIZE, Sort.by("views").descending());

      List<PostEntity> postList = new ArrayList<>();

      for (int i = 1; i <= 5; i++){
        postList.add(EntityCreator.createPost(1000L + i, member));
      }

      //given
      when(memberRepository.findByAccountName(accountName))
          .thenReturn(Optional.of(member));
      when(postRepository.findByMember(member, pageRequest))
          .thenReturn(new PageImpl<>(postList));

      //when
      BlogPostList blogPostList = blogService.getAllBlogPostListBySortType(accountName, sort, page);

      //then
      verify(postTagRepository, times(postList.size())).findAllByPost(any(PostEntity.class));

      for(BlogPostDto bp : blogPostList.getPostList()){
        assertThat(bp.getMemberId()).isEqualTo(member.getMemberId());
        assertThat(bp.getAccountName()).isEqualTo(member.getAccountName());
      }
      assertThat(blogPostList.getPageDto().getCurrentPage()).isEqualTo(1);
      assertThat(blogPostList.getPageDto().getTotalElement()).isEqualTo(5);
    }

    @Test
    @DisplayName("실패 : BLOG_NOT_FOUND")
    void getBlogPostListBySortType_BLOG_NOT_FOUND(){
      MemberEntity member = EntityCreator.createMember(1L);
      String accountName = member.getAccountName();
      String sort = "latest";
      int page = 1;
      PageRequest pageRequest =
          PageRequest.of(page - 1, NumberConstant.DEFAULT_PAGE_SIZE, Sort.by("views").descending());

      List<PostEntity> postList = new ArrayList<>();

      for (int i = 1; i <= 5; i++){
        postList.add(EntityCreator.createPost(1000L + i, member));
      }

      //given
      when(memberRepository.findByAccountName(accountName))
          .thenReturn(Optional.empty());

      //when
      //then
      BlogException blogException =
          assertThrows(BlogException.class, () -> blogService.getAllBlogPostListBySortType(accountName, sort, page));
      assertThat(blogException.getErrorCode()).isEqualTo(ErrorCode.BLOG_NOT_FOUND);

      verify(postTagRepository, never()).findAllByPost(any(PostEntity.class));
    }

  }

  @Nested
  @DisplayName("Blog 메인 페이지 조회 - 태그로 검색")
  class GetBlogPostListByTag{

    @Test
    @DisplayName("성공")
    void getBlogPostListByTag(){
      MemberEntity member = EntityCreator.createMember(1L);
      TagEntity tag = EntityCreator.createTag(10L, "test-tag", member);

      String accountName = member.getAccountName();
      int page = 1;
      PageRequest pageRequest =
          PageRequest.of(page - 1, NumberConstant.DEFAULT_PAGE_SIZE, Sort.by("postCreatedTime").descending());

      List<PostTagEntity> postTagList = new ArrayList<>();
      List<PostEntity> postList = new ArrayList<>();
      for (int i = 1; i <= 5; i++){
        PostEntity post = EntityCreator.createPost(100L + i, member);
        postTagList.add(EntityCreator.createPostTag(1000L + i, post, tag));
        postList.add(post);
      }

      //given
      when(memberRepository.findByAccountName(accountName))
          .thenReturn(Optional.of(member));
      when(tagRepository.findByTagNameAndMember(tag.getTagName(), member))
          .thenReturn(Optional.of(tag));
      when(postTagRepository.findByMemberAndTag(member, tag, pageRequest))
          .thenReturn(new PageImpl<>(postTagList));


      //when
      BlogPostList blogPostList = blogService.getBlogPostListByTag(accountName, tag.getTagName(), page);

      //then
      verify(postTagRepository, times(postTagList.size())).findAllByPost(any(PostEntity.class));

      for(BlogPostDto bp : blogPostList.getPostList()){
        assertThat(bp.getMemberId()).isEqualTo(member.getMemberId());
        assertThat(bp.getAccountName()).isEqualTo(member.getAccountName());
      }
      assertThat(blogPostList.getPageDto().getCurrentPage()).isEqualTo(1);
      assertThat(blogPostList.getPageDto().getTotalElement()).isEqualTo(5);
    }

    @Test
    @DisplayName("실패 : BLOG_NOT_FOUND")
    void getBlogPostListByTag_BLOG_NOT_FOUND(){
      MemberEntity member = EntityCreator.createMember(1L);
      TagEntity tag = EntityCreator.createTag(10L, "test-tag", member);

      String accountName = member.getAccountName();
      int page = 1;
      PageRequest pageRequest =
          PageRequest.of(page - 1, NumberConstant.DEFAULT_PAGE_SIZE, Sort.by("postCreatedTime").descending());

      //given
      when(memberRepository.findByAccountName(accountName))
          .thenReturn(Optional.empty());


      //when
      //then
      BlogException blogException = assertThrows(BlogException.class,
          () -> blogService.getBlogPostListByTag(accountName, tag.getTagName(), page));
      assertThat(blogException.getErrorCode()).isEqualTo(ErrorCode.BLOG_NOT_FOUND);

      verify(postTagRepository, never()).findByMemberAndTag(member, tag, pageRequest);
    }

    @Test
    @DisplayName("실패 : TAG_NOT_FOUND")
    void getBlogPostListByTag_TAG_NOT_FOUND(){
      MemberEntity member = EntityCreator.createMember(1L);
      TagEntity tag = EntityCreator.createTag(10L, "test-tag", member);

      String accountName = member.getAccountName();
      int page = 1;
      PageRequest pageRequest =
          PageRequest.of(page - 1, NumberConstant.DEFAULT_PAGE_SIZE, Sort.by("postCreatedTime").descending());

      //given
      when(memberRepository.findByAccountName(accountName))
          .thenReturn(Optional.of(member));
      when(tagRepository.findByTagNameAndMember(tag.getTagName(), member))
          .thenReturn(Optional.empty());

      //when
      //then
      BlogException blogException = assertThrows(BlogException.class,
          () -> blogService.getBlogPostListByTag(accountName, tag.getTagName(), page));
      assertThat(blogException.getErrorCode()).isEqualTo(ErrorCode.TAG_NOT_FOUND);

      verify(postTagRepository, never()).findByMemberAndTag(member, tag, pageRequest);
    }

  }

  @Nested
  @DisplayName("Blog 메인 페이지 조회 - 검색어")
  class GetBlogPostListByQuery{
    @Test
    @DisplayName("성공")
    void getBlogPostListByQuery(){
      MemberEntity member = EntityCreator.createMember(1L);
      String accountName = member.getAccountName();
      int page = 1;
      String query = "test";
      PageRequest pageRequest =
          PageRequest.of(page - 1, NumberConstant.DEFAULT_PAGE_SIZE, Sort.by("registeredAt").descending());

      List<PostEntity> postList = new ArrayList<>();
      for (int i = 1; i <= 5; i++){
        postList.add(EntityCreator.createPost(100L + i, member));
      }

      //given
      when(memberRepository.findByAccountName(accountName))
          .thenReturn(Optional.of(member));
      when(postRepository.findByMemberAndTitleContainingOrIntroductionContaining(member, query,query, pageRequest))
          .thenReturn(new PageImpl<>(postList));

      //when
      BlogPostList blogPostList = blogService.getBlogPostListByQuery(accountName, query, page);

      //then
      verify(postTagRepository, times(postList.size())).findAllByPost(any(PostEntity.class));

      for(BlogPostDto bp : blogPostList.getPostList()){
        assertThat(bp.getMemberId()).isEqualTo(member.getMemberId());
        assertThat(bp.getAccountName()).isEqualTo(member.getAccountName());
      }
      assertThat(blogPostList.getPageDto().getCurrentPage()).isEqualTo(1);
      assertThat(blogPostList.getPageDto().getTotalElement()).isEqualTo(5);
    }

    @Test
    @DisplayName("실패 : BLOG_NOT_FOUND")
    void getBlogPostListByQuery_BLOG_NOT_FOUND(){
      MemberEntity member = EntityCreator.createMember(1L);
      String accountName = member.getAccountName();
      int page = 1;
      String query = "test";
      PageRequest pageRequest =
          PageRequest.of(page - 1, NumberConstant.DEFAULT_PAGE_SIZE, Sort.by("registeredAt").descending());

      //given
      when(memberRepository.findByAccountName(accountName))
          .thenReturn(Optional.empty());

      //when
      //then
      BlogException blogException =
          assertThrows(BlogException.class, () -> blogService.getBlogPostListByQuery(accountName, query, page));
      assertThat(blogException.getErrorCode()).isEqualTo(ErrorCode.BLOG_NOT_FOUND);

      verify(postRepository, never())
          .findByMemberAndTitleContainingOrIntroductionContaining(member, query, query, pageRequest);
    }

  }


}