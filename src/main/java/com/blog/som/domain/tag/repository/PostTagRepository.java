package com.blog.som.domain.tag.repository;

import com.blog.som.domain.tag.entity.PostTagEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostTagRepository extends JpaRepository<PostTagEntity, Long> {

}
