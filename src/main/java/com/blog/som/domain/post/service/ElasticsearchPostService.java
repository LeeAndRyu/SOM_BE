package com.blog.som.domain.post.service;

import com.blog.som.domain.member.entity.MemberEntity;
import com.blog.som.domain.member.repository.MemberRepository;
import com.blog.som.domain.post.elasticsearch.document.PostEsDocument;
import com.blog.som.domain.post.dto.PostDeleteResponse;
import com.blog.som.domain.post.dto.PostDto;
import com.blog.som.domain.post.dto.PostEditRequest;
import com.blog.som.domain.post.dto.PostWriteRequest;
import com.blog.som.domain.post.entity.PostEntity;
import com.blog.som.domain.post.elasticsearch.repository.ElasticsearchPostRepository;
import com.blog.som.domain.post.repository.PostRepository;
import com.blog.som.domain.tag.entity.PostTagEntity;
import com.blog.som.domain.tag.entity.TagEntity;
import com.blog.som.domain.tag.repository.PostTagRepository;
import com.blog.som.domain.tag.repository.TagRepository;
import com.blog.som.global.exception.ErrorCode;
import com.blog.som.global.exception.custom.MemberException;
import com.blog.som.global.exception.custom.PostException;
import com.blog.som.global.redis.email.CacheRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class ElasticsearchPostService implements PostService {

  private final PostRepository postRepository;
  private final MemberRepository memberRepository;
  private final TagRepository tagRepository;
  private final PostTagRepository postTagRepository;
  private final CacheRepository cacheRepository;
  private final ElasticsearchPostRepository elasticSearchPostRepository;

  @Override
  public PostDto writePost(PostWriteRequest request, Long memberId) {
    MemberEntity member = memberRepository.findById(memberId)
        .orElseThrow(() -> new MemberException(ErrorCode.MEMBER_NOT_FOUND));

    PostEntity post = postRepository.save(PostWriteRequest.toEntity(request, member));

    //tagList를 lower case로 변환
    List<String> tagList = request.getTags().stream().map(String::toLowerCase).toList();

    this.handleNewTags(tagList, member, post);

    //es에 저장
    elasticSearchPostRepository.save(PostEsDocument.fromEntity(post, tagList));

    return PostDto.fromEntity(post, tagList);
  }

  @Override
  public PostDto getPost(Long postId, String accessUserAgent) {
    PostEntity post = postRepository.findById(postId)
        .orElseThrow(() -> new PostException(ErrorCode.POST_NOT_FOUND));

    List<String> tagList;

    Optional<PostEsDocument> optionalPostDocument = elasticSearchPostRepository.findById(post.getPostId());

    PostEsDocument postEsDocument;

    if(optionalPostDocument.isPresent()){
      postEsDocument = optionalPostDocument.get();
      tagList = postEsDocument.getTags();
    }else{
      log.info("elasticSearch postDocument [id={}] not found", post.getPostId());
      tagList = postTagRepository.findAllByPost(post)
          .stream()
          .map(pt -> pt.getTag().getTagName())
          .toList();
      postEsDocument = elasticSearchPostRepository.save(PostEsDocument.fromEntity(post, tagList));
    }

    if(StringUtils.hasText(accessUserAgent) && cacheRepository.canAddView(accessUserAgent, postId)){
      post.addView();
      postRepository.save(post);
      postEsDocument.addView();
      elasticSearchPostRepository.save(postEsDocument);
    }

    return PostDto.fromEntity(post, tagList);
  }

  @Override
  public PostDto editPost(PostEditRequest postEditRequest, Long postId, Long loginMemberId) {
    PostEntity post = postRepository.findById(postId)
        .orElseThrow(() -> new PostException(ErrorCode.POST_NOT_FOUND));
    MemberEntity member = post.getMember();

    if(!Objects.equals(member.getMemberId(), loginMemberId)){
      throw new PostException(ErrorCode.POST_EDIT_NO_AUTHORITY);
    }

    post.editPost(postEditRequest);
    postRepository.save(post);

    List<String> requestList = postEditRequest.getTags().stream().map(String::toLowerCase).toList();
    List<String> editRequestTags = new ArrayList<>(requestList);

    for (PostTagEntity postTag : postTagRepository.findAllByPost(post)) {
      //DB에도 있고, request에도 있는 경우 ( 그대로 인 경우 )
      String currentTagName = postTag.getTag().getTagName();
      if(editRequestTags.contains(currentTagName)){
        editRequestTags.remove(currentTagName);
        continue;
      }

      //DB에는 있지만, request에 없는 경우 ( 기존 Entity 삭제 )
      postTagRepository.delete(postTag);//Tag보다 PostTag를 항상 먼저 삭제해야 한다.
      TagEntity currentTag = postTag.getTag();

      if(currentTag.getCount() <= 1){
        tagRepository.delete(currentTag);
      }else{
        currentTag.minusCount();
        tagRepository.save(currentTag);
      }

    }
    log.info("[PostService.editPost()] remain List tags : {} ", editRequestTags);
    this.handleNewTags(editRequestTags, member, post);

    elasticSearchPostRepository.deleteById(postId);
    elasticSearchPostRepository.save(PostEsDocument.fromEntity(post, requestList));

    return PostDto.fromEntity(post, requestList);
  }

  private void handleNewTags(List<String> tagList, MemberEntity member, PostEntity post) {
    for (String tagName : tagList) {
      Optional<TagEntity> optionalTag = tagRepository.findByTagNameAndMember(tagName, member);
      TagEntity tagEntity;

      //tagName이 이미 존재할 때 : 기존 tagEntity의 count + 1
      if (optionalTag.isPresent()) {
        tagEntity = optionalTag.get();
        tagEntity.addCount();
      } //tagName이 존재하지 않을 때 : 새로운 tagEntity 저장
      else {
        tagEntity = new TagEntity(tagName, member);
      }

      tagRepository.save(tagEntity);
      postTagRepository.save(new PostTagEntity(post, tagEntity, member));
    }
  }

  @Override
  public PostDeleteResponse deletePost(Long postId, Long loginMemberId) {
    PostEntity post = postRepository.findById(postId)
        .orElseThrow(() -> new PostException(ErrorCode.POST_NOT_FOUND));
    MemberEntity member = post.getMember();

    if(!Objects.equals(member.getMemberId(), loginMemberId)){
      throw new PostException(ErrorCode.POST_DELETE_NO_AUTHORITY);
    }

    for(PostTagEntity postTag : postTagRepository.findAllByPost(post)){
      TagEntity tag = postTag.getTag();

      postTagRepository.delete(postTag);

      if(tag.getCount() <= 1){
        tagRepository.delete(tag);
      }else{
        tag.minusCount();
        tagRepository.save(tag);
      }
    }
    postRepository.delete(post);

    elasticSearchPostRepository.deleteById(postId);

    return new PostDeleteResponse(post.getPostId(), post.getTitle());
  }

}
