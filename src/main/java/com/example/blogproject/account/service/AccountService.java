package com.example.blogproject.account.service;

import com.example.blogproject.account.dto.AccountReqDto;
import com.example.blogproject.account.dto.LoginReqDto;
import com.example.blogproject.account.entity.Account;
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

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AccountService {
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;
    private final AccountRepository accountRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    @Transactional
    public GlobalResDto signup(AccountReqDto accountReqDto) {
        // nickname �ߺ��˻�
        if(accountRepository.findByEmail(accountReqDto.getEmail()).isPresent()){
            throw new RuntimeException("Overlap Check");
        }

        // �н����� ��ȣȭ
        accountReqDto.setEncodePwd(passwordEncoder.encode(accountReqDto.getPassword()));
        Account account = new Account(accountReqDto);

        // ȸ������ ����
        accountRepository.save(account);
        return new GlobalResDto("Success signup", HttpStatus.OK.value());
    }

    @Transactional
    public GlobalResDto login(LoginReqDto loginReqDto, HttpServletResponse response) {

        // ���̵� �˻�
        Account account = accountRepository.findByEmail(loginReqDto.getEmail()).orElseThrow(
                () -> new RuntimeException("Not found Account")
        );

        // ��й�ȣ �˻�
        if(!passwordEncoder.matches(loginReqDto.getPassword(), account.getPassword())) {
            throw new RuntimeException("Not matches Password");
        }

        // ���̵� ������ Token����
        TokenDto tokenDto = jwtUtil.createAllToken(loginReqDto.getEmail());

        // Refresh��ū �ִ��� Ȯ��
        Optional<RefreshToken> refreshToken = refreshTokenRepository.findByAccountEmail(loginReqDto.getEmail());

        // �ִٸ� ����ū �߱��� ������Ʈ
        // ���ٸ� ���� ����� ��� ����
        if(refreshToken.isPresent()) {
            refreshTokenRepository.save(refreshToken.get().updateToken(tokenDto.getRefreshToken()));
        }else {
            RefreshToken newToken = new RefreshToken(tokenDto.getRefreshToken(), loginReqDto.getEmail());
            refreshTokenRepository.save(newToken);
        }

        // response ����� Access Token / Refresh Token ����
        setHeader(response, tokenDto);

        return new GlobalResDto("Success Login", HttpStatus.OK.value());

    }

    private void setHeader(HttpServletResponse response, TokenDto tokenDto) {
        response.addHeader(JwtUtil.ACCESS_TOKEN, tokenDto.getAccessToken());
        response.addHeader(JwtUtil.REFRESH_TOKEN, tokenDto.getRefreshToken());
    }
}
