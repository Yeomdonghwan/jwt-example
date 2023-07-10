package com.example.blogproject.category;

import com.example.blogproject.blog.Blog;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
@Entity
public class Category {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="category_id")
    private Long id;

    private String category_name;

    @ManyToOne
    @JoinColumn(name="blog_id")
    private Blog blog;
}
