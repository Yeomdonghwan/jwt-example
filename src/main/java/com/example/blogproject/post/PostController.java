package com.example.blogproject.post;
import com.example.blogproject.security.user.CurrentUser;
import com.example.blogproject.security.user.UserDetailsImpl;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class PostController {
    private final PostService postService;

    @GetMapping(value = "/posts/{id}")
    public ResponseEntity<PostDto> getPost(@PathVariable Long id) {
        Post post = postService.getPostById(id);
        return new ResponseEntity<>(new PostDto(post), HttpStatus.OK);
    }

//    @GetMapping(value = "/posts")
//    public ResponseEntity<List<PostDto>> getPostList(Pageable pageable) {
//        Page<Post> posts = postService.findAllByOrderByCreatedDateDescPageable(pageable);
//        Page<PostDto> postDto = posts.map(post -> new PostDto((post)));
//        return new ResponseEntity<>(postDto.getContent(), HttpStatus.OK);
//    }

    @GetMapping("/getAuth")
    public ResponseEntity<String> getAuthorities() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        // 사용자의 권한 정보 가져오기
        List<String> authorities = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        // 권한 정보를 원하는 형태로 가공하여 반환



        // 여기서는 간단히 문자열 리스트로 반환하도록 예시로 작성했습니다.
        return ResponseEntity.ok(authorities.toString());
    }
}
