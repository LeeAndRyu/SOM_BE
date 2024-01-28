package com.blog.som.domain.blog.service;

import com.blog.som.domain.blog.constant.FollowConstant;
import com.blog.som.domain.blog.dto.BlogMemberDto;
import com.blog.som.domain.blog.dto.BlogPostDto;
import com.blog.som.domain.blog.dto.BlogPostList;
import com.blog.som.domain.follow.service.FollowService;
import com.blog.som.domain.member.entity.MemberEntity;
import com.blog.som.domain.member.repository.MemberRepository;
import com.blog.som.domain.post.elasticsearch.document.PostDocument;
import com.blog.som.domain.post.elasticsearch.repository.ElasticSearchPostRepository;
import com.blog.som.domain.post.entity.PostEntity;
import com.blog.som.domain.post.repository.PostRepository;
import com.blog.som.global.constant.NumberConstant;
import com.blog.som.global.constant.SearchConstant;
import com.blog.som.global.dto.PageDto;
import com.blog.som.global.exception.ErrorCode;
import com.blog.som.global.exception.custom.BlogException;
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
public class ElasticsearchBlogService implements BlogService {

  private final MemberRepository memberRepository;
  private final PostRepository postRepository;
  private final FollowService followService;
  private final ElasticSearchPostRepository elasticSearchPostRepository;

  @Override
  public BlogMemberDto getBlogMember(String accountName) {
    MemberEntity member = memberRepository.findByAccountName(accountName)
        .orElseThrow(() -> new BlogException(ErrorCode.BLOG_NOT_FOUND));

    return BlogMemberDto.fromEntity(member);
  }

  @Override
  public String getFollowStatus(Long memberId, String accountName) {
    boolean result = followService.isFollowing(memberId, accountName);
    if (result) {
      return FollowConstant.FOLLOWED;
    }
    return FollowConstant.NOT_FOLLOWED;
  }

  @Override
  public BlogPostList getBlogPostListBySortType(String accountName, String sort, int page) {
    String sortBy = SearchConstant.REGISTERED_AT;

    if (sort.equals(SearchConstant.HOT)) {
      sortBy = SearchConstant.VIEWS;
    }

    PageRequest pageRequest = PageRequest.of(page - 1, NumberConstant.DEFAULT_PAGE_SIZE, Sort.by(sortBy).descending());

    Page<PostDocument> documents = elasticSearchPostRepository.findAllByAccountName(accountName, pageRequest);

    List<BlogPostDto> blogPostDtoList =
        documents.getContent()
            .stream()
            .map(BlogPostDto::fromDocument).toList();
    return new BlogPostList(PageDto.fromPostDocumentPage(documents), blogPostDtoList);
  }

  @Override
  public BlogPostList getBlogPostListByTag(String accountName, String tagName, int page) {
    PageRequest pageRequest =
        PageRequest.of(page - 1, NumberConstant.DEFAULT_PAGE_SIZE,
            Sort.by(SearchConstant.REGISTERED_AT).descending());

    Page<PostDocument> pageDocument = elasticSearchPostRepository.findByAccountNameAndTagsContaining(
        accountName, tagName, pageRequest);
    List<BlogPostDto> blogPostDtoList = pageDocument.getContent().stream().map(BlogPostDto::fromDocument).toList();

    return new BlogPostList(PageDto.fromPostDocumentPage(pageDocument), blogPostDtoList);
  }

  @Override
  public BlogPostList getBlogPostListByQuery(String accountName, String query, int page) {
    MemberEntity member = memberRepository.findByAccountName(accountName)
        .orElseThrow(() -> new BlogException(ErrorCode.BLOG_NOT_FOUND));

    PageRequest pageRequest =
        PageRequest.of(page - 1, NumberConstant.DEFAULT_PAGE_SIZE,
            Sort.by(SearchConstant.REGISTERED_AT).descending());

    //LikeQuery를 사용하는게 낫다.
    Page<PostEntity> posts =
        postRepository.findByMemberAndTitleContainingOrIntroductionContaining(member, query, query, pageRequest);

    List<BlogPostDto> blogPostDtoList =
        posts.stream()
            .map(p -> elasticSearchPostRepository.findByPostId(p.getPostId()))
            .filter(Optional::isPresent)
            .map(op -> BlogPostDto.fromDocument(op.get()))
            .toList();

    return new BlogPostList(PageDto.fromPostEntityPage(posts), blogPostDtoList);
  }
}
