package com.blog.som.domain.blog.controller;



import com.blog.som.domain.blog.dto.BlogMemberDto;
import com.blog.som.domain.blog.dto.BlogPostList;
import com.blog.som.domain.blog.service.BlogService;
import com.blog.som.domain.member.security.userdetails.LoginMember;
import com.blog.som.domain.member.type.Role;
import com.blog.som.global.exception.ErrorCode;
import com.blog.som.global.exception.custom.BlogException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Api(tags = "블로그(blog)")
@RequiredArgsConstructor
@RestController
public class BlogController {

  private final BlogService blogService;

  @ApiOperation("블로그 회원 정보 조회")
  @GetMapping("/blog/{accountName}/member")
  public ResponseEntity<BlogMemberDto> blogMember(@PathVariable String accountName,
      @AuthenticationPrincipal LoginMember loginMember){

    BlogMemberDto blogMember = blogService.getBlogMember(accountName);

    if(loginMember.getRole().equals(Role.USER)){
      blogMember.setLoginMemberFollowStatus(
          blogService.getFollowStatus(loginMember.getMemberId(), accountName));
    }

    return ResponseEntity.ok(blogMember);
  }

  @ApiOperation("블로그 게시글 list 조회")
  @GetMapping("/blog/{accountName}/posts")
  public ResponseEntity<BlogPostList> blogPosts(@PathVariable String accountName,
      @RequestParam(value = "sort", required = false, defaultValue = "latest") String sort,
      @RequestParam(value = "tag", required = false, defaultValue = "") String tagName,
      @RequestParam(value = "q", required = false, defaultValue = "")String query,
      @RequestParam(value = "p",required = false, defaultValue = "1") int page){
    //두가지 모두 query로 들어올 순 없음
    if (StringUtils.hasText(tagName) && StringUtils.hasText(query)) {
      throw new BlogException(ErrorCode.BLOG_POSTS_INVALID_QUERY);
    }
    //hot
    if(sort.equals("hot")){
      return ResponseEntity.ok(blogService.getBlogPostListBySortType(accountName, sort, page));
    }
    //tag 검색
    if(StringUtils.hasText(tagName)){
      return ResponseEntity.ok(blogService.getBlogPostListByTag(accountName, tagName, page));
    }

    //query 검색
    if(StringUtils.hasText(query)){
      return ResponseEntity.ok(blogService.getBlogPostListByQuery(accountName, query, page));
    }

    //전체 검색
    return ResponseEntity.ok(blogService.getBlogPostListBySortType(accountName, sort, page));
  }

}
