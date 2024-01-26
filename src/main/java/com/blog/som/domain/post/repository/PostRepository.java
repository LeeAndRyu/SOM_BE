package com.blog.som.domain.post.repository;

import com.blog.som.domain.member.entity.MemberEntity;
import com.blog.som.domain.post.entity.PostEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepository extends JpaRepository<PostEntity, Long> {

  Page<PostEntity> findByMember(MemberEntity member, Pageable pageable);

  Page<PostEntity> findByMemberAndTitleContainingOrIntroductionContaining(
      MemberEntity member, String title, String introduction, Pageable pageable);
}
