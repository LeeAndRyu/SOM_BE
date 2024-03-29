package com.blog.som.global.api;

import com.blog.som.global.s3.S3ImageService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Api(tags = "[API] 이미지 업로드, 반환 관련")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class ImageController {

  private final S3ImageService s3ImageService;

  @ApiOperation(value = "이미지 한개 저장", notes = "이미지를 S3 객체로 저장 후 url 반환")
  @PostMapping("/s3/image")
  public ResponseEntity<String> convertImageToS3Object(
      @RequestPart MultipartFile image) {
    return ResponseEntity.ok(s3ImageService.upload(image));
  }

  @ApiOperation(value = "이미지 한개 삭제", notes = "전달 받은 이미지 객체 삭제")
  @DeleteMapping("/s3/image")
  public ResponseEntity<Boolean> deleteImageFromS3ByAdress(
      @RequestBody String imageAddress) {
    s3ImageService.deleteImageFromS3(imageAddress);
    return ResponseEntity.ok(true);
  }

}
