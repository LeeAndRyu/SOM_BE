package com.blog.som.domain.post.elasticsearch.repository;

import com.blog.som.domain.post.elasticsearch.document.PostEsDocument;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ElasticsearchPostRepository extends ElasticsearchRepository<PostEsDocument, Long> {

  Page<PostEsDocument> findAllByAccountName(String accountName, Pageable pageable);

  void deleteByPostId(Long postId);

  //정확히 일치하는 경우만 반환
  @Query("{\"bool\": {\"must\": [{\"match\": {\"account_name\": \"?0\"}}, {\"match\": {\"tags\": \"?1\"}}]}}")
  Page<PostEsDocument> findByAccountNameAndTagsContaining(String accountName, String tagName, Pageable pageable);

  Page<PostEsDocument> findByAccountNameAndTitleContainingOrIntroductionContaining(
      String accountName, String title, String introduction, Pageable pageable);

  Page<PostEsDocument> findAll(Pageable pageable);

  Page<PostEsDocument> findByTitleContainingOrIntroductionContaining(String title, String introduction, Pageable pageable);

  Page<PostEsDocument> findByContentContaining(String query, Pageable pageable);

  //정확히 일치하는 경우만 반환
  @Query("{\"bool\": {\"must\": [{\"match\": {\"tags\": \"?0\"}}]}}")
  Page<PostEsDocument> findByTagsContaining(String query, Pageable pageable);

}
