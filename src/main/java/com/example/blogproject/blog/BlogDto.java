package com.example.blogproject.blog;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BlogDto {
    private Long id;
    private String blogName;
    private String description;

    private String ownerNickname;
    private LocalDateTime createdAt;
}
