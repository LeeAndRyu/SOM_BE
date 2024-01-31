package com.blog.som.domain.blog.dto;

import com.blog.som.domain.post.elasticsearch.document.PostEsDocument;
import com.blog.som.domain.post.entity.PostEntity;
import com.blog.som.domain.post.mongo.document.PostDocument;
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
public class BlogPostDto {

  private Long postId;

  private Long memberId;

  private String profileImage;

  private String accountName;

  private String title;

  private String thumbnail;

  private String introduction;

  private int likes;

  private int views;

  private int comments;

  private LocalDateTime registeredAt;

  private List<String> tags;

  public static BlogPostDto fromEntity(PostEntity post, List<String> tagList) {
    return BlogPostDto.builder()
        .postId(post.getPostId())
        .memberId(post.getMember().getMemberId())
        .profileImage(post.getMember().getProfileImage())
        .accountName(post.getMember().getAccountName())
        .title(post.getTitle())
        .thumbnail(post.getThumbnail())
        .introduction(post.getIntroduction())
        .likes(post.getLikes())
        .views(post.getViews())
        .comments(post.getComments())
        .registeredAt(post.getRegisteredAt())
        .tags(tagList)
        .build();
  }

  public static BlogPostDto fromDocument(PostDocument postDocument) {
    return BlogPostDto.builder()
        .postId(postDocument.getPostId())
        .memberId(postDocument.getMemberId())
        .profileImage(postDocument.getProfileImage())
        .accountName(postDocument.getAccountName())
        .title(postDocument.getTitle())
        .thumbnail(postDocument.getThumbnail())
        .introduction(postDocument.getIntroduction())
        .likes(postDocument.getLikes())
        .views(postDocument.getViews())
        .comments(postDocument.getComments())
        .registeredAt(postDocument.getRegisteredAt())
        .tags(postDocument.getTags())
        .build();
  }


  public static BlogPostDto fromEsDocument(PostEsDocument postEsDocument) {
    return BlogPostDto.builder()
        .postId(postEsDocument.getPostId())
        .memberId(postEsDocument.getMemberId())
        .profileImage(postEsDocument.getProfileImage())
        .accountName(postEsDocument.getAccountName())
        .title(postEsDocument.getTitle())
        .thumbnail(postEsDocument.getThumbnail())
        .introduction(postEsDocument.getIntroduction())
        .likes(postEsDocument.getLikes())
        .views(postEsDocument.getViews())
        .registeredAt(postEsDocument.getRegisteredAt())
        .tags(postEsDocument.getTags())
        .build();
  }

}

