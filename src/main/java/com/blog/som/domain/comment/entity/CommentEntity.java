package com.blog.som.domain.comment.entity;

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
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity(name = "comment")
@EntityListeners(AuditingEntityListener.class)
public class CommentEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "comment_id", nullable = false)
  private Long commentId;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "member_id", nullable = false)
  private MemberEntity member;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "post_id", nullable = false)
  private PostEntity post;

  @Column(name = "content", nullable = false)
  private String content;

  @CreatedDate
  private LocalDateTime registeredAt;

  @LastModifiedDate
  private LocalDateTime lastModifiedAt;

  public CommentEntity(MemberEntity member, PostEntity post, String content) {
    this.member = member;
    this.post = post;
    this.content = content;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    CommentEntity that = (CommentEntity) o;
    return Objects.equals(commentId, that.commentId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(commentId);
  }
}
