package com.blog.som.domain.post.mongo.respository;

import com.blog.som.domain.post.mongo.document.PostDocument;
import java.time.LocalDate;
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
  Page<PostDocument> findRecentAndHighViews(LocalDate oneMonthAgo, Pageable pageable);

  Page<PostDocument> findByTitleContainingOrIntroductionContaining(String title, String introduction, Pageable pageable);

  Page<PostDocument> findByContentContaining(String content, Pageable pageable);

  Page<PostDocument> findByTagsContaining(String tagName, Pageable pageable);

  int countByAccountName(String accountName);

  Page<PostDocument> findByAccountName(String accountName, Pageable pageable);

  Page<PostDocument> findByAccountNameAndTagsContaining(String accountName, String tagName, Pageable pageable);

  Page<PostDocument> findByAccountNameAndTitleContainingOrIntroductionContaining(
      String accountName, String title, String introduction, Pageable pageable);


}
