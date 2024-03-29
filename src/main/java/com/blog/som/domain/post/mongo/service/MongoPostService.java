package com.blog.som.domain.post.mongo.service;

import com.blog.som.domain.likes.type.LikesStatus;
import com.blog.som.domain.post.mongo.document.PostDocument;
import com.blog.som.domain.post.mongo.respository.MongoPostRepository;
import com.blog.som.global.exception.ErrorCode;
import com.blog.som.global.exception.custom.PostException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class MongoPostService {

  private final MongoPostRepository mongoPostRepository;

  @Async
  public void updatePostDocumentProfileImage(String accountName, String profileImage) {
    int page = 0;
    int pageSize = 100;
    int amount = 0;

    Page<PostDocument> documentPage = mongoPostRepository.findByAccountName(accountName,
        PageRequest.of(page, pageSize));

    while (documentPage.getNumberOfElements() != 0) {
      List<PostDocument> list = documentPage.getContent();
      amount += list.size();
      for (PostDocument postDocument : list) {
        postDocument.setProfileImage(profileImage);
      }
      mongoPostRepository.saveAll(list);
      log.info("page={}, accumulate={}", page, amount);

      page++;
      documentPage = mongoPostRepository.findByAccountName(accountName, PageRequest.of(page, pageSize));
    }
    log.info("[Elasticsearch PostDocument update profile_image] total amount={}", amount);
  }

  public void updatePostDocumentLikes(LikesStatus likesStatus, Long postId){
    PostDocument postDocument = mongoPostRepository.findByPostId(postId)
        .orElseThrow(() -> new PostException(ErrorCode.POST_NOT_FOUND));

    if(LikesStatus.LIKES.equals(likesStatus)){
      postDocument.addLikes();
    }else{
      postDocument.minusLikes();
    }
    mongoPostRepository.save(postDocument);
  }

  public void updatePostDocumentComments(boolean result, Long postId){
    PostDocument postDocument = mongoPostRepository.findByPostId(postId)
        .orElseThrow(() -> new PostException(ErrorCode.POST_NOT_FOUND));

    if(result){
      postDocument.addComments();
    }else{
      postDocument.minusComments();
    }
    mongoPostRepository.save(postDocument);
  }

}
