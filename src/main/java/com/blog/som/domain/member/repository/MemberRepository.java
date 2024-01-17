package com.blog.som.domain.member.repository;

import com.blog.som.domain.member.entity.MemberEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberRepository extends JpaRepository<MemberEntity, Long> {

  boolean existsByEmail(String email);

  Optional<MemberEntity> findByEmail(String email);
}
