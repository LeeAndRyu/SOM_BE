package com.blog.som.domain.tag.dto;

import com.blog.som.domain.tag.entity.TagEntity;
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
public class TagDto {

  private Long tagId;

  private String tagName;

  private int tagCount;

  public static TagDto fromEntity(TagEntity tag){
    return TagDto.builder()
        .tagId(tag.getTagId())
        .tagName(tag.getTagName())
        .tagCount(tag.getCount())
        .build();
  }
}
