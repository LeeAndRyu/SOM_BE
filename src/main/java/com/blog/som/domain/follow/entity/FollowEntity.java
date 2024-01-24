package com.blog.som.domain.follow.entity;

import com.blog.som.domain.member.entity.MemberEntity;
import java.time.LocalDateTime;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
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
@Entity(name = "follow")
@EntityListeners(AuditingEntityListener.class)
public class FollowEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "follow_id", nullable = false)
  private Long followId;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "from_member_id", nullable = false)
  private MemberEntity fromMember;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "to_member_id", nullable = false)
  private MemberEntity toMember;

  @CreatedDate
  private LocalDateTime followAt;

  public FollowEntity(MemberEntity fromMember, MemberEntity toMember) {
    this.fromMember = fromMember;
    this.toMember = toMember;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    FollowEntity that = (FollowEntity) o;
    return Objects.equals(followId, that.followId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(followId);
  }
}
