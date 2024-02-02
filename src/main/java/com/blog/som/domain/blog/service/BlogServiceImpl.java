package com.blog.som.domain.blog.service;

import com.blog.som.domain.blog.dto.BlogMemberDto;
import com.blog.som.domain.blog.dto.BlogPostDto;
import com.blog.som.domain.blog.dto.BlogPostList;
import com.blog.som.domain.blog.dto.BlogTagListDto;
import com.blog.som.domain.follow.service.FollowService;
import com.blog.som.domain.member.entity.MemberEntity;
import com.blog.som.domain.member.repository.MemberRepository;
import com.blog.som.domain.post.entity.PostEntity;
import com.blog.som.domain.post.repository.PostRepository;
import com.blog.som.domain.tag.dto.TagDto;
import com.blog.som.domain.tag.entity.PostTagEntity;
import com.blog.som.domain.tag.entity.TagEntity;
import com.blog.som.domain.tag.repository.PostTagRepository;
import com.blog.som.domain.tag.repository.TagRepository;
import com.blog.som.global.constant.NumberConstant;
import com.blog.som.global.constant.SearchConstant;
import com.blog.som.global.dto.PageDto;
import com.blog.som.global.exception.ErrorCode;
import com.blog.som.global.exception.custom.BlogException;
import com.blog.som.domain.follow.type.FollowStatus;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

@Slf4j
@RequiredArgsConstructor
//@Service
public class BlogServiceImpl implements BlogService {

  private final MemberRepository memberRepository;
  private final PostRepository postRepository;
  private final TagRepository tagRepository;
  private final PostTagRepository postTagRepository;
  private final FollowService followService;

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

    int count = postRepository.countByMember(member);

    return new BlogTagListDto(count, tagDtoList);
  }

  @Override
  public void validateAccountName(String accountName) {
    if(!memberRepository.existsByAccountName(accountName)){
      throw new BlogException(ErrorCode.BLOG_NOT_FOUND);
    }
  }

  @Override
  public FollowStatus getFollowStatus(Long memberId, String accountName) {
    boolean result = followService.isFollowing(memberId, accountName);
    if (result) {
      return FollowStatus.FOLLOWED;
    }
    return FollowStatus.NOT_FOLLOWED;
  }

  @Override
  public BlogPostList getAllBlogPostListBySortType(String accountName, String sort, int page) {
    MemberEntity member = memberRepository.findByAccountName(accountName)
        .orElseThrow(() -> new BlogException(ErrorCode.BLOG_NOT_FOUND));

    String sortBy = SearchConstant.REGISTERED_AT;
    if (sort.equals(SearchConstant.HOT)) {
      sortBy = SearchConstant.VIEWS;
    }


    Page<PostEntity> posts =
        postRepository.findByMember(member,
            PageRequest.of(page - 1, NumberConstant.DEFAULT_PAGE_SIZE, Sort.by(sortBy).descending()));

    List<BlogPostDto> blogPostList =
        posts.getContent().stream()
            .map(this::getBlogPostDtoFromPostEntity)
            .toList();

    return new BlogPostList(PageDto.fromPostEntityPage(posts), blogPostList);
  }

  @Override
  public BlogPostList getBlogPostListByTag(String accountName, String tagName, int page) {
    MemberEntity member = memberRepository.findByAccountName(accountName)
        .orElseThrow(() -> new BlogException(ErrorCode.BLOG_NOT_FOUND));

    TagEntity tag = tagRepository.findByTagNameAndMember(tagName, member)
        .orElseThrow(() -> new BlogException(ErrorCode.TAG_NOT_FOUND));

    PageRequest pageRequest =
        PageRequest.of(page - 1, NumberConstant.DEFAULT_PAGE_SIZE,
            Sort.by(SearchConstant.POST_CREATE_TIME).descending());

    Page<PostTagEntity> postTags = postTagRepository.findByMemberAndTag(member, tag, pageRequest);

    List<PostEntity> posts = postTags.getContent().stream().map(PostTagEntity::getPost).toList();

    List<BlogPostDto> blogPostList = posts.stream().map(this::getBlogPostDtoFromPostEntity).toList();


    return new BlogPostList(PageDto.fromPostTagEntityEntityPage(postTags), blogPostList);
  }

  @Override
  public BlogPostList getBlogPostListByQuery(String accountName, String query, int page) {
    MemberEntity member = memberRepository.findByAccountName(accountName)
        .orElseThrow(() -> new BlogException(ErrorCode.BLOG_NOT_FOUND));

    PageRequest pageRequest =
        PageRequest.of(page - 1, NumberConstant.DEFAULT_PAGE_SIZE,
            Sort.by(SearchConstant.REGISTERED_AT).descending());

    Page<PostEntity> posts =
        postRepository.findByMemberAndTitleContainingOrIntroductionContaining(member, query, query, pageRequest);

    List<BlogPostDto> blogPostList = posts.getContent().stream().map(this::getBlogPostDtoFromPostEntity).toList();

    return new BlogPostList(PageDto.fromPostEntityPage(posts), blogPostList);
  }


  private BlogPostDto getBlogPostDtoFromPostEntity(PostEntity postEntity) {
    List<String> tagList =
        postTagRepository.findAllByPost(postEntity)
            .stream()
            .map(pt -> pt.getTag().getTagName())
            .toList();
    return BlogPostDto.fromEntity(postEntity, tagList);
  }
}
