package com.example.blogproject.post;
import org.springframework.security.core.GrantedAuthority;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
public class PostController {
    @GetMapping("/posts/{postId}")
    public ResponseEntity<String> getPost(@PathVariable Long postId) {

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
