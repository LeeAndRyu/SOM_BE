package com.blog.som.domain.tag.repository;

import com.blog.som.domain.member.entity.MemberEntity;
import com.blog.som.domain.post.entity.PostEntity;
import com.blog.som.domain.tag.entity.PostTagEntity;
import com.blog.som.domain.tag.entity.TagEntity;
import java.util.List;

import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface PostTagRepository extends JpaRepository<PostTagEntity, Long> {

  List<PostTagEntity> findAllByPost(PostEntity post);

  @Query("SELECT t.tagName from post_tag pt " +
          "JOIN tag t on pt.tag.tagId = t.tagId " +
          "where pt.post = :post")
  List<String> findPostTagNamesByPost(@Param("post") PostEntity post);

  Page<PostTagEntity> findByMemberAndTag(MemberEntity member, TagEntity tag, Pageable pageable);
}
