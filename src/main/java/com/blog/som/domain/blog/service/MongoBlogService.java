package com.blog.som.domain.blog.service;

import com.blog.som.domain.blog.constant.FollowConstant;
import com.blog.som.domain.blog.dto.BlogMemberDto;
import com.blog.som.domain.blog.dto.BlogPostDto;
import com.blog.som.domain.blog.dto.BlogPostList;
import com.blog.som.domain.blog.dto.BlogTagListDto;
import com.blog.som.domain.follow.service.FollowService;
import com.blog.som.domain.member.entity.MemberEntity;
import com.blog.som.domain.member.repository.MemberRepository;
import com.blog.som.domain.post.mongo.document.PostDocument;
import com.blog.som.domain.post.mongo.respository.MongoPostRepository;
import com.blog.som.domain.tag.dto.TagDto;
import com.blog.som.domain.tag.repository.TagRepository;
import com.blog.som.global.constant.NumberConstant;
import com.blog.som.global.constant.SearchConstant;
import com.blog.som.global.dto.PageDto;
import com.blog.som.global.exception.ErrorCode;
import com.blog.som.global.exception.custom.BlogException;
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
public class MongoBlogService implements BlogService {

  private final MemberRepository memberRepository;
  private final TagRepository tagRepository;
  private final FollowService followService;
  private final MongoPostRepository mongoPostRepository;

  @Override
  public BlogMemberDto getBlogMember(String accountName) {
    MemberEntity member = memberRepository.findByAccountName(accountName)
        .orElseThrow(() -> new BlogException(ErrorCode.BLOG_NOT_FOUND));

    return BlogMemberDto.fromEntity(member);
  }

  @Override
  public BlogTagListDto getBlogTags(String accountName) {
    MemberEntity member = memberRepository.findByAccountName(accountName)
        .orElseThrow(() -> new BlogException(ErrorCode.BLOG_NOT_FOUND));

    List<TagDto> tagDtoList = tagRepository.findAllByMember(member)
        .stream()
        .map(TagDto::fromEntity)
        .toList();

    int count = mongoPostRepository.countByAccountName(accountName);

    return new BlogTagListDto(count, tagDtoList);
  }

  @Override
  public void validateAccountName(String accountName) {
    if (!memberRepository.existsByAccountName(accountName)) {
      throw new BlogException(ErrorCode.BLOG_NOT_FOUND);
    }
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
  public BlogPostList getAllBlogPostListBySortType(String accountName, String sort, int page) {
    String sortBy = SearchConstant.REGISTERED_AT;

    if (sort.equals(SearchConstant.HOT)) {
      sortBy = SearchConstant.VIEWS;
    }
    PageRequest pageRequest = PageRequest.of(page - 1, NumberConstant.DEFAULT_PAGE_SIZE, Sort.by(sortBy).descending());

    Page<PostDocument> searchPageResult =
        mongoPostRepository.findByAccountName(accountName, pageRequest);

    return this.getBlogPostListBySearchPage(searchPageResult);
  }

  @Override
  public BlogPostList getBlogPostListByTag(String accountName, String tagName, int page) {
    PageRequest pageRequest =
        PageRequest.of(page - 1, NumberConstant.DEFAULT_PAGE_SIZE,
            Sort.by(SearchConstant.REGISTERED_AT).descending());
    Page<PostDocument> searchPageResult =
        mongoPostRepository.findByAccountNameAndTagsContaining(accountName, tagName, pageRequest);

    return this.getBlogPostListBySearchPage(searchPageResult);
  }

  @Override
  public BlogPostList getBlogPostListByQuery(String accountName, String query, int page) {
    PageRequest pageRequest =
        PageRequest.of(page - 1, NumberConstant.DEFAULT_PAGE_SIZE,
            Sort.by(SearchConstant.REGISTERED_AT).descending());
    Page<PostDocument> searchPageResult =
        mongoPostRepository
            .findByAccountNameAndTitleContainingOrIntroductionContaining(
                accountName, query, query, pageRequest);

    return this.getBlogPostListBySearchPage(searchPageResult);
  }

  private BlogPostList getBlogPostListBySearchPage(Page<PostDocument> searchPage) {
    List<BlogPostDto> blogPostDtoList = searchPage.getContent()
        .stream()
        .map(BlogPostDto::fromDocument)
        .toList();
    return new BlogPostList(PageDto.fromDocumentPage(searchPage), blogPostDtoList);
  }
}
