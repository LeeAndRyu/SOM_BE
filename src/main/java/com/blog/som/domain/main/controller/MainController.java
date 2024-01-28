package com.blog.som.domain.main.controller;

import com.blog.som.domain.blog.dto.BlogPostList;
import com.blog.som.domain.main.service.MainService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
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
      @RequestParam(value = "p",required = false, defaultValue = "1") int page){
    BlogPostList mainPageList = mainService.getMainPageList(sort, page);

    return ResponseEntity.ok(mainPageList);
  }
}
