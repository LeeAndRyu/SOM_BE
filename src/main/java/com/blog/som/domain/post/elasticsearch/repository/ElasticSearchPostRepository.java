package com.blog.som.domain.post.elasticsearch.repository;

import com.blog.som.domain.post.elasticsearch.document.PostDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ElasticSearchPostRepository extends ElasticsearchRepository<PostDocument, Long> {

}
