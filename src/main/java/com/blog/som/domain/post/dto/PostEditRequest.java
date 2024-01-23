package com.blog.som.domain.post.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PostEditRequest {

  private String title;

  private String content;

  private String thumbnail;

  private String introduction;

  private List<String> tags;
}
