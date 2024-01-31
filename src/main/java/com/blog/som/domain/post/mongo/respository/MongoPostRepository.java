package com.blog.som.domain.post.mongo.respository;

import com.blog.som.domain.post.mongo.document.PostDocument;
import java.time.LocalDateTime;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface MongoPostRepository extends MongoRepository<PostDocument, String> {

  Optional<PostDocument> findByPostId(Long postId);

  void deleteByPostId(Long postId);

  @Query("{ 'registeredAt' :  {$gt :  ?0}, 'views' :  {$exists: true}}")
  Page<PostDocument> findRecentAndHighViews(LocalDateTime oneMonthAgo, Pageable pageable);

  Page<PostDocument> findByTitleContainingOrIntroductionContaining(String title, String introduction, Pageable pageable);

  Page<PostDocument> findByContentContaining(String content, Pageable pageable);

  Page<PostDocument> findByTagsContaining(String tagName, Pageable pageable);

}
