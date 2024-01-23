package com.blog.som.domain.tag.entity;

import com.blog.som.domain.member.entity.MemberEntity;
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
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity(name = "tag")
@EntityListeners(AuditingEntityListener.class)
public class TagEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "tag_id", nullable = false)
  private Long tagId;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "member_id", nullable = false)
  private MemberEntity member;

  @Column(name = "tag_name")
  private String tagName;

  @Column(name = "count")
  private int count;

  public void addCount(){
    this.count += 1;
  }

  public void minusCount(){
    this.count -= 1;
  }

  public TagEntity(String tagName, MemberEntity member) {
    this.member = member;
    this.tagName = tagName;
    this.count = 1;
  }
}
