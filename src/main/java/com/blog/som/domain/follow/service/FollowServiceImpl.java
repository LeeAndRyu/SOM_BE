package com.blog.som.domain.follow.service;

import com.blog.som.domain.follow.dto.FollowCancelResponse;
import com.blog.som.domain.follow.dto.FollowDto;
import com.blog.som.domain.follow.entity.FollowEntity;
import com.blog.som.domain.follow.repository.FollowRepository;
import com.blog.som.domain.member.entity.MemberEntity;
import com.blog.som.domain.member.repository.MemberRepository;
import com.blog.som.global.exception.ErrorCode;
import com.blog.som.global.exception.custom.BlogException;
import com.blog.som.global.exception.custom.FollowException;
import com.blog.som.global.exception.custom.MemberException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class FollowServiceImpl implements FollowService{

  private final FollowRepository followRepository;
  private final MemberRepository memberRepository;

  @Override
  public FollowDto doFollow(Long fromMemberId, String blogAccountName) {
    MemberEntity fromMember = memberRepository.findById(fromMemberId)
        .orElseThrow(() -> new MemberException(ErrorCode.MEMBER_NOT_FOUND));
    MemberEntity toMember = memberRepository.findByAccountName(blogAccountName)
        .orElseThrow(() -> new BlogException(ErrorCode.BLOG_NOT_FOUND));

    if(followRepository.existsByFromMemberAndToMember(fromMember, toMember)){
      throw new FollowException(ErrorCode.ALREADY_FOLLOWED);
    }
    fromMember.addFollowingCount();
    toMember.addFollowerCount();

    FollowEntity saved = followRepository.save(new FollowEntity(fromMember, toMember));

    memberRepository.save(fromMember);
    memberRepository.save(toMember);

    return FollowDto.fromEntity(saved);
  }

  @Override
  public FollowCancelResponse cancelFollow(Long fromMemberId, String blogAccountName) {
    MemberEntity fromMember = memberRepository.findById(fromMemberId)
        .orElseThrow(() -> new MemberException(ErrorCode.MEMBER_NOT_FOUND));
    MemberEntity toMember = memberRepository.findByAccountName(blogAccountName)
        .orElseThrow(() -> new BlogException(ErrorCode.BLOG_NOT_FOUND));

    FollowEntity followEntity = followRepository.findByFromMemberAndToMember(fromMember, toMember)
        .orElseThrow(() -> new FollowException(ErrorCode.FOLLOW_NOT_FOUND));

    followRepository.delete(followEntity);

    fromMember.minusFollowingCount();
    toMember.minusFollowerCount();

    memberRepository.save(fromMember);
    memberRepository.save(toMember);

    return FollowCancelResponse.fromEntity(followEntity);
  }

  @Override
  public boolean isFollowing(Long fromMemberId, String blogAccountName) {
    MemberEntity fromMember = memberRepository.findById(fromMemberId)
        .orElseThrow(() -> new MemberException(ErrorCode.MEMBER_NOT_FOUND));
    MemberEntity toMember = memberRepository.findByAccountName(blogAccountName)
        .orElseThrow(() -> new BlogException(ErrorCode.BLOG_NOT_FOUND));

    return followRepository.existsByFromMemberAndToMember(fromMember, toMember);
  }
}
