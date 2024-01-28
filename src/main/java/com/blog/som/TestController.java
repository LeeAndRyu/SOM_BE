package com.blog.som;

import com.blog.som.domain.member.security.userdetails.LoginMember;
import com.blog.som.domain.post.elasticsearch.document.PostDocument;
import com.blog.som.domain.post.elasticsearch.repository.ElasticSearchPostRepository;
import com.blog.som.global.s3.S3ImageService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Api(tags = "test용 컨트롤러 - security 오픈되어있음")
@RequiredArgsConstructor
@RestController
@RequestMapping("/test")
public class TestController {
  private final S3ImageService s3ImageService;
  private final ElasticSearchPostRepository elasticSearchPostRepository;
  
  @ApiOperation(value = "로그인 멤버 확인", notes = "토큰만 Header에 포함시켜서 로그인 멤버 확인")
  @GetMapping("/loginUser")
  public ResponseEntity<?> loginUser(@AuthenticationPrincipal LoginMember loginMember){
      
      return ResponseEntity.ok(loginMember);
  }

  @PostMapping("/s3/upload")
  public ResponseEntity<?> s3Upload(@RequestPart(value = "image", required = false) MultipartFile image){
    log.info("image : {}", image);
    String profileImage = s3ImageService.upload(image);
    return ResponseEntity.ok(profileImage);
  }

  @GetMapping("/s3/delete")
  public ResponseEntity<?> s3delete(@RequestParam String addr){
      log.info("image address : {}", addr);
      s3ImageService.deleteImageFromS3(addr);
      return ResponseEntity.ok(null);
  }

  @DeleteMapping("/delete")
  public ResponseEntity<?> deleteES(@RequestParam Long id){
    elasticSearchPostRepository.deleteByPostId(id);
    return ResponseEntity.ok("delete");
  }

  @GetMapping("/find/all")
  public ResponseEntity<?> findAll(){
    Iterable<PostDocument> all = elasticSearchPostRepository.findAll();
    return ResponseEntity.ok(all);
  }


  
  
}
