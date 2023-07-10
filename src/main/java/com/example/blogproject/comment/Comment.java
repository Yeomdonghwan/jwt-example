package com.example.blogproject.comment;

import com.example.blogproject.account.entity.Account;
import com.example.blogproject.post.Post;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@Getter
@Entity
public class Comment {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="comment_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name="account_id")
    private Account account;

    @ManyToOne
    @JoinColumn(name="post_id")
    private Post post;

    private String content;
    private LocalDateTime createdAt;
}
