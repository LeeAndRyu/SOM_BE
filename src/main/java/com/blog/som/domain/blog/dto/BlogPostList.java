package com.blog.som.domain.blog.dto;

import com.blog.som.global.dto.PageDto;
import java.util.List;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Builder
public class BlogPostList {

  private PageDto pageDto;
  private List<BlogPostDto> postList;

  public BlogPostList (PageDto pageDto, List<BlogPostDto> postList){
    this.pageDto = pageDto;
    this.postList = postList;
  }
}
