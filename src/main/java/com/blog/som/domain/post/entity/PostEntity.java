package com.blog.som.domain.post.entity;

import com.blog.som.domain.member.entity.MemberEntity;
import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
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
@Entity(name = "post")
@EntityListeners(AuditingEntityListener.class)
public class PostEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "post_id")
  private Long postId;

  @ManyToOne
  @JoinColumn(name = "member")
  private MemberEntity member;

  @Column(name = "title")
  private String title;

  @Column(name = "content")
  private String content;

  @Column(name = "thumbnail")
  private String thumbnail;

  @Column(name = "introduction")
  private String introduction;

  @Column(name = "like")
  private Long like;

  @Column(name = "view")
  private Long view;

  @CreatedDate
  @Column(name = "registered_at", nullable = false)
  private LocalDateTime registeredAt;

  @Column(name = "last_modified_at")
  private LocalDateTime lastModifiedAt;
}
