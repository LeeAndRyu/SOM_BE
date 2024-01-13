package com.blog.som.domain.member.service;

import com.blog.som.domain.member.dto.MemberRegister.Request;
import com.blog.som.domain.member.dto.MemberRegister.Response;
import com.blog.som.domain.member.entity.MemberEntity;
import com.blog.som.domain.member.repository.MemberRepository;
import com.blog.som.global.components.PasswordUtils;
import com.blog.som.global.exception.ErrorCode;
import com.blog.som.global.exception.custom.MemberException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class MemberServiceImpl implements MemberService {

  private final MemberRepository memberRepository;

  @Override
  public Response registerMember(Request request) {

    if(memberRepository.existsByEmail(request.getEmail())){
      throw new MemberException(ErrorCode.MEMBER_ALREADY_EXISTS);
    }

    MemberEntity savedMember = memberRepository.save(
        Request.toEntity(request, PasswordUtils.encPassword(request.getPassword())));

    return Response.fromEntity(savedMember);
  }
}
