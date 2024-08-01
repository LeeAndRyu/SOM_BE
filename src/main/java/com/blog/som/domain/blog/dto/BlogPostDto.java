package com.blog.som.domain.blog.dto;

import com.blog.som.domain.mybatis.BlogPostWithTagString;
import com.blog.som.domain.post.entity.PostEntity;
import com.blog.som.domain.post.mongo.document.PostDocument;
import java.time.LocalDateTime;
import java.util.Arrays;
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
  public static BlogPostDto fromBlogPostWithTagString(BlogPostWithTagString blogPost) {
    List<String> tagList = Arrays.stream(blogPost.getTags().split(", ")).toList();
    return BlogPostDto.builder()
            .postId(blogPost.getPostId())
            .memberId(blogPost.getMemberId())
            .profileImage(blogPost.getProfileImage())
            .accountName(blogPost.getAccountName())
            .title(blogPost.getTitle())
            .thumbnail(blogPost.getThumbnail())
            .introduction(blogPost.getIntroduction())
            .likes(blogPost.getLikes())
            .views(blogPost.getViews())
            .comments(blogPost.getComments())
            .registeredAt(blogPost.getRegisteredAt())
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

}

