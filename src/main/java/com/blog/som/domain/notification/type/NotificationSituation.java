package com.blog.som.domain.notification.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum NotificationSituation {
  COMMENT,
  FOLLOWED,
  LIKES
  ;

}
