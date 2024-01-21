package com.blog.som.global.components.mail;

import com.blog.som.global.redis.email.EmailAuthRepository;
import javax.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class MailSender {

  private final JavaMailSender javaMailSender;
  private final EmailAuthRepository emailAuthRepository;

  @Async
  public void sendMailForRegister(SendMailDto sendMailDto) {
    String mail = sendMailDto.getEmail();
    String subject = "[" + sendMailDto.getNickname() + "]님 SOM : StoryOfMe 가입을 환영합니다. ";
    String text = new StringBuilder()
        .append("<h3>SOM 가입을 환영합니다. </h3>")
        .append("<p>아래 링크를 클릭하셔서 가입을 완료 하세요. </p>")
        .append("<div><a target='_blank' href='http://localhost:8080/auth/email-auth?key=" + sendMailDto.getAuthKey()
            + "'>이메일 인증하기</a></div>")
        .append("<p>감사합니다!</p>")
        .toString();

    //Redis 에 저장 (timeout = 10분)
    emailAuthRepository.saveEmailAuthUuid(sendMailDto.getAuthKey(), sendMailDto.getEmail());

    this.sendMail(mail, subject, text);
    log.info("메일 전송 완료 - {}", mail);
  }

  private void sendMail(String mail, String subject, String text) {
    MimeMessagePreparator msg = new MimeMessagePreparator() {
      @Override
      public void prepare(MimeMessage mimeMessage) throws Exception {
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
        mimeMessageHelper.setTo(mail);
        mimeMessageHelper.setSubject(subject);
        mimeMessageHelper.setText(text, true);
      }
    };

    try {
      javaMailSender.send(msg);
    } catch (Exception e) {
      log.error(e.getMessage());
    }
  }


}