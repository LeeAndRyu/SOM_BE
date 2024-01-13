package com.blog.som.domain.member.service;

import com.blog.som.domain.member.dto.MemberRegister;

public interface MemberService {

  MemberRegister.Response registerMember(MemberRegister.Request request);

}
