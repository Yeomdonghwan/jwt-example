package com.example.blogproject.post;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class PostDto {
    private Long id;
    private String title;
    private String content;
    private Long accountId;
    private Long blogId;
    private String nickName;
    private LocalDateTime createdAt;

    public PostDto(Post post){
        this.id=post.getId();
        this.title=post.getTitle();
        this.content= post.getContent();
        this.accountId=post.getAccount().getId();
        this.nickName= post.getAccount().getNickname();
        this.createdAt=post.getCreatedAt();
        this.blogId=post.getBlog().getId();
    }
}
