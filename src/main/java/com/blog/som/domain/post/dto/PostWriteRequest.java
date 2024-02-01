package com.blog.som.domain.post.dto;

import com.blog.som.domain.member.entity.MemberEntity;
import com.blog.som.domain.post.entity.PostEntity;
import java.time.LocalDateTime;
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
public class PostWriteRequest {

  private String title;

  private String content;

  private String thumbnail;

  private String introduction;

  private List<String> tags;

  private List<String> totalImageList;

  public static PostEntity toEntity(PostWriteRequest request, MemberEntity member){
    return PostEntity.builder()
        .member(member)
        .title(request.getTitle())
        .content(request.getContent())
        .thumbnail(request.getThumbnail())
        .introduction(request.getIntroduction())
        .lastModifiedAt(LocalDateTime.now())
        .build();
  }
}
