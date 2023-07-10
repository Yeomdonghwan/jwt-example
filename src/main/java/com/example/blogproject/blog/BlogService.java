package com.example.blogproject.blog;

import com.example.blogproject.account.entity.Account;
import com.example.blogproject.security.user.UserDetailsImpl;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Transactional
@Service
@RequiredArgsConstructor
public class BlogService {
    private final BlogRepository blogRepository;

    public Long createBlog(BlogDto blogDto, UserDetailsImpl userDetails){
        Account account = userDetails.getAccount();
        Blog blog = new Blog(
                null,
                account,
                blogDto.getBlogName(),
                blogDto.getDescription(),
                null
        );
        blogRepository.save(blog);
        return blog.getId();
    }
}
