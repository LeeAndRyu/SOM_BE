package com.blog.som.domain.main.service;

import com.blog.som.domain.blog.dto.BlogPostDto;
import com.blog.som.domain.blog.dto.BlogPostList;
import com.blog.som.domain.member.entity.MemberEntity;
import com.blog.som.domain.member.repository.MemberRepository;
import com.blog.som.domain.post.elasticsearch.document.PostDocument;
import com.blog.som.domain.post.elasticsearch.repository.ElasticsearchPostRepository;
import com.blog.som.global.constant.NumberConstant;
import com.blog.som.global.constant.SearchConstant;
import com.blog.som.global.dto.PageDto;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class ElasticsearchMainService implements MainService {

  private final ElasticsearchPostRepository elasticsearchPostRepository;
  private final MemberRepository memberRepository;

  @Override
  public BlogPostList getAllPostListHot(int page) {
    Page<PostDocument> searchPageResult =
        elasticsearchPostRepository.findAll(this.getBasicPageRequest(page));

    return this.getBlogPostListBySearchPage(searchPageResult);
  }

  @Override
  public BlogPostList getAllPostListLatest(int page) {
    PageRequest pageRequest =
        PageRequest.of(page - 1,
            NumberConstant.DEFAULT_PAGE_SIZE, Sort.by(SearchConstant.REGISTERED_AT).descending());

    Page<PostDocument> searchPageResult = elasticsearchPostRepository.findAll(pageRequest);

    return this.getBlogPostListBySearchPage(searchPageResult);
  }

  @Override
  public BlogPostList searchAllPostByTitleOrIntroduction(String query, int page) {
    Page<PostDocument> searchPageResult = elasticsearchPostRepository.
        findByTitleContainingOrIntroductionContaining(query, query, this.getBasicPageRequest(page));
    return this.getBlogPostListBySearchPage(searchPageResult);
  }

  @Override
  public BlogPostList searchAllPostByContent(String query, int page) {
    Page<PostDocument> searchPageResult =
        elasticsearchPostRepository
            .findByContentContaining(query, this.getBasicPageRequest(page));
    return this.getBlogPostListBySearchPage(searchPageResult);
  }

  @Override
  public BlogPostList searchAllPostByTag(String query, int page) {
    Page<PostDocument> searchPageResult =
        elasticsearchPostRepository.findByTagsContaining(query, this.getBasicPageRequest(page));
    return this.getBlogPostListBySearchPage(searchPageResult);
  }

  private BlogPostList getBlogPostListBySearchPage(Page<PostDocument> searchPage) {
    List<BlogPostDto> blogPostDtoList = new ArrayList<>();
    for(PostDocument pd : searchPage.getContent()){
      Optional<MemberEntity> optionalMember = memberRepository.findByAccountName(pd.getAccountName());
      if(optionalMember.isPresent()){
        blogPostDtoList.add(BlogPostDto.fromDocument(pd, optionalMember.get().getProfileImage()));
      }
    }
    return new BlogPostList(PageDto.fromPostDocumentPage(searchPage), blogPostDtoList);
  }

  private PageRequest getBasicPageRequest(int page) {
    return PageRequest.of(page - 1, NumberConstant.DEFAULT_PAGE_SIZE,
        Sort.by(SearchConstant.VIEWS).descending());
  }
}
