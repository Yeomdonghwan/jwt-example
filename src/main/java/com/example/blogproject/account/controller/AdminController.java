package com.example.blogproject.account.controller;

import com.example.blogproject.global.dto.GlobalResDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @GetMapping("/")
    ResponseEntity<GlobalResDto> adminOnly(){
        return new ResponseEntity(new GlobalResDto("success",200),HttpStatus.OK);
    }
}
