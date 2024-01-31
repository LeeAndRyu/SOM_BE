package com.blog.som.domain.post.mongo.document;

import com.blog.som.domain.post.entity.PostEntity;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
@Document(collection = "post")
public class PostDocument {

  @Id
  private ObjectId id;

  @Indexed(unique = true)
  private Long postId;

  @Indexed(unique = true)
  private String accountName;

  private Long memberId;

  private String profileImage;

  private String title;

  private String thumbnail;

  private String introduction;

  private String content;

  private int likes;

  private int views;

  private int comments;

  private LocalDateTime registeredAt;

  private List<String> tags = new ArrayList<>();

  public void addView() {
    this.views += 1;
  }

  public void addLikes() {
    this.likes += 1;
  }

  public static PostDocument fromEntity(PostEntity postEntity, List<String> tags) {
    return PostDocument.builder()
        .postId(postEntity.getPostId())
        .memberId(postEntity.getMember().getMemberId())
        .profileImage(postEntity.getMember().getProfileImage())
        .accountName(postEntity.getMember().getAccountName())
        .title(postEntity.getTitle())
        .thumbnail(postEntity.getThumbnail())
        .introduction(postEntity.getIntroduction())
        .content(postEntity.getContent())
        .likes(postEntity.getLikes())
        .views(postEntity.getViews())
        .registeredAt(postEntity.getRegisteredAt())
        .tags(tags)
        .build();
  }
}
