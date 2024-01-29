package com.blog.som.domain.main.controller;

import com.blog.som.domain.blog.dto.BlogPostList;
import com.blog.som.domain.main.service.MainService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Api(tags = "SOM 메인(main)")
@RequiredArgsConstructor
@RestController
public class MainController {

  private final MainService mainService;

  @ApiOperation("메인 페이지")
  @GetMapping("/main")
  public ResponseEntity<BlogPostList> mainPage(@RequestParam(required = false, defaultValue = "hot") String sort,
      @RequestParam(value = "p", required = false, defaultValue = "1") int page) {

    if (sort.equals("latest")) {
      return ResponseEntity.ok(mainService.getAllPostListLatest(page));
    }

    return ResponseEntity.ok(mainService.getAllPostListHot(page));
  }

  @ApiOperation("메인 페이지 - 검색")
  @GetMapping("/search")
  public ResponseEntity<BlogPostList> mainPageSearch(
      @RequestParam(required = false, defaultValue = "title") String type,
      @RequestParam(value = "q", required = false, defaultValue = "") String query,
      @RequestParam(value = "p", required = false, defaultValue = "1") int page) {

    if (!StringUtils.hasText(query)) {
      return ResponseEntity.ok(mainService.getAllPostListHot(page));
    }

    if (type.equals("title")) {
      return ResponseEntity.ok(mainService.searchAllPostByTitleOrIntroduction(query, page));
    }

    if (type.equals("content")) {
      return ResponseEntity.ok(mainService.searchAllPostByContent(query, page));
    }

    if (type.equals("tag")) {
      return ResponseEntity.ok(mainService.searchAllPostByTag(query, page));
    }

    //type이 일치하지 않을 떄
    return ResponseEntity.ok(mainService.getAllPostListHot(page));
  }

}
