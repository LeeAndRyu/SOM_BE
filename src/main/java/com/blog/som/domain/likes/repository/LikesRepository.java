package com.blog.som.domain.likes.repository;

import com.blog.som.domain.likes.entity.LikesEntity;
import com.blog.som.domain.member.entity.MemberEntity;
import com.blog.som.domain.post.entity.PostEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LikesRepository extends JpaRepository<LikesEntity, Long> {

  boolean existsByMemberAndPost(MemberEntity member, PostEntity post);

  Optional<LikesEntity> findByMemberAndPost(MemberEntity member, PostEntity post);
}
