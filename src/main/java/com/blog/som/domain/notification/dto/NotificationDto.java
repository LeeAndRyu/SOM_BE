package com.blog.som.domain.notification.dto;

import com.blog.som.domain.notification.entity.NotificationEntity;
import com.blog.som.domain.notification.type.NotificationSituation;
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
public class NotificationDto {

  private Long notificationId;

  private Long memberId;

  private String profileImage;

  private NotificationSituation notificationSituation;

  private Long targetEntityId;

  private String title;

  private String message1;

  private String message2;

  private String url;

  private LocalDateTime createdAt;

  private LocalDateTime readAt;

  public static NotificationDto fromEntity(NotificationEntity notification){
    return NotificationDto.builder()
        .notificationId(notification.getNotificationId())
        .memberId(notification.getMember().getMemberId())
        .notificationSituation(notification.getNotificationSituation())
        .targetEntityId(notification.getTargetEntityId())
        .profileImage(notification.getProfileImage())
        .title(notification.getTitle())
        .message1(notification.getMessage1())
        .message2(notification.getMessage2())
        .url(notification.getUrl())
        .createdAt(notification.getCreatedAt())
        .readAt(notification.getReadAt())
        .build();
  }
}
