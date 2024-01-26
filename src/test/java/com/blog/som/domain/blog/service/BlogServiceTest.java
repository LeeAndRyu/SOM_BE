package com.blog.som.domain.blog.service;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import com.blog.som.EntityCreator;
import com.blog.som.domain.blog.constant.FollowConstant;
import com.blog.som.domain.blog.dto.BlogMemberDto;
import com.blog.som.domain.follow.entity.FollowEntity;
import com.blog.som.domain.follow.service.FollowService;
import com.blog.som.domain.member.entity.MemberEntity;
import com.blog.som.domain.member.repository.MemberRepository;
import com.blog.som.domain.post.repository.PostRepository;
import com.blog.som.domain.tag.repository.PostTagRepository;
import com.blog.som.domain.tag.repository.TagRepository;
import com.blog.som.global.exception.ErrorCode;
import com.blog.som.global.exception.custom.BlogException;
import com.blog.som.global.exception.custom.MemberException;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@Slf4j
@ExtendWith(MockitoExtension.class)
class BlogServiceTest {

  @Mock
  private MemberRepository memberRepository;
  @Mock
  private PostRepository postRepository;
  @Mock
  private TagRepository tagRepository;
  @Mock
  private PostTagRepository postTagRepository;
  @Mock
  private FollowService followService;

  @InjectMocks
  private BlogServiceImpl blogService;


  @Nested
  @DisplayName("Blog 메인 페이지 조회 - 회원")
  class GetBlogMember{
    @Test
    @DisplayName("성공")
    void getBlogMember(){
      MemberEntity member = EntityCreator.createMember(1L);
      String accountName=  member.getAccountName();

      //given
      when(memberRepository.findByAccountName(accountName))
          .thenReturn(Optional.of(member));

      //when
      BlogMemberDto result = blogService.getBlogMember(accountName);

      //then
      assertThat(result.getBlogName()).isEqualTo(member.getBlogName());
      assertThat(result.getProfileImage()).isEqualTo(member.getProfileImage());
      assertThat(result.getNickname()).isEqualTo(member.getNickname());
      assertThat(result.getIntroduction()).isEqualTo(member.getIntroduction());
      assertThat(result.getFollowerCount()).isEqualTo(member.getFollowerCount());
      assertThat(result.getFollowingCount()).isEqualTo(member.getFollowingCount());
      assertThat(result.getLoginMemberFollowStatus()).isNull();
    }

    @Test
    @DisplayName("실패 : BLOG_NOT_FOUND")
    void getBlogMember_BLOG_NOT_FOUND(){
      MemberEntity member = EntityCreator.createMember(1L);
      String accountName=  member.getAccountName();

      //given
      when(memberRepository.findByAccountName(accountName))
          .thenReturn(Optional.empty());

      //when
      //then
      BlogException blogException =
          assertThrows(BlogException.class, () -> blogService.getBlogMember(accountName));
      assertThat(blogException.getErrorCode()).isEqualTo(ErrorCode.BLOG_NOT_FOUND);
    }
  }

  @Nested
  @DisplayName("Blog 메인 페이지 조회 - 팔로우 여부")
  class GetFollowStatus{
    @Test
    @DisplayName("성공 : 팔로우 상태")
    void getFollowStatus_FOLLOWED(){
      MemberEntity fromMember = EntityCreator.createMember(1L);
      MemberEntity toMember = EntityCreator.createMember(2L);
      String toMemberAccountName = toMember.getAccountName();

      //given
      when(followService.isFollowing(1L, toMemberAccountName))
          .thenReturn(true);
      //when
      String followStatus = blogService.getFollowStatus(1L, toMemberAccountName);

      //then
      assertThat(followStatus).isEqualTo(FollowConstant.FOLLOWED);
    }

    @Test
    @DisplayName("성공 : 팔로우 하지 않는 상태")
    void getFollowStatus_NOT_FOLLOWED(){
      MemberEntity fromMember = EntityCreator.createMember(1L);
      MemberEntity toMember = EntityCreator.createMember(2L);
      String toMemberAccountName = toMember.getAccountName();

      //given
      when(followService.isFollowing(1L, toMemberAccountName))
          .thenReturn(false);
      //when
      String followStatus = blogService.getFollowStatus(1L, toMemberAccountName);

      //then
      assertThat(followStatus).isEqualTo(FollowConstant.NOT_FOLLOWED);
    }
  }



}