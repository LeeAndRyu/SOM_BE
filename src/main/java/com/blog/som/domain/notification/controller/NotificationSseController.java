package com.blog.som.domain.notification.controller;

import com.blog.som.domain.member.security.userdetails.LoginMember;
import com.blog.som.domain.notification.service.NotificationService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Api(tags = "알림 SSE 관련")
@Slf4j
@RequiredArgsConstructor
@RestController
public class NotificationSseController {

  private final NotificationService notificationService;

  @ApiOperation(value = "구독을 시작하기 위한 메서드")
  @GetMapping(value = "/sse/subscribe", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
  public SseEmitter subscribe(@AuthenticationPrincipal LoginMember loginMember,
      HttpServletResponse response) {
    response.setCharacterEncoding("UTF-8");

    return notificationService.subscribe(loginMember.getMemberId());
  }

}
