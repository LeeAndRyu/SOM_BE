package com.blog.som.domain.post.elasticsearch.document;


import com.blog.som.domain.post.entity.PostEntity;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.Mapping;
import org.springframework.data.elasticsearch.annotations.Setting;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
@Setting(settingPath = "static/es/es-settings.json")
@Mapping(mappingPath = "static/es/post-mapping.json")
@Document(indexName = "post")
public class PostDocument {

  @Id
  @Field(name = "id", type = FieldType.Keyword)
  private Long id;

  @Field(name = "post_id", type = FieldType.Long)
  private Long postId;

  @Field(name = "member_id", type = FieldType.Long)
  private Long memberId;

  @Field(name = "account_name", type = FieldType.Text)
  private String accountName;

  @Field(name = "title", type = FieldType.Text)
  private String title;

  @Field(name = "thumbnail", type = FieldType.Text)
  private String thumbnail;

  @Field(name = "introduction", type = FieldType.Text)
  private String introduction;

  @Field(name = "content", type = FieldType.Text)
  private String content;

  @Field(name = "likes", type = FieldType.Integer)
  private int likes;

  @Field(name = "views", type = FieldType.Integer)
  private int views;

  @Field(name = "registered_at", type = FieldType.Date, format = {DateFormat.date_hour_minute_second_millis, DateFormat.epoch_millis})
  private LocalDateTime registeredAt;

  @Field(name = "tags", type = FieldType.Text)
  private List<String> tags = new ArrayList<>();

  public void addView(){
    this.views += 1;
  }

  public void addLikes(){
    this.likes += 1;
  }

  public static PostDocument fromEntity(PostEntity postEntity, List<String> tags){
    return PostDocument.builder()
        .id(postEntity.getPostId())
        .postId(postEntity.getPostId())
        .memberId(postEntity.getMember().getMemberId())
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

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    PostDocument that = (PostDocument) o;
    return Objects.equals(id, that.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }
}
