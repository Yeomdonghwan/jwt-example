package com.example.blogproject.postTag;

import com.example.blogproject.post.Post;
import com.example.blogproject.tag.Tag;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
@Entity(name="post_tag")
public class PostTag {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="post_tag_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name="post_id")
    private Post post;

    @ManyToOne
    @JoinColumn(name="tag_id")
    private Tag tag;

}
