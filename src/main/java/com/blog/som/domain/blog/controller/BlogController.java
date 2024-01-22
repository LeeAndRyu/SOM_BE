package com.blog.som.domain.blog.controller;


import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = "블로그(blog)")
@RequiredArgsConstructor
@RestController
public class BlogController {

  @ApiOperation("Blog main")
  @GetMapping("/{blogName}")
  public ResponseEntity<?> blogMain(@PathVariable String blogName){

      return ResponseEntity.ok(null);
  }

}
