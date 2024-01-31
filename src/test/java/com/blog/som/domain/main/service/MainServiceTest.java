package com.blog.som.domain.main.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.when;

import com.blog.som.EntityCreator;
import com.blog.som.domain.blog.dto.BlogPostDto;
import com.blog.som.domain.blog.dto.BlogPostList;
import com.blog.som.domain.member.entity.MemberEntity;
import com.blog.som.domain.post.mongo.document.PostDocument;
import com.blog.som.domain.post.mongo.respository.MongoPostRepository;
import com.blog.som.global.constant.NumberConstant;
import com.blog.som.global.constant.SearchConstant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
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
class MainServiceTest {

  @Mock
  private MongoPostRepository mongoPostRepository;

  @InjectMocks
  private MainServiceImpl mainService;

  private MemberEntity member;

  private List<PostDocument> getPostDocumentList() {
    member = EntityCreator.createMember(1L);
    List<PostDocument> PostDocumentList = new ArrayList<>();

    for (int i = 1; i <= 5; i++) {
      PostDocumentList.add(EntityCreator.createPostDocument(EntityCreator.createPost(100L + i, member)));
    }
    return PostDocumentList;
  }

  private PageRequest getPageDefaultRequest(int page) {
    return PageRequest.of(page - 1, NumberConstant.DEFAULT_PAGE_SIZE, Sort.by(SearchConstant.VIEWS).descending());
  }

  @Test
  @DisplayName("전체 글 조회 - hot")
  void getAllPostListHot() {
    List<PostDocument> PostDocumentList = this.getPostDocumentList();

    int page = 1;
    PageRequest pageRequest = getPageDefaultRequest(page);

    //given
    when(mongoPostRepository.findRecentAndHighViews(LocalDate.now().minusMonths(6L), pageRequest))
        .thenReturn(new PageImpl<>(PostDocumentList));

    //when
    BlogPostList result = mainService.getAllPostListHot(page);

    //then
    for (BlogPostDto bp : result.getPostList()) {
      assertThat(bp.getMemberId()).isEqualTo(member.getMemberId());
      assertThat(bp.getAccountName()).isEqualTo(member.getAccountName());
    }
    assertThat(result.getPageDto().getCurrentPage()).isEqualTo(1);
    assertThat(result.getPageDto().getTotalElement()).isEqualTo(5);
  }

  @Test
  @DisplayName("전체 글 조회 - latest")
  void getAllPostListLatest() {
    List<PostDocument> PostDocumentList = this.getPostDocumentList();

    int page = 1;
    PageRequest pageRequest = PageRequest.of(page - 1,
        NumberConstant.DEFAULT_PAGE_SIZE, Sort.by(SearchConstant.REGISTERED_AT).descending());

    //given
    when(mongoPostRepository.findRecentAndHighViews(LocalDate.now().minusMonths(6L), pageRequest))
        .thenReturn(new PageImpl<>(PostDocumentList));

    //when
    BlogPostList result = mainService.getAllPostListLatest(page);

    //then
    for (BlogPostDto bp : result.getPostList()) {
      assertThat(bp.getMemberId()).isEqualTo(member.getMemberId());
      assertThat(bp.getAccountName()).isEqualTo(member.getAccountName());
    }
    assertThat(result.getPageDto().getCurrentPage()).isEqualTo(1);
    assertThat(result.getPageDto().getTotalElement()).isEqualTo(5);
  }

  @Test
  @DisplayName("전체 글 검색 - title 또는 introduction")
  void searchAllPostByTitleOrIntroduction() {
    List<PostDocument> PostDocumentList = this.getPostDocumentList();

    int page = 1;
    PageRequest pageRequest = getPageDefaultRequest(page);
    String query = "test";

    //given
    when(mongoPostRepository.
        findByTitleContainingOrIntroductionContaining(query, query, pageRequest))
        .thenReturn(new PageImpl<>(PostDocumentList));

    //when
    BlogPostList result = mainService.searchAllPostByTitleOrIntroduction(query, page);

    //then
    for (BlogPostDto bp : result.getPostList()) {
      assertThat(bp.getMemberId()).isEqualTo(member.getMemberId());
      assertThat(bp.getAccountName()).isEqualTo(member.getAccountName());
    }
    assertThat(result.getPageDto().getCurrentPage()).isEqualTo(1);
    assertThat(result.getPageDto().getTotalElement()).isEqualTo(5);
  }

  @Test
  @DisplayName("전체 글 검색 - content")
  void searchAllPostByContent() {
    List<PostDocument> PostDocumentList = this.getPostDocumentList();

    int page = 1;
    PageRequest pageRequest = getPageDefaultRequest(page);
    String query = "test";

    //given
    when(mongoPostRepository
        .findByContentContaining(query, pageRequest))
        .thenReturn(new PageImpl<>(PostDocumentList));

    //when
    BlogPostList result = mainService.searchAllPostByContent(query, page);

    //then
    for (BlogPostDto bp : result.getPostList()) {
      assertThat(bp.getMemberId()).isEqualTo(member.getMemberId());
      assertThat(bp.getAccountName()).isEqualTo(member.getAccountName());
    }
    assertThat(result.getPageDto().getCurrentPage()).isEqualTo(1);
    assertThat(result.getPageDto().getTotalElement()).isEqualTo(5);
  }

  @Test
  @DisplayName("전체 글 검색 - tag")
  void searchAllPostByTag() {
    List<PostDocument> PostDocumentList = this.getPostDocumentList();

    int page = 1;
    PageRequest pageRequest = getPageDefaultRequest(page);
    String query = "test";

    //given
    when(mongoPostRepository
        .findByTagsContaining(query, pageRequest))
        .thenReturn(new PageImpl<>(PostDocumentList));

    //when
    BlogPostList result = mainService.searchAllPostByTag(query, page);

    //then
    for (BlogPostDto bp : result.getPostList()) {
      assertThat(bp.getMemberId()).isEqualTo(member.getMemberId());
      assertThat(bp.getAccountName()).isEqualTo(member.getAccountName());
    }
    assertThat(result.getPageDto().getCurrentPage()).isEqualTo(1);
    assertThat(result.getPageDto().getTotalElement()).isEqualTo(5);
  }
}