package com.blog.som.domain.notification.service;

import com.blog.som.domain.member.entity.MemberEntity;
import com.blog.som.domain.notification.dto.NotificationCreateDto;
import com.blog.som.domain.notification.dto.NotificationDto;
import java.util.List;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

public interface NotificationService {

  /**
   * 알림 리스트 조회
   */
  List<NotificationDto> getNotifications(Long memberId);

  /**
   * 안읽은 알림 여부 확인
   */
  boolean checkUnreadNotification(Long memberId);

  /**
   * 서버의 이벤트를 클라이언트에게 보내는 메서드 (실제 알림 발생 시 사용)
   */
  NotificationDto notify(MemberEntity member, MemberEntity writer, NotificationCreateDto notificationCreateDto);

  /**
   * 클라이언트가 구독을 위해 호출하는 메서드
   */
  SseEmitter subscribe(Long memberId);

}
