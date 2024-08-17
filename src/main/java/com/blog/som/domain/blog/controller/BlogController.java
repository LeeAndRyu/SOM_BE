package com.blog.som.domain.blog.controller;



import com.blog.som.domain.blog.dto.BlogMemberDto;
import com.blog.som.domain.blog.dto.BlogPostList;
import com.blog.som.domain.blog.dto.BlogTagListDto;
import com.blog.som.domain.blog.service.BlogService;
import com.blog.som.domain.member.security.userdetails.LoginMember;
import com.blog.som.domain.member.type.Role;
import com.blog.som.global.constant.SearchConstant;
import com.blog.som.global.exception.ErrorCode;
import com.blog.som.global.exception.custom.BlogException;
import com.blog.som.domain.follow.type.FollowStatus;
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
      blogMember.setFollowStatus(
          blogService.getFollowStatus(loginMember.getMemberId(), accountName));
    }

    return ResponseEntity.ok(blogMember);
  }

  @ApiOperation("블로그 태그 리스트 조회")
  @GetMapping("/blog/{accountName}/tags")
  public ResponseEntity<BlogTagListDto> blogTags(@PathVariable String accountName){

    BlogTagListDto blogTags = blogService.getBlogTags(accountName);

    return ResponseEntity.ok(blogService.getBlogTags(accountName));
  }



  @ApiOperation(value = "블로그 게시글 list 조회", notes = "sort: latest/hot/tag/query")
  @GetMapping("/blog/{accountName}/posts")
  public ResponseEntity<BlogPostList> blogPosts(@PathVariable String accountName,
      @RequestParam(value = "sort", required = false, defaultValue = "latest") String sort,
      @RequestParam(value = "q", required = false, defaultValue = "")String query,
      @RequestParam(value = "p",required = false, defaultValue = "1") int page){
    
    //accountName이 존재하는지 검증
    blogService.validateAccountName(accountName);

    BlogPostList blogPostList = blogService.getBlogPosts(accountName, sort, query, page);

    return ResponseEntity.ok(blogPostList);

  }

}
