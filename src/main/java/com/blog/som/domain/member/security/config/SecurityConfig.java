package com.blog.som.domain.member.security.config;


import com.blog.som.domain.member.security.errorhandling.MyAccessDeniedHandler;
import com.blog.som.domain.member.security.errorhandling.MyAuthenticationEntryPoint;
import com.blog.som.domain.member.security.filter.JwtAuthenticationFilter;
import com.blog.som.domain.member.security.filter.JwtExceptionFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
@Configuration
public class SecurityConfig {

  private final JwtAuthenticationFilter jwtAuthenticationFilter;
  private final JwtExceptionFilter jwtExceptionFilter;
  private final MyAccessDeniedHandler myAccessDeniedHandler;
  private final MyAuthenticationEntryPoint myAuthenticationEntryPoint;

  //모두에게 접근 허용
  private static final String[] PERMIT_ALL_URL = {
      //swagger
      "/v2/api-docs",
      "/swagger-resources/**",
      "/configuration/ui",
      "/configuration/security",
      "/swagger-ui.html",
      "/swagger-ui/**",
      "/webjars/**",
      "/member/register",
      "/auth/email-auth"
  };

  //멤버에게만 접근 허용
  private static final String[] PERMIT_ONLY_MEMBER = {
      "/member/**"
  };

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

    http
        .httpBasic().disable()
        .csrf().disable()
        .cors().and()
        .headers().frameOptions().disable();

    http.formLogin().disable();

    http
        .authorizeRequests()
        .antMatchers(PERMIT_ALL_URL)
        .permitAll()
        .antMatchers("/admin/**")
        .hasAuthority("ROLE_ADMIN")
        .antMatchers(PERMIT_ONLY_MEMBER)
        .hasAuthority("ROLE_USER");

    http
        .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
        .addFilterBefore(jwtExceptionFilter, JwtAuthenticationFilter.class)
        .exceptionHandling()
        .authenticationEntryPoint(myAuthenticationEntryPoint)
        .accessDeniedHandler(myAccessDeniedHandler);

    return http.build();
  }

  @Bean
  public WebSecurityCustomizer webSecurityCustomizer() {
    return web -> web.ignoring().antMatchers("/login", "/exception/**");
  }

  @Bean
  public AuthenticationManager authenticationManager(
      AuthenticationConfiguration authenticationConfiguration) throws Exception {
    return authenticationConfiguration.getAuthenticationManager();
  }
}
