package com.blog.som.domain.blog.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.never;
import static org.mockito.BDDMockito.times;
import static org.mockito.BDDMockito.verify;
import static org.mockito.BDDMockito.when;

import com.blog.som.EntityCreator;
import com.blog.som.domain.blog.constant.FollowConstant;
import com.blog.som.domain.blog.dto.BlogMemberDto;
import com.blog.som.domain.blog.dto.BlogPostDto;
import com.blog.som.domain.blog.dto.BlogPostList;
import com.blog.som.domain.follow.service.FollowService;
import com.blog.som.domain.member.entity.MemberEntity;
import com.blog.som.domain.member.repository.MemberRepository;
import com.blog.som.domain.post.elasticsearch.document.PostDocument;
import com.blog.som.domain.post.elasticsearch.repository.ElasticsearchPostRepository;
import com.blog.som.domain.post.entity.PostEntity;
import com.blog.som.domain.post.repository.PostRepository;
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
class ElasticsearchBlogServiceTest {

  @Mock
  private MemberRepository memberRepository;
  @Mock
  private FollowService followService;
  @Mock
  private ElasticsearchPostRepository elasticSearchPostRepository;

  @InjectMocks
  private ElasticsearchBlogService blogService;

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
      assertThat(result.getLoginMemberFollowStatus()).isNull();
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
      String followStatus = blogService.getFollowStatus(1L, toMemberAccountName);

      //then
      assertThat(followStatus).isEqualTo(FollowConstant.FOLLOWED);
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
      String followStatus = blogService.getFollowStatus(1L, toMemberAccountName);

      //then
      assertThat(followStatus).isEqualTo(FollowConstant.NOT_FOLLOWED);
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

      List<PostDocument> postDocumentList = new ArrayList<>();

      for (int i = 1; i <= 5; i++) {
        postDocumentList.add(EntityCreator.createPostDocument(EntityCreator.createPost(100L + i, member)));
      }

      //given
      when(elasticSearchPostRepository.findAllByAccountName(accountName, pageRequest))
          .thenReturn(new PageImpl<>(postDocumentList));

      //when
      BlogPostList blogPostList = blogService.getBlogPostListBySortType(accountName, sort, page);

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

      List<PostDocument> postDocumentList = new ArrayList<>();

      for (int i = 1; i <= 5; i++) {
        postDocumentList.add(EntityCreator.createPostDocument(EntityCreator.createPost(100L + i, member)));
      }

      //given
      when(elasticSearchPostRepository.findAllByAccountName(accountName, pageRequest))
          .thenReturn(new PageImpl<>(postDocumentList));

      //when
      BlogPostList blogPostList = blogService.getBlogPostListBySortType(accountName, sort, page);

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

    List<PostDocument> postDocumentList = new ArrayList<>();

    for (int i = 1; i <= 5; i++) {
      postDocumentList.add(EntityCreator.createPostDocument(EntityCreator.createPost(100L + i, member)));
    }

    //given
    when(elasticSearchPostRepository.findByAccountNameAndTagsContaining(accountName, tagName, pageRequest))
        .thenReturn(new PageImpl<>(postDocumentList));

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

    List<PostDocument> postDocumentList = new ArrayList<>();

    for (int i = 1; i <= 5; i++) {
      postDocumentList.add(EntityCreator.createPostDocument(EntityCreator.createPost(100L + i, member)));
    }

    //given
    when(elasticSearchPostRepository
        .findByAccountNameAndTitleContainingOrIntroductionContaining(
            accountName, query, query, pageRequest))
        .thenReturn(new PageImpl<>(postDocumentList));

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