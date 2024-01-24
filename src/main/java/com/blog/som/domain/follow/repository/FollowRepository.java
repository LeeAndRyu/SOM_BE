package com.blog.som.domain.follow.repository;

import com.blog.som.domain.follow.entity.FollowEntity;
import com.blog.som.domain.member.entity.MemberEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FollowRepository extends JpaRepository<FollowEntity, Long> {
  boolean existsByFromMemberAndToMember(MemberEntity fromMember, MemberEntity toMember);
  Optional<FollowEntity> findByFromMemberAndToMember(MemberEntity fromMember, MemberEntity toMember);
}
