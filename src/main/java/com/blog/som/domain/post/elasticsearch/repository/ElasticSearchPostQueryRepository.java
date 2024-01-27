package com.blog.som.domain.post.elasticsearch.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;

@RequiredArgsConstructor
public class ElasticSearchPostQueryRepository {

  private final ElasticsearchOperations elasticsearchOperations;

}
