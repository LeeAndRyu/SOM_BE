package com.blog.som.domain.post.entity;

import com.blog.som.domain.member.entity.MemberEntity;
import com.blog.som.domain.post.dto.PostEditRequest;
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
@Entity(name = "post")
@EntityListeners(AuditingEntityListener.class)
public class PostEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "post_id", nullable = false)
  private Long postId;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "member_id", nullable = false)
  private MemberEntity member;

  @Column(name = "title")
  private String title;

  @Column(name = "content")
  private String content;

  @Column(name = "thumbnail")
  private String thumbnail;

  @Column(name = "introduction")
  private String introduction;

  @Column(name = "likes")
  private int likes;

  @Column(name = "views")
  private int views;

  @CreatedDate
  @Column(name = "registered_at", nullable = false)
  private LocalDateTime registeredAt;

  @Column(name = "last_modified_at")
  private LocalDateTime lastModifiedAt;

  public void addView(){
    this.views += 1;
  }

  public void editPost(PostEditRequest request){
    this.title = request.getTitle();
    this.content = request.getContent();
    this.thumbnail = request.getThumbnail();
    this.introduction = request.getIntroduction();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    PostEntity that = (PostEntity) o;
    return Objects.equals(postId, that.postId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(postId);
  }
}
