package com.blog.som.domain.post.service;

import com.blog.som.domain.member.entity.MemberEntity;
import com.blog.som.domain.member.repository.MemberRepository;
import com.blog.som.domain.post.dto.PostDeleteResponse;
import com.blog.som.domain.post.dto.PostDto;
import com.blog.som.domain.post.dto.PostEditRequest;
import com.blog.som.domain.post.dto.PostWriteRequest;
import com.blog.som.domain.post.entity.PostEntity;
import com.blog.som.domain.post.mongo.document.PostDocument;
import com.blog.som.domain.post.mongo.respository.MongoPostRepository;
import com.blog.som.domain.post.repository.PostRepository;
import com.blog.som.domain.tag.entity.PostTagEntity;
import com.blog.som.domain.tag.entity.TagEntity;
import com.blog.som.domain.tag.repository.PostTagRepository;
import com.blog.som.domain.tag.repository.TagRepository;
import com.blog.som.global.exception.ErrorCode;
import com.blog.som.global.exception.custom.MemberException;
import com.blog.som.global.exception.custom.PostException;
import com.blog.som.global.redis.email.CacheRepository;
import com.blog.som.global.s3.S3ImageService;
import com.blog.som.global.util.HtmlParser;
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
public class PostServiceImpl implements PostService {

  private final PostRepository postRepository;
  private final MemberRepository memberRepository;
  private final TagRepository tagRepository;
  private final PostTagRepository postTagRepository;
  private final CacheRepository cacheRepository;
  private final S3ImageService s3ImageService;

  @Override
  public PostDto writePost(PostWriteRequest request, Long memberId) {
    MemberEntity member = memberRepository.findById(memberId)
        .orElseThrow(() -> new MemberException(ErrorCode.MEMBER_NOT_FOUND));
    PostEntity post = postRepository.save(PostWriteRequest.toEntity(request, member));

    //tagList를 lower case로 변환
    List<String> tagList = request.getTags().stream().map(String::toLowerCase).toList();

    this.handleNewTags(tagList, member, post);

    this.deleteUnUsedImageFromS3(request.getTotalImageList(), post.getThumbnail(), post.getContent());

    return PostDto.fromEntity(post, tagList);
  }

  @Override
  public PostDto getPost(Long postId, String accessUserAgent) {
    PostEntity post = postRepository.findById(postId)
        .orElseThrow(() -> new PostException(ErrorCode.POST_NOT_FOUND));
    List<String> tags = postTagRepository.findPostTagNamesByPost(post);

    if (StringUtils.hasText(accessUserAgent) && cacheRepository.canAddView(accessUserAgent, postId)) {
      post.addView();
    }

    return PostDto.fromEntity(post, tags);
  }

  @Override
  public PostDto editPost(PostEditRequest request, Long postId, Long loginMemberId) {
    PostEntity post = postRepository.findById(postId)
        .orElseThrow(() -> new PostException(ErrorCode.POST_NOT_FOUND));
    MemberEntity member = post.getMember();

    if (!Objects.equals(member.getMemberId(), loginMemberId)) {
      throw new PostException(ErrorCode.POST_EDIT_NO_AUTHORITY);
    }
    post.editPost(request);

    //태그 수정 시작
    List<String> requestList = request.getTags().stream().map(String::toLowerCase).toList();
    List<String> editRequestTags = new ArrayList<>(requestList);

    for (PostTagEntity postTag : postTagRepository.findAllByPost(post)) {
      //DB에도 있고, request에도 있는 경우 ( 그대로 인 경우 )
      String currentTagName = postTag.getTag().getTagName();
      if (editRequestTags.contains(currentTagName)) {
        editRequestTags.remove(currentTagName);
        continue;
      }

      //DB에는 있지만, request에 없는 경우 ( 기존 Entity 삭제 )
      postTagRepository.delete(postTag);//Tag보다 PostTag를 항상 먼저 삭제해야 한다.
      TagEntity currentTag = postTag.getTag();

      if (currentTag.getCount() <= 1) {
        tagRepository.delete(currentTag);
      } else {
        currentTag.minusCount();
        tagRepository.save(currentTag);
      }

    }
    log.info("[PostService.editPost()] remain List tags : {} ", editRequestTags);
    this.handleNewTags(editRequestTags, member, post); //태그 수정 끝

    //안쓰는 이미지 삭제
    this.deleteUnUsedImageFromS3(request.getTotalImageList(), post.getThumbnail(), post.getContent());

    return PostDto.fromEntity(post, requestList);
  }

  @Override
  public List<String> getImagesFromPost(Long postId) {
    PostEntity post = postRepository.findById(postId)
        .orElseThrow(() -> new PostException(ErrorCode.POST_NOT_FOUND));

    List<String> imageList = HtmlParser.getImageList(post.getContent());
    if(!post.getThumbnail().isBlank()){
      imageList.add(post.getThumbnail());
    }

    return imageList;
  }

  /**
   * request 받은 List<String> tagList -> tagEntity와 postTagEntity 처리
   */
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

  /**
   * 게시글 편집 과정에서 사용했으나, 최종 사용되지 않는 이미지를 S3에서 삭제
   */
  private void deleteUnUsedImageFromS3(List<String> requestTotalList, String thumbnail, String content) {
    requestTotalList.remove(thumbnail);//전체 리스트에서 썸네일 제외
    List<String> useImageList = HtmlParser.getImageList(content); //content에 포함된 image list

    //전체 리스트에서 content에 포함된 image list 제거
    useImageList.stream().forEach(requestTotalList::remove);

    //requestTotalList에 남아 있는 것은 사용되지 않는 image list
    requestTotalList.stream().forEach(s3ImageService::deleteImageFromS3);
  }


  @Override
  public PostDeleteResponse deletePost(Long postId, Long loginMemberId) {
    PostEntity post = postRepository.findById(postId)
        .orElseThrow(() -> new PostException(ErrorCode.POST_NOT_FOUND));
    MemberEntity member = post.getMember();

    if (!Objects.equals(member.getMemberId(), loginMemberId)) {
      throw new PostException(ErrorCode.POST_DELETE_NO_AUTHORITY);
    }

    for (PostTagEntity postTag : postTagRepository.findAllByPost(post)) {
      TagEntity tag = postTag.getTag();

      postTagRepository.delete(postTag);

      if (tag.getCount() <= 1) {
        tagRepository.delete(tag);
      } else {
        tag.minusCount();
        tagRepository.save(tag);
      }
    }
    postRepository.delete(post);


    //S3에서 이미지 삭제
    List<String> imageList = HtmlParser.getImageList(post.getContent());
    imageList.stream().forEach(s3ImageService::deleteImageFromS3);
    if(!post.getThumbnail().isBlank()){
      s3ImageService.deleteImageFromS3(post.getThumbnail());
    }

    return new PostDeleteResponse(post.getPostId(), post.getTitle());
  }

}
