package com.blog.som.domain.notification.dto;

import com.blog.som.domain.comment.entity.CommentEntity;
import com.blog.som.domain.member.entity.MemberEntity;
import com.blog.som.domain.notification.entity.NotificationEntity;
import com.blog.som.domain.notification.type.NotificationSituation;

import com.blog.som.domain.post.entity.PostEntity;
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
public class NotificationCreateDto {

  private NotificationSituation notificationSituation;
  private Long targetEntityId;
  private String title;
  private String message1;
  private String message2;
  private String url;

  public static NotificationEntity toEntity(
      MemberEntity member, MemberEntity writer, NotificationCreateDto notificationCreateDto){
    return NotificationEntity.builder()
        .member(member)
        .writer(writer)
        .notificationSituation(notificationCreateDto.getNotificationSituation())
        .targetEntityId(notificationCreateDto.getTargetEntityId())
        .title(notificationCreateDto.getTitle())
        .message1(notificationCreateDto.getMessage1())
        .message2(notificationCreateDto.getMessage2())
        .url(notificationCreateDto.getUrl())
        .build();
  }

  public static NotificationCreateDto comment(String writerNickname, PostEntity post, CommentEntity comment){
    String title = "<strong>" + writerNickname + "</strong>" + "님이 댓글을 남겼습니다.";
    String message1 = comment.getContent();
    String message2 = post.getTitle();
    String url = "/blog/" + post.getMember().getAccountName() + "/" + post.getPostId();

    return NotificationCreateDto.builder()
        .notificationSituation(NotificationSituation.COMMENT)
        .targetEntityId(comment.getCommentId())
        .title(title)
        .message1(message1)
        .message2(message2)
        .url(url)
        .build();
  }

  public static NotificationCreateDto follow(MemberEntity fromMember, Long followId){
    String title = fromMember.getNickname() + " 님이 팔로우 하였습니다.";
    String message1 = "[" + fromMember.getBlogName() + "] <- 블로그 방문하기";
    String message2 = "";
    String url = "/blog/" + fromMember.getAccountName();

    return NotificationCreateDto.builder()
        .notificationSituation(NotificationSituation.FOLLOWED)
        .targetEntityId(followId)
        .title(title)
        .message1(message1)
        .message2(message2)
        .url(url)
        .build();
  }

  public static NotificationCreateDto likes(MemberEntity fromMember, PostEntity post){
    String title = fromMember.getNickname() + " 님이 게시글을 좋아합니다.";
    String message1 = post.getTitle();
    String message2 = "";
    String url = "/blog/" + post.getMember().getAccountName() + "/" + post.getPostId();

    return NotificationCreateDto.builder()
        .notificationSituation(NotificationSituation.LIKES)
        .targetEntityId(post.getPostId())
        .title(title)
        .message1(message1)
        .message2(message2)
        .url(url)
        .build();
  }



}
