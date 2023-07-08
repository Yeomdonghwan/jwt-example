package com.example.blogproject.account.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class LoginReqDto {
    @NotBlank
    private String email;
    @NotBlank
    private String password;

}
