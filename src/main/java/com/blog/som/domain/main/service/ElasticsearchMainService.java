package com.blog.som.domain.main.service;

import com.blog.som.domain.blog.dto.BlogPostDto;
import com.blog.som.domain.blog.dto.BlogPostList;
import com.blog.som.domain.post.elasticsearch.document.PostDocument;
import com.blog.som.domain.post.elasticsearch.repository.ElasticsearchPostRepository;
import com.blog.som.global.constant.NumberConstant;
import com.blog.som.global.constant.SearchConstant;
import com.blog.som.global.dto.PageDto;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class ElasticsearchMainService implements MainService{

  private final ElasticsearchPostRepository elasticsearchPostRepository;


  @Override
  public BlogPostList getMainPageList(String sort, int page) {

    String sortBy = SearchConstant.VIEWS;

    if(sort.equals("latest")){
      sortBy = SearchConstant.REGISTERED_AT;
    }

    PageRequest pageRequest =
        PageRequest.of(page - 1, NumberConstant.DEFAULT_PAGE_SIZE, Sort.by(sortBy).descending());

    Page<PostDocument> findPage = elasticsearchPostRepository.findAll(pageRequest);

    List<BlogPostDto> blogPostDtoList = findPage.getContent().stream().map(BlogPostDto::fromDocument).toList();

    return new BlogPostList(PageDto.fromPostDocumentPage(findPage), blogPostDtoList);
  }
}
