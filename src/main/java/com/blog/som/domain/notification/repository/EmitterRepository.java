package com.blog.som.domain.notification.repository;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Repository;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Repository
public class EmitterRepository {

  private final Map<Long, SseEmitter> emitterMap = new ConcurrentHashMap<>();

  public void save(Long id, SseEmitter sseEmitter){
    emitterMap.put(id, sseEmitter);
  }

  public void deleteById(Long id){
    emitterMap.remove(id);
  }

  public SseEmitter getEmitter(Long id){
    return emitterMap.get(id);
  }

}
