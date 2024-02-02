package com.blog.som.domain.notification.dto;

import com.blog.som.domain.member.entity.MemberEntity;
import com.blog.som.domain.notification.entity.NotificationEntity;
import com.blog.som.domain.notification.type.NotificationSituation;

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

  public static NotificationCreateDto comment(){
    return NotificationCreateDto.builder()
        .build();
  }

  public static NotificationCreateDto follow(){
    return NotificationCreateDto.builder()
        .build();
  }

  public static NotificationCreateDto likes(){
    return NotificationCreateDto.builder()
        .build();
  }



}
