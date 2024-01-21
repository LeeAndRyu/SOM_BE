package com.blog.som;

import com.blog.som.domain.member.security.userdetails.LoginMember;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Api(tags = "test용 컨트롤러 - security 오픈되어있음")
@RequiredArgsConstructor
@RequestMapping("/test")
public class TestController {
  
  @ApiOperation(value = "로그인 멤버 확인", notes = "토큰만 Header에 포함시켜서 로그인 멤버 확인")
  @GetMapping("/loginUser")
  public ResponseEntity<?> loginUser(@AuthenticationPrincipal LoginMember loginMember){
      
      return ResponseEntity.ok(loginMember);
  }
  
  
}
