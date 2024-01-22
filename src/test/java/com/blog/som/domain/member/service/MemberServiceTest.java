package com.blog.som.domain.member.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.amazonaws.services.s3.model.Owner;
import com.blog.som.EntityCreator;
import com.blog.som.domain.member.dto.MemberDto;
import com.blog.som.domain.member.dto.MemberEditRequest;
import com.blog.som.domain.member.dto.MemberPasswordEdit;
import com.blog.som.domain.member.dto.MemberRegister.EmailDuplicateResponse;
import com.blog.som.domain.member.dto.MemberRegister.Request;
import com.blog.som.domain.member.entity.MemberEntity;
import com.blog.som.domain.member.repository.MemberRepository;
import com.blog.som.global.components.mail.MailSender;
import com.blog.som.global.components.mail.SendMailDto;
import com.blog.som.global.components.password.PasswordUtils;
import com.blog.som.global.constant.ResponseConstant;
import com.blog.som.global.exception.ErrorCode;
import com.blog.som.global.exception.custom.MemberException;
import com.blog.som.global.redis.email.EmailAuthRepository;
import com.blog.som.global.s3.S3ImageService;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

  @Mock
  private MemberRepository memberRepository;
  @Mock
  private EmailAuthRepository emailAuthRepository;
  @Mock
  private MailSender mailSender;
  @Mock
  private S3ImageService s3ImageService;

  @InjectMocks
  private MemberServiceImpl memberService;


  @Nested
  @DisplayName("회원 가입 시작 - 이메일 중복체크")
  class EmailDuplicateCheckAndStartRegister {

    @Test
    @DisplayName("성공")
    void emailDuplicateCheckAndStartRegister() {
      String email = "test@test.com";
      //given
      when(memberRepository.existsByEmail(email))
          .thenReturn(false);

      //when
      EmailDuplicateResponse response = memberService.emailDuplicateCheckAndStartRegister(email);

      //then
      verify(mailSender, times(1)).sendMailForRegister(any(SendMailDto.class));
      assertThat(response.isDuplicateYn()).isFalse();
      assertThat(response.getEmail()).isEqualTo(email);
    }

    @Test
    @DisplayName("실패 : 이메일 중복")
    void emailDuplicateCheckAndStartRegister_email_duplicate() {
      String email = "test@test.com";
      //given
      when(memberRepository.existsByEmail(email))
          .thenReturn(true);

      //when
      EmailDuplicateResponse response = memberService.emailDuplicateCheckAndStartRegister(email);

      //then
      verify(mailSender, never()).sendMailForRegister(any(SendMailDto.class));

      assertThat(response.isDuplicateYn()).isTrue();
      assertThat(response.getEmail()).isEqualTo(email);
    }
  }

  @Nested
  @DisplayName("회원가입")
  class RegisterMember {

    @Test
    @DisplayName("성공")
    void registerMember() {
      MemberEntity member = EntityCreator.createMember(1L);
      String code = "test.uuid";
      String email = member.getEmail();
      Request request = Request.builder()
          .password(member.getPassword())
          .nickname(member.getNickname())
          .accountName(member.getAccountName())
          .introduction(member.getIntroduction())
          .build();

      //given
      when(emailAuthRepository.getEmailByUuid(code))
          .thenReturn(email);
      when(memberRepository.existsByEmail(email))
          .thenReturn(false);
      when(memberRepository.save(Request.toEntity(email, request)))
          .thenReturn(member);
      //when
      MemberDto result = memberService.registerMember(request, code);

      //then
      assertThat(result.getEmail()).isEqualTo(email);
      assertThat(result.getBlogName()).isEqualTo(request.getAccountName() + ".som");
    }

    @Test
    @DisplayName("실패 : EMAIL_AUTH_ALREADY_COMPLETE")
    void registerMember_EMAIL_AUTH_ALREADY_COMPLETE() {
      MemberEntity member = EntityCreator.createMember(1L);
      String code = "test.uuid";
      String email = member.getEmail();
      Request request = Request.builder()
          .password(member.getPassword())
          .nickname(member.getNickname())
          .accountName(member.getAccountName())
          .introduction(member.getIntroduction())
          .build();

      //given
      when(emailAuthRepository.getEmailByUuid(code))
          .thenReturn(email);
      when(memberRepository.existsByEmail(email))
          .thenReturn(true);


      //when
      //then
      MemberException memberException =
          assertThrows(MemberException.class, () -> memberService.registerMember(request, code));
      assertThat(memberException.getErrorCode()).isEqualTo(ErrorCode.EMAIL_AUTH_ALREADY_COMPLETE);
    }

    @Test
    @DisplayName("실패 : ACCOUNT_NAME_ALREADY_EXISTS")
    void registerMember_ACCOUNT_NAME_ALREADY_EXISTS() {
      MemberEntity member = EntityCreator.createMember(1L);
      String code = "test.uuid";
      String email = member.getEmail();
      Request request = Request.builder()
          .password(member.getPassword())
          .nickname(member.getNickname())
          .accountName(member.getAccountName())
          .introduction(member.getIntroduction())
          .build();

      //given
      when(emailAuthRepository.getEmailByUuid(code))
          .thenReturn(email);
      when(memberRepository.existsByEmail(email))
          .thenReturn(false);
      when(memberRepository.existsByAccountName(request.getAccountName()))
          .thenReturn(true);


      //when
      //then
      MemberException memberException =
          assertThrows(MemberException.class, () -> memberService.registerMember(request, code));
      assertThat(memberException.getErrorCode()).isEqualTo(ErrorCode.ACCOUNT_NAME_ALREADY_EXISTS);
    }

  }


  @Nested
  @DisplayName("회원 정보 수정")
  class EditMemberInfo {

    @Test
    @DisplayName("성공")
    void editMemberInfo() {
      MemberEntity member = EntityCreator.createMember(1L);

      MemberEditRequest request = MemberEditRequest.builder()
          .nickname("edit-nickname")
          .introduction("edit-introduction")
          .blogName("edit-blogName")
          .build();

      MemberEntity updatedMember = EntityCreator.createMember(1L);
      updatedMember.setNickname(request.getNickname());
      updatedMember.setIntroduction(request.getIntroduction());
      updatedMember.setBlogName(request.getBlogName());

      //given
      when(memberRepository.findById(1L))
          .thenReturn(Optional.of(member));
      when(memberRepository.save(member))
          .thenReturn(updatedMember);

      //when
      MemberDto result = memberService.editMemberInfo(1L, request);

      //then
      assertThat(result.getNickname()).isEqualTo(request.getNickname());
      assertThat(result.getIntroduction()).isEqualTo(request.getIntroduction());
      assertThat(result.getBlogName()).isEqualTo(request.getBlogName());
    }

    @Test
    @DisplayName("실패 : MEMBER_NOT_FOUND")
    void editMemberInfo_MEMBER_NOT_FOUND() {
      MemberEditRequest request = MemberEditRequest.builder()
          .nickname("edit-nickname")
          .introduction("edit-introduction")
          .blogName("edit-blogName")
          .build();

      //given
      when(memberRepository.findById(1L))
          .thenReturn(Optional.empty());

      //when
      //then
      MemberException memberException =
          assertThrows(MemberException.class, () -> memberService.editMemberInfo(1L, request));
      assertThat(memberException.getErrorCode()).isEqualTo(ErrorCode.MEMBER_NOT_FOUND);

    }
  }

  @Nested
  @DisplayName("비밀번호 수정")
  class EditMemberPassword {

    @Test
    @DisplayName("성공")
    void editMemberPassword() {
      MemberEntity member = EntityCreator.createMember(1L);
      String plainPassword = "hello";
      String encPassword = PasswordUtils.encPassword(plainPassword);
      member.setPassword(encPassword);

      String newPassword = "hi123123";

      MemberPasswordEdit.Request request = MemberPasswordEdit.Request.builder()
          .currentPassword(plainPassword)
          .newPassword(newPassword)
          .newPasswordCheck(newPassword)
          .build();

      //given
      when(memberRepository.findById(1L))
          .thenReturn(Optional.of(member));

      //when
      MemberPasswordEdit.Response response = memberService.editMemberPassword(1L, request);

      //then
      assertThat(response.getMemberId()).isEqualTo(1L);
      assertThat(response.getMessage()).isEqualTo(ResponseConstant.PASSWORD_EDIT_COMPLETE);
    }

    @Test
    @DisplayName("실패 : MEMBER_NOT_FOUND")
    void editMemberPassword_MEMBER_NOT_FOUND() {
      MemberEntity member = EntityCreator.createMember(1L);
      String plainPassword = "hello";
      String encPassword = PasswordUtils.encPassword(plainPassword);
      member.setPassword(encPassword);

      String newPassword = "hi123123";

      MemberPasswordEdit.Request request = MemberPasswordEdit.Request.builder()
          .currentPassword(plainPassword)
          .newPassword(newPassword)
          .newPasswordCheck(newPassword)
          .build();

      //given
      when(memberRepository.findById(1L))
          .thenReturn(Optional.empty());

      //when
      //then
      MemberException memberException = assertThrows(MemberException.class,
          () -> memberService.editMemberPassword(1L, request));
      assertThat(memberException.getErrorCode()).isEqualTo(ErrorCode.MEMBER_NOT_FOUND);
    }

    @Test
    @DisplayName("실패 : MEMBER_PASSWORD_INCORRECT")
    void editMemberPassword_MEMBER_PASSWORD_INCORRECT() {
      MemberEntity member = EntityCreator.createMember(1L);
      String plainPassword = "hello";
      String encPassword = PasswordUtils.encPassword(plainPassword);
      member.setPassword(encPassword);

      String newPassword = "hi123123";

      MemberPasswordEdit.Request request = MemberPasswordEdit.Request.builder()
          .currentPassword(plainPassword + "!!!")
          .newPassword(newPassword)
          .newPasswordCheck(newPassword)
          .build();

      //given
      when(memberRepository.findById(1L))
          .thenReturn(Optional.of(member));

      //when
      //then
      MemberException memberException = assertThrows(MemberException.class,
          () -> memberService.editMemberPassword(1L, request));
      assertThat(memberException.getErrorCode()).isEqualTo(ErrorCode.MEMBER_PASSWORD_INCORRECT);
    }

    @Test
    @DisplayName("실패 : PASSWORD_CHECK_INCORRECT")
    void editMemberPassword_PASSWORD_CHECK_INCORRECT() {
      MemberEntity member = EntityCreator.createMember(1L);
      String plainPassword = "hello";
      String encPassword = PasswordUtils.encPassword(plainPassword);
      member.setPassword(encPassword);

      String newPassword = "hi123123";

      MemberPasswordEdit.Request request = MemberPasswordEdit.Request.builder()
          .currentPassword(plainPassword)
          .newPassword(newPassword)
          .newPasswordCheck(newPassword + "!!!")
          .build();

      //given
      when(memberRepository.findById(1L))
          .thenReturn(Optional.of(member));

      //when
      //then
      MemberException memberException = assertThrows(MemberException.class,
          () -> memberService.editMemberPassword(1L, request));
      assertThat(memberException.getErrorCode()).isEqualTo(ErrorCode.PASSWORD_CHECK_INCORRECT);
    }

  }

  @Nested
  @DisplayName("프로필 사진 업로드")
  class UpdateProfileImage {

    private MultipartFile createMockMultipartFile(int fileNumber) {
      String filename = "test-file" + fileNumber + ".jpg";
      String content = "test-data" + fileNumber;

      return new MockMultipartFile("file", filename, "image/jpg", content.getBytes());
    }

    private MultipartFile createEmptyMockMultipartFile(int fileNumber) throws IOException {
      String filename = "test-file" + fileNumber + ".jpg";
      String content = "test-data" + fileNumber;

      return new MockMultipartFile("file", filename, "image/jpg", new ByteArrayInputStream(new byte[0]));
    }

    @Test
    @DisplayName("성공 : 기존 profile image 존재")
    void updateProfileImage() {
      MultipartFile multipartFile = createMockMultipartFile(1);
      MemberEntity member = EntityCreator.createMember(1L);
      String beforeImage = member.getProfileImage();
      String saveResult = "aws-" + multipartFile.getOriginalFilename();

      //given
      when(memberRepository.findById(1L))
          .thenReturn(Optional.of(member));
      when(s3ImageService.upload(multipartFile))
          .thenReturn(saveResult);

      //when
      MemberDto result = memberService.updateProfileImage(1L, multipartFile);

      //then
      verify(s3ImageService, times(1)).deleteImageFromS3(beforeImage);
      assertThat(result.getProfileImage()).isEqualTo(saveResult);
    }

    @Test
    @DisplayName("성공 : 기존 profile image = null")
    void updateProfileImage_profile_image_is_null() {
      MultipartFile multipartFile = createMockMultipartFile(1);
      MemberEntity member = EntityCreator.createMember(1L);
      String beforeImage = member.getProfileImage();
      member.setProfileImage(null);
      String saveResult = "aws-" + multipartFile.getOriginalFilename();

      //given
      when(memberRepository.findById(1L))
          .thenReturn(Optional.of(member));
      when(s3ImageService.upload(multipartFile))
          .thenReturn(saveResult);

      //when
      MemberDto result = memberService.updateProfileImage(1L, multipartFile);

      //then
      verify(s3ImageService, never()).deleteImageFromS3(beforeImage);
      assertThat(result.getProfileImage()).isEqualTo(saveResult);
    }


    @Test
    @DisplayName("성공 : input = 빈 파일")
    void updateProfileImage_empty_file() throws IOException {
      MultipartFile multipartFile = createEmptyMockMultipartFile(1);
      MemberEntity member = EntityCreator.createMember(1L);

      //given
      when(memberRepository.findById(1L))
          .thenReturn(Optional.of(member));

      //when
      MemberDto result = memberService.updateProfileImage(1L, multipartFile);

      //then
      assertThat(result.getProfileImage()).isEqualTo(member.getProfileImage());
    }

    @Test
    @DisplayName("실패 : MEMBER_NOT_FOUND")
    void updateProfileImage_MEMBER_NOT_FOUND() {
      MultipartFile multipartFile = createMockMultipartFile(1);
      MemberEntity member = EntityCreator.createMember(1L);

      //given
      when(memberRepository.findById(1L))
          .thenReturn(Optional.empty());

      //when
      //then
      MemberException memberException =
          assertThrows(MemberException.class,
              () -> memberService.updateProfileImage(member.getMemberId(), multipartFile));
      assertThat(memberException.getErrorCode()).isEqualTo(ErrorCode.MEMBER_NOT_FOUND);
    }
  }

  @Nested
  @DisplayName("프로필 이미지 삭제")
  class DeleteProfileImage {

    @Test
    @DisplayName("성공 : 이미지 삭제")
    void deleteProfileImage() {
      MemberEntity member = EntityCreator.createMember(1L);
      //given
      when(memberRepository.findById(1L))
          .thenReturn(Optional.of(member));
      //when
      MemberDto memberDto = memberService.deleteProfileImage(1L);

      //then
      assertThat(memberDto.getProfileImage()).isNull();
    }

    @Test
    @DisplayName("성공 : profile-image = null")
    void deleteProfileImage_profile_image_is_null() {
      MemberEntity member = EntityCreator.createMember(1L);
      member.setProfileImage(null);
      //given
      when(memberRepository.findById(1L))
          .thenReturn(Optional.of(member));
      //when
      MemberDto memberDto = memberService.deleteProfileImage(1L);

      //then
      verify(s3ImageService, never()).deleteImageFromS3(member.getProfileImage());
      verify(memberRepository, never()).save(member);
    }

    @Test
    @DisplayName("실패 : MEMBER_NOT_FOUND")
    void deleteProfileImage_MEMBER_NOT_FOUND() {
      MemberEntity member = EntityCreator.createMember(1L);

      //given
      when(memberRepository.findById(1L))
          .thenReturn(Optional.empty());

      //when
      //then
      MemberException memberException =
          assertThrows(MemberException.class,
              () -> memberService.deleteProfileImage(member.getMemberId()));
      assertThat(memberException.getErrorCode()).isEqualTo(ErrorCode.MEMBER_NOT_FOUND);
    }

  }
}