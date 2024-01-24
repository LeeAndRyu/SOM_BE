package com.blog.som.domain.member.entity;

import com.blog.som.domain.member.dto.MemberEditRequest;
import com.blog.som.domain.member.type.Role;
import java.time.LocalDateTime;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity(name = "member")
@EntityListeners(AuditingEntityListener.class)
public class MemberEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "member_id", nullable = false)
  private Long memberId;

  @Column(name = "email", unique = true, nullable = false)
  private String email;

  @Column(name = "password", nullable = false)
  private String password;

  @Column(name = "nickname", nullable = false)
  private String nickname;

  @Column(name = "account_name", nullable = false)
  private String accountName;

  @Column(name = "blog_name", nullable = false)
  private String blogName;

  @Column(name = "introduction")
  private String introduction;

  @Column(name = "profile_image")
  private String profileImage;

  @Column(name = "following")
  private int followingCount;

  @Column(name = "follower")
  private int followerCount;

  @Column(name = "registered_at", nullable = false)
  @CreatedDate
  private LocalDateTime registeredAt;

  @Enumerated(EnumType.STRING)
  @Column(name = "role")
  private Role role;

  public void editMember(MemberEditRequest request) {
    this.nickname = request.getNickname();
    this.introduction = request.getIntroduction();
    this.blogName = request.getBlogName();
  }

  public void addFollowerCount(){
    this.followerCount += 1;
  }
  public void addFollowingCount(){
    this.followingCount += 1;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    MemberEntity member = (MemberEntity) o;
    return Objects.equals(memberId, member.memberId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(memberId);
  }

  @Override
  public String toString() {
    return "MemberEntity{" +
        "memberId=" + memberId +
        ", email='" + email + '\'' +
        ", nickname='" + nickname + '\'' +
        '}';
  }

}
