package com.blog.som.domain.notification.repository;

import com.blog.som.domain.member.entity.MemberEntity;
import com.blog.som.domain.notification.entity.NotificationEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationRepository extends JpaRepository<NotificationEntity, Long> {

  List<NotificationEntity> findTop100ByMemberOrderByCreatedAtDesc(MemberEntity member);

  List<NotificationEntity> findByMemberAndReadAtIsNull(MemberEntity member);

  boolean existsByMemberAndReadAtIsNull(MemberEntity member);
}
