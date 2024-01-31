package com.blog.som.domain.comment.repository;

import com.blog.som.domain.comment.entity.CommentEntity;
import com.blog.som.domain.post.entity.PostEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepository extends JpaRepository<CommentEntity, Long> {

  List<CommentEntity> findByPostOrderByRegisteredAtDesc(PostEntity post);
}
