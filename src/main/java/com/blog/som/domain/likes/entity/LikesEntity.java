package com.blog.som.domain.likes.entity;


import com.blog.som.domain.member.entity.MemberEntity;
import com.blog.som.domain.post.entity.PostEntity;
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
@Entity(name = "likes")
@EntityListeners(AuditingEntityListener.class)
public class LikesEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "likes_id", nullable = false)
  private Long likesId;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "member_id", nullable = false)
  private MemberEntity member;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "post_id", nullable = false)
  private PostEntity post;

  @CreatedDate
  private LocalDateTime registeredAt;

  public LikesEntity(MemberEntity member, PostEntity post) {
    this.member = member;
    this.post = post;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    LikesEntity that = (LikesEntity) o;
    return Objects.equals(likesId, that.likesId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(likesId);
  }
}
