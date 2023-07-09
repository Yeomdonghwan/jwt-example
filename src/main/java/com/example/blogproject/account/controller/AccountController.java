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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

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
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin")
    public ResponseEntity<GlobalResDto> adminOnlyMethod() {
        // ROLE_ADMIN 권한이 있는 사용자만 실행 가능한 로직
        return new ResponseEntity<>(new GlobalResDto("success!!",200),HttpStatus.OK);
    }
}
