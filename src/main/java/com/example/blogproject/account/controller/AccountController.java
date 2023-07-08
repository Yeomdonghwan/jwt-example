package com.example.blogproject.account.controller;

import com.example.blogproject.account.dto.AccountReqDto;
import com.example.blogproject.account.dto.LoginReqDto;
import com.example.blogproject.account.service.AccountService;
import com.example.blogproject.global.dto.GlobalResDto;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/account")
public class AccountController {
    private final AccountService accountService;

    @PostMapping("/register")
    public ResponseEntity<GlobalResDto> register(@RequestBody @Valid AccountReqDto accountReqDto) {
        GlobalResDto response = accountService.signup(accountReqDto);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/login")
    public ResponseEntity<GlobalResDto> login(@RequestBody @Valid LoginReqDto loginReqDto, HttpServletResponse response) {
        GlobalResDto loginResponse = accountService.login(loginReqDto, response);
        return new ResponseEntity<>(loginResponse, HttpStatus.OK);
    }

}
