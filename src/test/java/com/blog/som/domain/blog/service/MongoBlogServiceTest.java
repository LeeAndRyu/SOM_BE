package com.blog.som.domain.blog.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.when;

import com.blog.som.EntityCreator;
import com.blog.som.domain.blog.dto.BlogMemberDto;
import com.blog.som.domain.blog.dto.BlogPostDto;
import com.blog.som.domain.blog.dto.BlogPostList;
import com.blog.som.domain.blog.dto.BlogTagListDto;
import com.blog.som.domain.follow.service.FollowService;
import com.blog.som.domain.follow.type.FollowStatus;
import com.blog.som.domain.member.entity.MemberEntity;
import com.blog.som.domain.member.repository.MemberRepository;
import com.blog.som.domain.post.mongo.document.PostDocument;
import com.blog.som.domain.post.mongo.respository.MongoPostRepository;
import com.blog.som.domain.post.repository.PostRepository;
import com.blog.som.domain.tag.entity.TagEntity;
import com.blog.som.domain.tag.repository.TagRepository;
import com.blog.som.global.constant.NumberConstant;
import com.blog.som.global.constant.SearchConstant;
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
class MongoBlogServiceTest {

  @Mock
  private MemberRepository memberRepository;
  @Mock
  private FollowService followService;
  @Mock
  private MongoPostRepository mongoPostRepository;
  @Mock
  private PostRepository postRepository;
  @Mock
  private TagRepository tagRepository;

  @InjectMocks
  private MongoBlogService blogService;

  @Nested
  @DisplayName("Blog 메인 페이지 조회 - 회원")
  class GetBlogMember {

    @Test
    @DisplayName("성공")
    void getBlogMember() {
      MemberEntity member = EntityCreator.createMember(1L);
      String accountName = member.getAccountName();

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
      assertThat(result.getFollowStatus()).isEqualTo(FollowStatus.NOT_LOGGED_IN);
    }

    @Test
    @DisplayName("실패 : BLOG_NOT_FOUND")
    void getBlogMember_BLOG_NOT_FOUND() {
      MemberEntity member = EntityCreator.createMember(1L);
      String accountName = member.getAccountName();

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
  class GetBlogTags {

    @Test
    @DisplayName("성공")
    void getBlogTags() {
      MemberEntity member = EntityCreator.createMember(1L);
      String accountName = member.getAccountName();
      List<TagEntity> list = new ArrayList<>();
      for (int i = 1; i <= 5; i++) {
        list.add(EntityCreator.createTag(10L + i, "tag" + i, member));
      }
      //given
      when(memberRepository.findByAccountName(accountName))
          .thenReturn(Optional.of(member));
      when(tagRepository.findAllByMember(member))
          .thenReturn(list);
      when(mongoPostRepository.countByAccountName(accountName))
          .thenReturn(5);
      //when
      BlogTagListDto blogTags = blogService.getBlogTags(accountName);

      //then
      assertThat(blogTags.getTotalPostCount()).isEqualTo(5);
      assertThat(blogTags.getTagList().size()).isEqualTo(5);
    }

    @Test
    @DisplayName("실패")
    void getBlogTags_BLOG_NOT_FOUND() {
      MemberEntity member = EntityCreator.createMember(1L);
      String accountName = member.getAccountName();

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
  class GetFollowStatus {

    @Test
    @DisplayName("성공 : 팔로우 상태")
    void getFollowStatus_FOLLOWED() {
      MemberEntity fromMember = EntityCreator.createMember(1L);
      MemberEntity toMember = EntityCreator.createMember(2L);
      String toMemberAccountName = toMember.getAccountName();

      //given
      when(followService.isFollowing(1L, toMemberAccountName))
          .thenReturn(true);
      //when
      FollowStatus followStatus = blogService.getFollowStatus(1L, toMemberAccountName);

      //then
      assertThat(followStatus).isEqualTo(FollowStatus.FOLLOWED);
    }

    @Test
    @DisplayName("성공 : 팔로우 하지 않는 상태")
    void getFollowStatus_NOT_FOLLOWED() {
      MemberEntity fromMember = EntityCreator.createMember(1L);
      MemberEntity toMember = EntityCreator.createMember(2L);
      String toMemberAccountName = toMember.getAccountName();

      //given
      when(followService.isFollowing(1L, toMemberAccountName))
          .thenReturn(false);
      //when
      FollowStatus followStatus = blogService.getFollowStatus(1L, toMemberAccountName);

      //then
      assertThat(followStatus).isEqualTo(FollowStatus.NOT_FOLLOWED);
    }
  }

  @Nested
  @DisplayName("Blog 메인 페이지 조회 - 정렬 방법")
  class GetBlogPostListBySortType {

    @Test
    @DisplayName("성공 : sort=latest")
    void getBlogPostListBySortType() {
      MemberEntity member = EntityCreator.createMember(1L);
      String accountName = member.getAccountName();
      String sort = "latest";
      int page = 1;
      PageRequest pageRequest =
          PageRequest.of(page - 1, NumberConstant.DEFAULT_PAGE_SIZE,
              Sort.by(SearchConstant.REGISTERED_AT).descending());

      List<PostDocument> PostDocumentList = new ArrayList<>();

      for (int i = 1; i <= 5; i++) {
        PostDocumentList.add(EntityCreator.createPostDocument(EntityCreator.createPost(100L + i, member)));
      }

      //given
      when(mongoPostRepository.findByAccountName(accountName, pageRequest))
          .thenReturn(new PageImpl<>(PostDocumentList));

      //when
      BlogPostList blogPostList = blogService.getAllBlogPostListBySortType(accountName, sort, page);

      //then

      for (BlogPostDto bp : blogPostList.getPostList()) {
        assertThat(bp.getMemberId()).isEqualTo(member.getMemberId());
        assertThat(bp.getAccountName()).isEqualTo(member.getAccountName());
      }
      assertThat(blogPostList.getPageDto().getCurrentPage()).isEqualTo(1);
      assertThat(blogPostList.getPageDto().getTotalElement()).isEqualTo(5);
    }

    @Test
    @DisplayName("성공 : sort=hot")
    void getBlogPostListBySortType_hot() {
      MemberEntity member = EntityCreator.createMember(1L);
      String accountName = member.getAccountName();
      String sort = SearchConstant.HOT;
      int page = 1;
      PageRequest pageRequest =
          PageRequest.of(page - 1, NumberConstant.DEFAULT_PAGE_SIZE, Sort.by(SearchConstant.VIEWS).descending());

      List<PostDocument> PostDocumentList = new ArrayList<>();

      for (int i = 1; i <= 5; i++) {
        PostDocumentList.add(EntityCreator.createPostDocument(EntityCreator.createPost(100L + i, member)));
      }

      //given
      when(mongoPostRepository.findByAccountName(accountName, pageRequest))
          .thenReturn(new PageImpl<>(PostDocumentList));

      //when
      BlogPostList blogPostList = blogService.getAllBlogPostListBySortType(accountName, sort, page);

      //then

      for (BlogPostDto bp : blogPostList.getPostList()) {
        assertThat(bp.getMemberId()).isEqualTo(member.getMemberId());
        assertThat(bp.getAccountName()).isEqualTo(member.getAccountName());
      }
      assertThat(blogPostList.getPageDto().getCurrentPage()).isEqualTo(1);
      assertThat(blogPostList.getPageDto().getTotalElement()).isEqualTo(5);
    }

  }

  @Test
  @DisplayName("Blog 메인 페이지 조회 - 태그로 검색")
  void getBlogPostListByTag() {
    MemberEntity member = EntityCreator.createMember(1L);
    String accountName = member.getAccountName();
    int page = 1;
    String tagName = "tag1";
    PageRequest pageRequest =
        PageRequest.of(page - 1, NumberConstant.DEFAULT_PAGE_SIZE,
            Sort.by(SearchConstant.REGISTERED_AT).descending());

    List<PostDocument> PostDocumentList = new ArrayList<>();

    for (int i = 1; i <= 5; i++) {
      PostDocumentList.add(EntityCreator.createPostDocument(EntityCreator.createPost(100L + i, member)));
    }

    //given
    when(mongoPostRepository.findByAccountNameAndTagsContaining(accountName, tagName, pageRequest))
        .thenReturn(new PageImpl<>(PostDocumentList));

    //when
    BlogPostList blogPostList = blogService.getBlogPostListByTag(accountName, tagName, page);

    //then
    for (BlogPostDto bp : blogPostList.getPostList()) {
      assertThat(bp.getMemberId()).isEqualTo(member.getMemberId());
      assertThat(bp.getAccountName()).isEqualTo(member.getAccountName());
    }
    assertThat(blogPostList.getPageDto().getCurrentPage()).isEqualTo(1);
    assertThat(blogPostList.getPageDto().getTotalElement()).isEqualTo(5);
  }

  @Test
  @DisplayName("Blog 메인 페이지 조회 - 검색어")
  void getBlogPostListByQuery() {
    MemberEntity member = EntityCreator.createMember(1L);
    String accountName = member.getAccountName();
    int page = 1;
    String query = "test";
    PageRequest pageRequest =
        PageRequest.of(page - 1, NumberConstant.DEFAULT_PAGE_SIZE, Sort.by("registeredAt").descending());

    List<PostDocument> PostDocumentList = new ArrayList<>();

    for (int i = 1; i <= 5; i++) {
      PostDocumentList.add(EntityCreator.createPostDocument(EntityCreator.createPost(100L + i, member)));
    }

    //given
    when(mongoPostRepository
        .findByAccountNameAndTitleContainingOrIntroductionContaining(
            accountName, query, query, pageRequest))
        .thenReturn(new PageImpl<>(PostDocumentList));

    //when
    BlogPostList blogPostList = blogService.getBlogPostListByQuery(accountName, query, page);

    //then
    for (BlogPostDto bp : blogPostList.getPostList()) {
      assertThat(bp.getMemberId()).isEqualTo(member.getMemberId());
      assertThat(bp.getAccountName()).isEqualTo(member.getAccountName());
    }
    assertThat(blogPostList.getPageDto().getCurrentPage()).isEqualTo(1);
    assertThat(blogPostList.getPageDto().getTotalElement()).isEqualTo(5);
  }


}