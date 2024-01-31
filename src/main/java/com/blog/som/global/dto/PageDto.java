package com.blog.som.global.dto;

import com.blog.som.domain.post.elasticsearch.document.PostEsDocument;
import com.blog.som.domain.post.entity.PostEntity;
import com.blog.som.domain.tag.entity.PostTagEntity;
import org.springframework.data.domain.Page;

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
public class PageDto {

  private int currentPage; //현재 페이지
  private int currentElements; //현재 페이지 데이터 개수
  private int pageSize; //한 페이지 크기
  private int totalElement; //전체 데이터 개수
  private int totalPages; //전체 페이지 개수

  public static PageDto fromPostEntityPage(Page<PostEntity> page){
    return PageDto.builder()
        .currentPage(page.getNumber() + 1)
        .currentElements(page.getNumberOfElements())
        .pageSize(page.getSize())
        .totalElement((int)page.getTotalElements())
        .totalPages(page.getTotalPages())
        .build();
  }

  public static PageDto fromPostTagEntityEntityPage(Page<PostTagEntity> page){
    return PageDto.builder()
        .currentPage(page.getNumber() + 1)
        .currentElements(page.getNumberOfElements())
        .pageSize(page.getSize())
        .totalElement((int)page.getTotalElements())
        .totalPages(page.getTotalPages())
        .build();
  }

  public static PageDto fromPostDocumentPage(Page<PostEsDocument> page){
    return PageDto.builder()
        .currentPage(page.getNumber() + 1)
        .currentElements(page.getNumberOfElements())
        .pageSize(page.getSize())
        .totalElement((int)page.getTotalElements())
        .totalPages(page.getTotalPages())
        .build();
  }



}
