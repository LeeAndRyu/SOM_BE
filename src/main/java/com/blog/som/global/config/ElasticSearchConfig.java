package com.blog.som.global.config;

import com.blog.som.domain.post.elasticsearch.repository.ElasticSearchPostQueryRepository;
import com.blog.som.domain.post.elasticsearch.repository.ElasticSearchPostRepository;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.RestClients;
import org.springframework.data.elasticsearch.config.AbstractElasticsearchConfiguration;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

@EnableElasticsearchRepositories(basePackageClasses = {ElasticSearchPostRepository.class, ElasticSearchPostQueryRepository.class})
@Configuration
public class ElasticSearchConfig extends AbstractElasticsearchConfiguration {

  @Value("${spring.elasticsearch.url}")
  private String elasticsearchUrl;

  @Override
  public RestHighLevelClient elasticsearchClient() {
    ClientConfiguration clientConfiguration =
        ClientConfiguration.builder()
            .connectedTo(elasticsearchUrl)
            .withConnectTimeout(10000)
            .withSocketTimeout(10000)
            .build();
    return RestClients.create(clientConfiguration).rest();
  }
}
