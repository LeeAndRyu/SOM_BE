package com.blog.som.domain.member.service;

import com.blog.som.domain.member.dto.EmailAuthResult;
import com.blog.som.domain.member.dto.MemberDto;
import com.blog.som.domain.member.dto.MemberEditRequest;
import com.blog.som.domain.member.dto.MemberPasswordEdit;
import com.blog.som.domain.member.dto.MemberRegister;

public interface MemberService {

  MemberRegister.Response registerMember(MemberRegister.Request request);

  EmailAuthResult emailAuth(String key);

  MemberDto editMemberInfo(Long memberId, MemberEditRequest request);

  MemberPasswordEdit.Response editMemberPassword(Long memberId, MemberPasswordEdit.Request request);
}
