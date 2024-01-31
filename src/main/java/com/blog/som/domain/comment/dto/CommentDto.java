package com.blog.som.domain.comment.dto;

import com.blog.som.domain.comment.entity.CommentEntity;
import java.time.LocalDateTime;

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
public class CommentDto {
  private Long commentId;

  private String content;

  private Long postId;

  private Long writerId;

  private String writerAccountName;

  private String writerNickname;

  private String writerProfileImage;

  private LocalDateTime registeredAt;

  private LocalDateTime lastModifiedAt;

  public static CommentDto fromEntity(CommentEntity comment){
    return CommentDto.builder()
        .commentId(comment.getCommentId())
        .content(comment.getContent())
        .postId(comment.getPost().getPostId())
        .writerId(comment.getMember().getMemberId())
        .writerAccountName(comment.getMember().getAccountName())
        .writerNickname(comment.getMember().getNickname())
        .writerProfileImage(comment.getMember().getProfileImage())
        .registeredAt(comment.getRegisteredAt())
        .lastModifiedAt(comment.getLastModifiedAt())
        .build();
  }
}
