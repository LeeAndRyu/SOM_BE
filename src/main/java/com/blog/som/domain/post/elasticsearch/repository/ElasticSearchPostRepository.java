package com.blog.som.domain.post.elasticsearch.repository;

import com.blog.som.domain.post.elasticsearch.document.PostDocument;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ElasticSearchPostRepository extends ElasticsearchRepository<PostDocument, Long> {

  Page<PostDocument> findAllByAccountName(String accountName, Pageable pageable);

  void deleteByPostId(Long postId);

  @Query("{\"bool\": {\"must\": [{\"match\": {\"account_name\": \"?0\"}}, {\"match\": {\"tags\": \"?1\"}}]}}")
  Page<PostDocument> findByAccountNameAndTagsContaining(String accountName, String tagName, Pageable pageable);

  Optional<PostDocument> findByPostId(Long postId);

  //단어가 일치하는 것을 찾는 쿼리
  @Query("{\"bool\": {\"must\": ["
      + "{\"match\": {\"account_name\": \"?0\"}}, "
      + "{\"bool\": {\"should\": ["
      + "{\"match_phrase\": {\"title\": \"?1\"}}, "
      + "{\"match_phrase\": {\"introduction\": \"?1\"}}"
      + "]}}"
      + "]}}")
  Page<PostDocument> findByAccountNameAndTitleOrIntroductionContaining(String accountName, String query, Pageable pageable);
}
