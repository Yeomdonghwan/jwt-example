package com.example.blogproject.post;

import com.example.blogproject.account.entity.Account;
import com.example.blogproject.blog.Blog;
import com.example.blogproject.security.user.UserDetailsImpl;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.webjars.NotFoundException;

@Service
@Transactional
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    public Post getPostById(Long id) {
        Post post = postRepository.findById(id).orElseThrow(()-> new NotFoundException("post does not exist"));
        return post;
    }

}
