package com.blog.som.domain.tag.repository;

import com.blog.som.domain.member.entity.MemberEntity;
import com.blog.som.domain.tag.entity.TagEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TagRepository extends JpaRepository<TagEntity, Long> {

  Optional<TagEntity> findByTagNameAndMember(String tagName, MemberEntity member);

  List<TagEntity> findAllByMember(MemberEntity member);

}
