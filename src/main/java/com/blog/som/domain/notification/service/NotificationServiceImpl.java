package com.blog.som.domain.notification.service;

import com.blog.som.domain.member.entity.MemberEntity;
import com.blog.som.domain.member.repository.MemberRepository;
import com.blog.som.domain.notification.dto.NotificationCreateDto;
import com.blog.som.domain.notification.dto.NotificationDto;
import com.blog.som.domain.notification.entity.NotificationEntity;
import com.blog.som.domain.notification.repository.EmitterRepository;
import com.blog.som.domain.notification.repository.NotificationRepository;
import com.blog.som.global.exception.ErrorCode;
import com.blog.som.global.exception.custom.MemberException;
import java.io.IOException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter.SseEventBuilder;

@Slf4j
@RequiredArgsConstructor
@Service
public class NotificationServiceImpl implements NotificationService {

  private static final Long EMITTER_DEFAULT_TIMEOUT = 1000 * 60 * 60L;

  private final MemberRepository memberRepository;
  private final EmitterRepository emitterRepository;
  private final NotificationRepository notificationRepository;

  @Override
  public List<NotificationDto> getNotifications(Long memberId) {
    MemberEntity member = memberRepository.findById(memberId)
        .orElseThrow(() -> new MemberException(ErrorCode.MEMBER_NOT_FOUND));

    List<NotificationEntity> list =
        notificationRepository.findTop100ByMemberOrderByCreatedAtDesc(member);

    List<NotificationEntity> unreadList = notificationRepository.findByMemberAndReadAtIsNull(member);
    unreadList.stream()
        .filter(n -> n.getReadAt() == null)
        .forEach(NotificationEntity::readNow);

    notificationRepository.saveAll(unreadList);

    return list.stream().map(NotificationDto::fromEntity).toList();
  }

  @Override
  public boolean checkUnreadNotification(Long memberId) {
    MemberEntity member = memberRepository.findById(memberId)
        .orElseThrow(() -> new MemberException(ErrorCode.MEMBER_NOT_FOUND));

    return notificationRepository.existsByMemberAndReadAtIsNull(member);
  }

  @Override
  public NotificationDto notify(MemberEntity member, MemberEntity writer, NotificationCreateDto notificationCreateDto) {
    NotificationEntity saved =
        notificationRepository.save(NotificationCreateDto.toEntity(member, writer, notificationCreateDto));

    this.sendToClient(member.getMemberId(), notificationCreateDto.getTitle());

    return NotificationDto.fromEntity(saved);
  }

  @Override
  public SseEmitter subscribe(Long memberId) {
    //이미터 생성
    SseEmitter emitter = new SseEmitter(EMITTER_DEFAULT_TIMEOUT);
    emitterRepository.save(memberId, emitter);

    //모든 데이터가 성공적으로 전송 된 상태 -> emitter 삭제
    emitter.onCompletion(() -> emitterRepository.deleteById(memberId));
    //타임아웃 되었을 때(지정 된 시간동안 아무 데이터도 전송되지 않았을 때) -> emitter 삭제
    emitter.onTimeout(() -> emitterRepository.deleteById(memberId));

    this.sendToClient(memberId, "EventStream Created");

    return emitter;
  }

  private void sendToClient(Long id, Object data) {
    SseEmitter emitter = emitterRepository.getEmitter(id);

    if (emitter != null) {
      try {
        SseEventBuilder sse = SseEmitter.event()
            .id(String.valueOf(id))
            .name("data")
            .data(data);
        emitter.send(sse);

      } catch (IOException e) {
        emitterRepository.deleteById(id);
        emitter.completeWithError(e);
      }
    }
  }


}
