package com.example.blogproject.account.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class AccountReqDto {

    @NotBlank
    private String nickname;
    @NotBlank
    private String email;
    @NotBlank
    private String password;

    private String passwordCheck;

    public AccountReqDto(String nickname,String email, String pw, String pwck) {
        this.email = email;
        this.password = pw;
        this.passwordCheck = pwck;
        this.nickname=nickname;
    }

    public void setEncodePwd(String encodePwd) {
        this.password = encodePwd;
    }
}
