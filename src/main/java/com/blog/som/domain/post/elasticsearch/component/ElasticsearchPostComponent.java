package com.blog.som.domain.post.elasticsearch.component;

import com.blog.som.domain.post.elasticsearch.document.PostEsDocument;
import com.blog.som.domain.post.elasticsearch.repository.ElasticsearchPostRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class ElasticsearchPostComponent {

  private final ElasticsearchPostRepository elasticsearchPostRepository;

  @Async
  public void updatePostDocumentProfileImage(String accountName, String profileImage) {
    int page = 0;
    int pageSize = 100;
    int amount = 0;

    Page<PostEsDocument> documentPage =
        elasticsearchPostRepository
            .findAllByAccountName(accountName, PageRequest.of(page, pageSize));

    while (documentPage.getNumberOfElements() != 0) {
      List<PostEsDocument> list = documentPage.getContent();
      amount += list.size();
      for (PostEsDocument postEsDocument : list) {
        postEsDocument.setProfileImage(profileImage);
      }
      elasticsearchPostRepository.saveAll(list);
      log.info("page={}, accumulate={}", page, amount);

      page++;
      documentPage = elasticsearchPostRepository.findAllByAccountName(accountName, PageRequest.of(page, pageSize));
    }
    log.info("[Elasticsearch PostDocument update profile_image] total amount={}", amount);
  }
  

}
