package com.blog.som.domain.mybatis;

import com.blog.som.domain.post.entity.PostEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface BlogPostMapper {

    PostEntity findById(Long postId);

    List<BlogPostWithTagString> findPostsByMemberId(
            @Param("memberId") Long memberId,@Param("sort") String sort, @Param("value") String value, @Param("pageStart") int pageStart, @Param("pageSize") int pageSize);

}
