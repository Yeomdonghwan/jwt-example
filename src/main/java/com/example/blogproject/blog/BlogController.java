package com.example.blogproject.blog;

import com.example.blogproject.security.user.CurrentUser;
import com.example.blogproject.security.user.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/blog")
public class BlogController {

    private final BlogService blogService;

    @PostMapping("/create")
    @Secured("ROLE_USER")
    ResponseEntity<Long> createBlog(@RequestBody BlogDto blogDto, @CurrentUser UserDetailsImpl userDetails){
        Long blogId = blogService.createBlog(blogDto, userDetails);
        return ResponseEntity.ok(blogId);
    }
}
