package com.blog.som.domain.post.service;

import com.blog.som.domain.member.entity.MemberEntity;
import com.blog.som.domain.member.repository.MemberRepository;
import com.blog.som.domain.post.dto.PostDto;
import com.blog.som.domain.post.dto.PostWriteRequest;
import com.blog.som.domain.post.entity.PostEntity;
import com.blog.som.domain.post.repository.PostRepository;
import com.blog.som.domain.tag.entity.PostTagEntity;
import com.blog.som.domain.tag.entity.TagEntity;
import com.blog.som.domain.tag.repository.PostTagRepository;
import com.blog.som.domain.tag.repository.TagRepository;
import com.blog.som.global.exception.ErrorCode;
import com.blog.som.global.exception.custom.MemberException;
import com.blog.som.global.exception.custom.PostException;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class PostServiceImpl implements PostService {

  private final PostRepository postRepository;
  private final MemberRepository memberRepository;
  private final TagRepository tagRepository;
  private final PostTagRepository postTagRepository;

  @Override
  public PostDto writePost(PostWriteRequest request, Long memberId) {
    MemberEntity member = memberRepository.findById(memberId)
        .orElseThrow(() -> new MemberException(ErrorCode.MEMBER_NOT_FOUND));

    PostEntity post = postRepository.save(PostWriteRequest.toEntity(request, member));

    //tagList를 lower case로 변환
    List<String> tagList = request.getTags().stream().map(String::toLowerCase).toList();

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
      postTagRepository.save(new PostTagEntity(post, tagEntity));
    }

    return PostDto.fromEntity(post, tagList);
  }

  @Override
  public PostDto getPost(Long postId) {
    PostEntity post = postRepository.findById(postId)
        .orElseThrow(() -> new PostException(ErrorCode.POST_NOT_FOUND));

    List<String> tagList = postTagRepository.findAllByPost(post)
        .stream()
        .map(pt -> pt.getTag().getTagName())
        .toList();

    return PostDto.fromEntity(post, tagList);
  }
}
