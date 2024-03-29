package com.blog.som.domain.member.security.userdetails;


import com.blog.som.domain.member.entity.MemberEntity;
import com.blog.som.domain.member.type.Role;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LoginMember implements UserDetails {

  private Long memberId;
  private String email;
  private String password;
  private Role role;

  public LoginMember(MemberEntity member) {
    this.memberId = member.getMemberId();
    this.email = member.getEmail();
    this.password = member.getPassword();
    this.role = member.getRole();
  }

  public LoginMember(Role role){
    this.memberId = 0L;
    this.email = "expired token";
    this.password = "expired token";
    this.role = Role.UNAUTH;
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    List<GrantedAuthority> grantedAuthorities = new ArrayList<>();
    grantedAuthorities.add(new SimpleGrantedAuthority("ROLE_" + this.role));
    return grantedAuthorities;
  }

  @Override
  public String getPassword() {
    return this.password;
  }

  @Override
  public String getUsername() {
    return this.email;
  }

  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  public boolean isAccountNonLocked() {
    return true;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @Override
  public boolean isEnabled() {
    return true;
  }

  @Override
  public String toString() {
    return "LoginMember{" +
        "memberId=" + memberId +
        ", email='" + email + '\'' +
        ", password='" + password + '\'' +
        ", role=" + role +
        '}';
  }
}
