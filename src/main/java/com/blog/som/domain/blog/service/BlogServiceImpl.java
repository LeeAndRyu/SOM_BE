package com.blog.som.domain.blog.service;

import com.blog.som.domain.blog.dto.BlogMemberDto;
import com.blog.som.domain.blog.dto.BlogPostDto;
import com.blog.som.domain.blog.dto.BlogPostList;
import com.blog.som.domain.member.entity.MemberEntity;
import com.blog.som.domain.member.repository.MemberRepository;
import com.blog.som.domain.post.entity.PostEntity;
import com.blog.som.domain.post.repository.PostRepository;
import com.blog.som.domain.tag.entity.PostTagEntity;
import com.blog.som.domain.tag.entity.TagEntity;
import com.blog.som.domain.tag.repository.PostTagRepository;
import com.blog.som.domain.tag.repository.TagRepository;
import com.blog.som.global.constant.NumberConstant;
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
public class BlogServiceImpl implements BlogService {

  private final MemberRepository memberRepository;
  private final PostRepository postRepository;
  private final TagRepository tagRepository;
  private final PostTagRepository postTagRepository;

  @Override
  public BlogMemberDto getBlogMember(String accountName) {
    MemberEntity member = memberRepository.findByAccountName(accountName)
        .orElseThrow(() -> new BlogException(ErrorCode.BLOG_NOT_FOUND));

    return BlogMemberDto.fromEntity(member);
  }

  @Override
  public BlogPostList getBlogPostListBySortType(String accountName, String sort, int page) {
    MemberEntity member = memberRepository.findByAccountName(accountName)
        .orElseThrow(() -> new BlogException(ErrorCode.BLOG_NOT_FOUND));

    String sortBy = "registeredAt";
    if(sort.equals("hot")){
      sortBy = "views";
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
            Sort.by("postCreatedTime").descending());

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
            Sort.by("registeredAt").descending());

    Page<PostEntity> posts =
        postRepository.findByTitleContainingOrIntroductionContaining(query, query, pageRequest);

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
