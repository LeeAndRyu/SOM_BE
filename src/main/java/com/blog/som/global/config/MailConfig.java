package com.blog.som.global.config;

import java.util.Properties;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;


@Configuration
public class MailConfig {

  @Value("${spring.mail.host}")
  private String host;

  @Value("${spring.mail.port}")
  private int port;

  @Value("${spring.mail.username}")
  private String username;

  @Value("${spring.mail.password}")
  private String password;

  @Value("${spring.mail.properties.mail.smtp.starttls.enable}")
  private boolean starttlsEnable;

  @Value("${spring.mail.properties.mail.smtp.starttls.required}")
  private boolean starttlsRequired;

  @Value("${spring.mail.properties.mail.smtp.timeout}")
  private int timeout;

  @Value("${spring.mail.properties.mail.smtp.connectiontimeout}")
  private int connectionTimeout;

  @Value("${spring.mail.properties.mail.smtp.writetimeout}")
  private int writeTimeout;

  @Bean
  public JavaMailSender javaMailSender(){
    JavaMailSenderImpl javaMailSender = new JavaMailSenderImpl();
    javaMailSender.setHost(host);
    javaMailSender.setPort(port);
    javaMailSender.setUsername(username);
    javaMailSender.setPassword(password);
    javaMailSender.setDefaultEncoding("UTF-8");

    Properties pt = new Properties();
    pt.put("mail.smtp.starttls.enable", starttlsEnable);
    pt.put("mail.smtp.starttls.required", starttlsRequired);
    pt.put("mail.smtp.connectiontimeout", connectionTimeout);
    pt.put("mail.smtp.timeout", timeout);
    pt.put("mail.smtp.writeTimeout", writeTimeout);

    javaMailSender.setJavaMailProperties(pt);

    return javaMailSender;
  }



}
