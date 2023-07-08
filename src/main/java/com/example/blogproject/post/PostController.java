package com.example.blogproject.post;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PostController {
    @GetMapping("/posts/{postId}")
    public ResponseEntity<String> getPost(@PathVariable Long postId) {

        return ResponseEntity.ok(null);
    }
}
