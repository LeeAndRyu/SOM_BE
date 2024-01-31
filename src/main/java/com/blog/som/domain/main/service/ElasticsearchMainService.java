package com.blog.som.domain.main.service;

import com.blog.som.domain.blog.dto.BlogPostDto;
import com.blog.som.domain.blog.dto.BlogPostList;
import com.blog.som.domain.post.elasticsearch.document.PostEsDocument;
import com.blog.som.domain.post.elasticsearch.repository.ElasticsearchPostRepository;
import com.blog.som.global.constant.NumberConstant;
import com.blog.som.global.constant.SearchConstant;
import com.blog.som.global.dto.PageDto;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
//@Service
public class ElasticsearchMainService implements MainService {

  private final ElasticsearchPostRepository elasticsearchPostRepository;

  @Override
  public BlogPostList getAllPostListHot(int page) {
    Page<PostEsDocument> searchPageResult =
        elasticsearchPostRepository.findAll(this.getBasicPageRequest(page));

    return this.getBlogPostListBySearchPage(searchPageResult);
  }

  @Override
  public BlogPostList getAllPostListLatest(int page) {
    PageRequest pageRequest =
        PageRequest.of(page - 1,
            NumberConstant.DEFAULT_PAGE_SIZE, Sort.by(SearchConstant.REGISTERED_AT).descending());

    Page<PostEsDocument> searchPageResult = elasticsearchPostRepository.findAll(pageRequest);

    return this.getBlogPostListBySearchPage(searchPageResult);
  }

  @Override
  public BlogPostList searchAllPostByTitleOrIntroduction(String query, int page) {
    Page<PostEsDocument> searchPageResult = elasticsearchPostRepository.
        findByTitleContainingOrIntroductionContaining(query, query, this.getBasicPageRequest(page));
    return this.getBlogPostListBySearchPage(searchPageResult);
  }

  @Override
  public BlogPostList searchAllPostByContent(String query, int page) {
    Page<PostEsDocument> searchPageResult =
        elasticsearchPostRepository
            .findByContentContaining(query, this.getBasicPageRequest(page));
    return this.getBlogPostListBySearchPage(searchPageResult);
  }

  @Override
  public BlogPostList searchAllPostByTag(String query, int page) {
    Page<PostEsDocument> searchPageResult =
        elasticsearchPostRepository.findByTagsContaining(query, this.getBasicPageRequest(page));
    return this.getBlogPostListBySearchPage(searchPageResult);
  }

  private BlogPostList getBlogPostListBySearchPage(Page<PostEsDocument> searchPage) {
    List<BlogPostDto> blogPostDtoList = searchPage.getContent()
        .stream()
        .map(BlogPostDto::fromEsDocument)
        .toList();
    return new BlogPostList(PageDto.fromEsDocumentPage(searchPage), blogPostDtoList);
  }

  private PageRequest getBasicPageRequest(int page) {
    return PageRequest.of(page - 1, NumberConstant.DEFAULT_PAGE_SIZE,
        Sort.by(SearchConstant.VIEWS).descending());
  }
}
