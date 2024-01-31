package com.blog.som.domain.main.service;

import com.blog.som.domain.blog.dto.BlogPostDto;
import com.blog.som.domain.blog.dto.BlogPostList;
import com.blog.som.domain.post.mongo.document.PostDocument;
import com.blog.som.domain.post.mongo.respository.MongoPostRepository;
import com.blog.som.global.constant.NumberConstant;
import com.blog.som.global.constant.SearchConstant;
import com.blog.som.global.dto.PageDto;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class MainServiceImpl implements MainService {

  private final MongoPostRepository mongoPostRepository;

  @Override
  public BlogPostList getAllPostListHot(int page) {

    Page<PostDocument> searchPageResult = mongoPostRepository
        .findRecentAndHighViews(LocalDateTime.now().minusMonths(6L), this.getBasicPageRequest(page));

    return this.getBlogPostListBySearchPage(searchPageResult);
  }

  @Override
  public BlogPostList getAllPostListLatest(int page) {
    PageRequest pageRequest =
        PageRequest.of(page - 1,
            NumberConstant.DEFAULT_PAGE_SIZE, Sort.by(SearchConstant.REGISTERED_AT).descending());

    Page<PostDocument> searchPageResult = mongoPostRepository
        .findRecentAndHighViews(LocalDateTime.now().minusMonths(6L), pageRequest);

    return this.getBlogPostListBySearchPage(searchPageResult);
  }

  @Override
  public BlogPostList searchAllPostByTitleOrIntroduction(String query, int page) {
    Page<PostDocument> searchPageResult = mongoPostRepository
        .findByTitleContainingOrIntroductionContaining(query, query, this.getBasicPageRequest(page));
    return this.getBlogPostListBySearchPage(searchPageResult);
  }

  @Override
  public BlogPostList searchAllPostByContent(String query, int page) {
    Page<PostDocument> searchPageResult =
        mongoPostRepository.findByContentContaining(query, this.getBasicPageRequest(page));
    return this.getBlogPostListBySearchPage(searchPageResult);
  }

  @Override
  public BlogPostList searchAllPostByTag(String query, int page) {
    Page<PostDocument> searchPageResult =
        mongoPostRepository.findByTagsContaining(query, getBasicPageRequest(page));
    return this.getBlogPostListBySearchPage(searchPageResult);
  }

  private BlogPostList getBlogPostListBySearchPage(Page<PostDocument> searchPage) {
    List<BlogPostDto> blogPostDtoList = searchPage.getContent()
        .stream()
        .map(BlogPostDto::fromDocument)
        .toList();
    return new BlogPostList(PageDto.fromDocumentPage(searchPage), blogPostDtoList);
  }

  private PageRequest getBasicPageRequest(int page) {
    return PageRequest.of(page - 1, NumberConstant.DEFAULT_PAGE_SIZE,
        Sort.by(SearchConstant.VIEWS).descending());
  }
}
