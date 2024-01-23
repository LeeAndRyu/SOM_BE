package com.blog.som.domain.tag.repository;

import com.blog.som.domain.post.entity.PostEntity;
import com.blog.som.domain.tag.entity.PostTagEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostTagRepository extends JpaRepository<PostTagEntity, Long> {

  List<PostTagEntity> findAllByPost(PostEntity post);
}
