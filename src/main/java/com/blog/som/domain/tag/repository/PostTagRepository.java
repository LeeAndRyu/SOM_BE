package com.blog.som.domain.tag.repository;

import com.blog.som.domain.member.entity.MemberEntity;
import com.blog.som.domain.post.entity.PostEntity;
import com.blog.som.domain.tag.entity.PostTagEntity;
import com.blog.som.domain.tag.entity.TagEntity;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostTagRepository extends JpaRepository<PostTagEntity, Long> {

  List<PostTagEntity> findAllByPost(PostEntity post);

  Page<PostTagEntity> findByMemberAndTag(MemberEntity member, TagEntity tag, Pageable pageable);
}
