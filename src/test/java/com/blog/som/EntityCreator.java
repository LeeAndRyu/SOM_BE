package com.blog.som;

import com.blog.som.domain.follow.entity.FollowEntity;
import com.blog.som.domain.member.entity.MemberEntity;
import com.blog.som.domain.member.type.Role;
import com.blog.som.domain.post.elasticsearch.document.PostEsDocument;
import com.blog.som.domain.post.entity.PostEntity;
import com.blog.som.domain.post.mongo.document.PostDocument;
import com.blog.som.domain.tag.entity.PostTagEntity;
import com.blog.som.domain.tag.entity.TagEntity;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EntityCreator {

  public static MemberEntity createMember(Long id) {
    return MemberEntity.builder()
        .memberId(id)
        .email("test" + id + "@test.com")
        .password("password" + id)
        .nickname("nickname" + id)
        .accountName("testAccountName" + id)
        .blogName("testAccountName" + id + ".som")
        .introduction("hello" + id)
        .profileImage("test-profile-image" + id + ".jpg")
        .followerCount(0)
        .followingCount(0)
        .registeredAt(LocalDateTime.now())
        .role(Role.USER)
        .build();
  }

  public static PostEntity createPost(Long id, MemberEntity member) {
    return PostEntity.builder()
        .postId(id)
        .member(member)
        .title("test-title" + id)
        .content("test-content" + id)
        .thumbnail("test-thumbnail" + id + ".jpg")
        .introduction("test-introduction" + id)
        .likes(0)
        .views(0)
        .registeredAt(LocalDateTime.now())
        .lastModifiedAt(LocalDateTime.now())
        .build();
  }

  public static TagEntity createTag(Long id, String tagName, MemberEntity member) {
    TagEntity tagEntity = new TagEntity(tagName, member);
    tagEntity.setTagId(id);
    return tagEntity;
  }

  public static PostTagEntity createPostTag(Long id, PostEntity post, TagEntity tag) {
    return PostTagEntity.builder()
        .postTagId(id)
        .post(post)
        .tag(tag)
        .build();
  }

  public static FollowEntity createFollowEntity(Long id, MemberEntity fromMember, MemberEntity toMember){
    return FollowEntity.builder()
        .followId(id)
        .fromMember(fromMember)
        .toMember(toMember)
        .followAt(LocalDateTime.now())
        .build();
  }

  public static PostDocument createPostDocument(PostEntity postEntity, List<String> tags){
    return PostDocument.builder()
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

  public static PostDocument createPostDocument(PostEntity postEntity){
    return PostDocument.builder()
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
        .tags(new ArrayList<>(Arrays.asList("tag1, tag2")))
        .build();
  }

  public static PostEsDocument createPostEsDocument(PostEntity postEntity, List<String> tags){
    return PostEsDocument.builder()
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

  public static PostEsDocument createPostEsDocument(PostEntity postEntity){
    return PostEsDocument.builder()
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
        .tags(new ArrayList<>(Arrays.asList("tag1, tag2")))
        .build();
  }
}
