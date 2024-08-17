package com.blog.som.global.dto;

import com.blog.som.domain.post.entity.PostEntity;
import com.blog.som.domain.post.mongo.document.PostDocument;
import com.blog.som.domain.tag.entity.PostTagEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.domain.Page;

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

  public static PageDto fromPageConstants(int dataSize, int page, int pageSize){
    return PageDto.builder()
            .currentPage(page)
            .currentElements(dataSize)
            .pageSize(pageSize)
            .totalElement(100)
            .totalPages(100)
            .build();
  }

  public static PageDto fromPostEntityPage(Page<PostEntity> page) {
    return PageDto.builder()
        .currentPage(page.getNumber() + 1)
        .currentElements(page.getNumberOfElements())
        .pageSize(page.getSize())
        .totalElement((int) page.getTotalElements())
        .totalPages(page.getTotalPages())
        .build();
  }

  public static PageDto fromPostTagEntityEntityPage(Page<PostTagEntity> page) {
    return PageDto.builder()
        .currentPage(page.getNumber() + 1)
        .currentElements(page.getNumberOfElements())
        .pageSize(page.getSize())
        .totalElement((int) page.getTotalElements())
        .totalPages(page.getTotalPages())
        .build();
  }

  public static PageDto fromDocumentPage(Page<PostDocument> page) {
    return PageDto.builder()
        .currentPage(page.getNumber() + 1)
        .currentElements(page.getNumberOfElements())
        .pageSize(page.getSize())
        .totalElement((int) page.getTotalElements())
        .totalPages(page.getTotalPages())
        .build();
  }

}
