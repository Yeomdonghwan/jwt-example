package com.example.blogproject.post;

import com.example.blogproject.account.entity.Account;
import com.example.blogproject.blog.Blog;
import com.example.blogproject.category.Category;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
@Getter
@Setter
@Entity
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class Post {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="post_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name="category_id")
    private Category category;

    @ManyToOne
    @JoinColumn(name="blog_id")
    private Blog blog;

    @ManyToOne
    @JoinColumn(name="account_id")
    private Account account;

    private String title;
    private String content;
    private LocalDateTime createdAt;

}
