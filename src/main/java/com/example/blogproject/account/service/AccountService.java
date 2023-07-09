package com.example.blogproject.account.service;

import com.example.blogproject.account.dto.AccountReqDto;
import com.example.blogproject.account.dto.LoginReqDto;
import com.example.blogproject.account.entity.Account;
import com.example.blogproject.account.entity.Authority;
import com.example.blogproject.account.entity.RefreshToken;
import com.example.blogproject.account.repository.AccountRepository;
import com.example.blogproject.account.repository.RefreshTokenRepository;
import com.example.blogproject.global.dto.GlobalResDto;
import com.example.blogproject.jwt.dto.TokenDto;
import com.example.blogproject.jwt.util.JwtUtil;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AccountService {
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;
    private final AccountRepository accountRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    @Transactional
    public GlobalResDto signup(AccountReqDto accountReqDto) {
        //nickname 중복검사
        if(accountRepository.findOneWithAuthoritiesByEmail(accountReqDto.getEmail()).isPresent()){
            throw new RuntimeException("Overlap Check");
        }

        // 패스워드 암호화
        accountReqDto.setEncodePwd(passwordEncoder.encode(accountReqDto.getPassword()));
        Account account = new Account(accountReqDto);

        //기본권한 설정
        Authority authority = Authority.builder()
                .authorityName("ROLE_USER")
                .build();
        account.setAuthorities(Collections.singleton(authority));
        // 회원가입 성공
        accountRepository.save(account);
        return new GlobalResDto("Success signup", HttpStatus.OK.value());
    }

    @Transactional
    public GlobalResDto login(LoginReqDto loginReqDto, HttpServletResponse response) {

        // 아이디 검사
        Account account = accountRepository.findOneWithAuthoritiesByEmail(loginReqDto.getEmail()).orElseThrow(
                () -> new RuntimeException("Not found Account")
        );

        // 비밀번호 검사
        if(!passwordEncoder.matches(loginReqDto.getPassword(), account.getPassword())) {
            throw new RuntimeException("Not matches Password");
        }

        // 아이디 정보로 Token생성
        TokenDto tokenDto = jwtUtil.createAllToken(loginReqDto.getEmail(),account.getAuthorities());

        // Refresh토큰 있는지 확인
        Optional<RefreshToken> refreshToken = refreshTokenRepository.findByAccountEmail(loginReqDto.getEmail());

        // 있다면 새토큰 발급후 업데이트
        // 없다면 새로 만들고 디비 저장
        if(refreshToken.isPresent()) {
            refreshTokenRepository.save(refreshToken.get().updateToken(tokenDto.getRefreshToken()));
        }else {
            RefreshToken newToken = new RefreshToken(tokenDto.getRefreshToken(), loginReqDto.getEmail());
            refreshTokenRepository.save(newToken);
        }

        // response 헤더에 Access Token / Refresh Token 넣음
        setHeader(response, tokenDto);

        return new GlobalResDto("Success Login", HttpStatus.OK.value());

    }

    private void setHeader(HttpServletResponse response, TokenDto tokenDto) {
        response.addHeader(JwtUtil.ACCESS_TOKEN, tokenDto.getAccessToken());
        response.addHeader(JwtUtil.REFRESH_TOKEN, tokenDto.getRefreshToken());
    }

    public Set<Authority> getAuthorities(String loginId) {
        Account account = accountRepository.findOneWithAuthoritiesByEmail(loginId).orElseThrow(
        );
        return account.getAuthorities();
    }
}
