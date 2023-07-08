package com.example.blogproject.account.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class AccountReqDto {

    @NotBlank
    private String email;
    @NotBlank
    private String password;

    private String passwordCheck;

    public AccountReqDto(String email, String pw, String pwck) {
        this.email = email;
        this.password = pw;
        this.passwordCheck = pwck;
    }

    public void setEncodePwd(String encodePwd) {
        this.password = encodePwd;
    }
}
