package com.blog.som.domain.likes.service;

import com.blog.som.domain.likes.dto.LikesResponse;
import com.blog.som.domain.likes.entity.LikesEntity;
import com.blog.som.domain.likes.repository.LikesRepository;
import com.blog.som.domain.member.entity.MemberEntity;
import com.blog.som.domain.member.repository.MemberRepository;
import com.blog.som.domain.post.entity.PostEntity;
import com.blog.som.domain.post.mongo.service.MongoPostService;
import com.blog.som.domain.post.repository.PostRepository;
import com.blog.som.global.exception.ErrorCode;
import com.blog.som.global.exception.custom.MemberException;
import com.blog.som.global.exception.custom.PostException;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class LikesServiceImpl implements LikesService {

  private final PostRepository postRepository;
  private final MemberRepository memberRepository;
  private final LikesRepository likesRepository;

  @Override
  public LikesResponse.ToggleResult toggleLikes(Long postId, Long loginMemberId) {
    PostEntity post = postRepository.findById(postId)
        .orElseThrow(() -> new PostException(ErrorCode.POST_NOT_FOUND));
    MemberEntity member = memberRepository.findById(loginMemberId)
        .orElseThrow(() -> new MemberException(ErrorCode.MEMBER_NOT_FOUND));

    Optional<LikesEntity> optionalLikes = likesRepository.findByMemberAndPost(member, post);

    if (optionalLikes.isEmpty()) {
      likesRepository.save(new LikesEntity(member, post));

      post.addLikes();
      postRepository.save(post);

      return new LikesResponse.ToggleResult(true, loginMemberId, postId);
    }
    likesRepository.delete(optionalLikes.get());

    post.minusLikes();
    postRepository.save(post);

    return new LikesResponse.ToggleResult(false, loginMemberId, postId);
  }


  @Override
  public LikesResponse.MemberLikesPost memberLikesPost(Long postId, Long loginMemberId) {
    PostEntity post = postRepository.findById(postId)
        .orElseThrow(() -> new PostException(ErrorCode.POST_NOT_FOUND));
    MemberEntity member = memberRepository.findById(loginMemberId)
        .orElseThrow(() -> new MemberException(ErrorCode.MEMBER_NOT_FOUND));

    if (likesRepository.existsByMemberAndPost(member, post)) {
      return new LikesResponse.MemberLikesPost(true, loginMemberId, postId);
    }
    return new LikesResponse.MemberLikesPost(false, loginMemberId, postId);
  }
}
