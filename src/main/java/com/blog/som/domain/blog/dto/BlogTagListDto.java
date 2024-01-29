package com.blog.som.domain.blog.dto;

import com.blog.som.domain.tag.dto.TagDto;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BlogTagListDto {

  private int totalPostCount;

  private List<TagDto> tagList;
}
