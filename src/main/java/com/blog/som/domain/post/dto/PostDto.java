package com.blog.som.domain.post.dto;

import com.blog.som.domain.post.entity.PostEntity;
import java.time.LocalDateTime;
import java.util.List;
import javax.persistence.EntityListeners;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class PostDto {

  private Long postId;

  private Long memberId;

  private String accountName;

  private String title;

  private String content;

  private String thumbnail;

  private String introduction;

  private int likes;

  private int views;

  private LocalDateTime registeredAt;

  private LocalDateTime lastModifiedAt;

  private List<String> tags;

  public static PostDto fromEntity(PostEntity post, List<String> tagList) {
    return PostDto.builder()
        .postId(post.getPostId())
        .memberId(post.getMember().getMemberId())
        .accountName(post.getMember().getAccountName())
        .title(post.getTitle())
        .content(post.getContent())
        .thumbnail(post.getThumbnail())
        .introduction(post.getIntroduction())
        .likes(post.getLikes())
        .views(post.getViews())
        .registeredAt(post.getRegisteredAt())
        .lastModifiedAt(post.getLastModifiedAt())
        .tags(tagList)
        .build();
  }
}
