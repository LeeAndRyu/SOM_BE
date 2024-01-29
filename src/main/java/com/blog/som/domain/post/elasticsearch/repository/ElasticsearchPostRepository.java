package com.blog.som.domain.post.elasticsearch.repository;

import com.blog.som.domain.post.elasticsearch.document.PostDocument;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ElasticsearchPostRepository extends ElasticsearchRepository<PostDocument, Long> {

  Page<PostDocument> findAllByAccountName(String accountName, Pageable pageable);

  void deleteByPostId(Long postId);

  //정확히 일치하는 경우만 반환
  @Query("{\"bool\": {\"must\": [{\"match\": {\"account_name\": \"?0\"}}, {\"match\": {\"tags\": \"?1\"}}]}}")
  Page<PostDocument> findByAccountNameAndTagsContaining(String accountName, String tagName, Pageable pageable);

  Page<PostDocument> findByAccountNameAndTitleContainingOrIntroductionContaining(
      String accountName, String title, String introduction, Pageable pageable);

  Page<PostDocument> findAll(Pageable pageable);

  Page<PostDocument> findByTitleContainingOrIntroductionContaining(String title, String introduction, Pageable pageable);

  Page<PostDocument> findByContentContaining(String query, Pageable pageable);

  //정확히 일치하는 경우만 반환
  @Query("{\"bool\": {\"must\": [{\"match\": {\"tags\": \"?0\"}}]}}")
  Page<PostDocument> findByTagsContaining(String query, Pageable pageable);

}
