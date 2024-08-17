package com.blog.som.domain.mybatis;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class BlogPostWithTagString {
        private Long postId;
        private Long memberId;
        private String profileImage;
        private String accountName;
        private String title;
        private String thumbnail;
        private String introduction;
        private int likes;
        private int views;
        private int comments;
        private LocalDateTime registeredAt;
        private String tags; // 콤마로 구분된 태그 문자열
}
