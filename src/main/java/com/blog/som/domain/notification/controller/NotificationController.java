package com.blog.som.domain.notification.controller;

import com.blog.som.domain.member.security.userdetails.LoginMember;
import com.blog.som.domain.notification.dto.NotificationDto;
import com.blog.som.domain.notification.service.NotificationService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import java.util.List;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Api(tags = "알림(Notification)")
@Slf4j
@RequiredArgsConstructor
@RestController
public class NotificationController {

  private final NotificationService notificationService;

  @ApiOperation(value = "구독을 시작하기 위한 메서드")
  @GetMapping(value = "/sse/subscribe", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
  public SseEmitter subscribe(@AuthenticationPrincipal LoginMember loginMember,
      HttpServletResponse response) {
    response.setCharacterEncoding("UTF-8");

    return notificationService.subscribe(loginMember.getMemberId());
  }

  @ApiOperation("알림 리스트 보기")
  @GetMapping("/notifications")
  public ResponseEntity<List<NotificationDto>> notificationList(
      @AuthenticationPrincipal LoginMember loginMember) {

    List<NotificationDto> notifications =
        notificationService.getNotifications(loginMember.getMemberId());

    return ResponseEntity.ok(notifications);
  }

  @ApiOperation("안읽은 알림 존재 여부")
  @PreAuthorize("hasAnyRole('ROLE_USER')")
  @GetMapping("/notification/unread")
  public ResponseEntity<Boolean> unreadNotificationExists(
      @AuthenticationPrincipal LoginMember loginMember) {

    boolean result = notificationService.checkUnreadNotification(loginMember.getMemberId());
    return ResponseEntity.ok(result);
  }

}
