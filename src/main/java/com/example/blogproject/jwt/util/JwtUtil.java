package com.example.blogproject.jwt.util;

import com.example.blogproject.account.entity.RefreshToken;
import com.example.blogproject.account.repository.RefreshTokenRepository;
import com.example.blogproject.jwt.dto.TokenDto;
import com.example.blogproject.security.user.UserDetailsServiceImpl;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtUtil {
    private final UserDetailsServiceImpl userDetailsService;
    private final RefreshTokenRepository refreshTokenRepository;

//    private static final long ACCESS_TIME = 30 * 60 * 1000L;
//    private static final long REFRESH_TIME =  7 * 24 * 60 * 60 * 1000L;

    private static final long ACCESS_TIME = 10 *  60 * 1000L  ;
    private static final long REFRESH_TIME = 10*  2 * 60 * 1000L ;
    public static final String ACCESS_TOKEN = "Access_Token";
    public static final String REFRESH_TOKEN = "Refresh_Token";


    @Value("${jwt.secret.key}")
    private String secretKey;
    private Key key;
    private final SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

    // bean���� ��� �Ǹ鼭 �� �ѹ� ������ �˴ϴ�.
    @PostConstruct
    public void init() {
        byte[] bytes = Base64.getDecoder().decode(secretKey);
        key = Keys.hmacShaKeyFor(bytes);
    }

    // header ��ū�� �������� ���
    public String getHeaderToken(HttpServletRequest request, String type) {
        return type.equals("Access") ? request.getHeader(ACCESS_TOKEN) :request.getHeader(REFRESH_TOKEN);
    }

    // ��ū ����
    public TokenDto createAllToken(String nickname) {
        return new TokenDto(createToken(nickname, "Access"), createToken(nickname, "Refresh"));
    }

    public String createToken(String nickname, String type) {

        Date date = new Date();

        long time = type.equals("Access") ? ACCESS_TIME : REFRESH_TIME;

        return Jwts.builder()
                .setSubject(nickname)
                .setExpiration(new Date(date.getTime() + time))
                .setIssuedAt(date)
                .signWith(key, signatureAlgorithm)
                .compact();

    }

    // ��ū ����
    public Boolean tokenValidation(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (Exception ex) {
            log.error(ex.getMessage());
            return false;
        }
    }

    // refreshToken ��ū ����
    // db�� ����Ǿ� �ִ� token�� ��
    // db�� �����Ѵٴ� ���� jwt token�� ����Ѵٴ� ������ ����Ų��.
    // db ���ٴ� redis�� ����ϴ� ���� ���� ����. (in-memory db�� ������ ��ȸ�ӵ��� ������ �ֱ������� �����ϴ� ����� �⺻������ �����մϴ�.)
    public Boolean refreshTokenValidation(String token) {

        // 1�� ��ū ����
        if(!tokenValidation(token)) return false;

        // DB�� ������ ��ū ��
        Optional<RefreshToken> refreshToken = refreshTokenRepository.findByAccountEmail(getEmailFromToken(token));

        return refreshToken.isPresent() && token.equals(refreshToken.get().getRefreshToken());
    }

    // ���� ��ü ����
    public Authentication createAuthentication(String email) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(email);
        // spring security ������ ������ �ִ� ��ü�Դϴ�. (UsernamePasswordAuthenticationToken)
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    // ��ū���� email �������� ���
    public String getEmailFromToken(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody().getSubject();
    }

    // ��� ��ū ��� ����
    public void setHeaderAccessToken(HttpServletResponse response, String accessToken) {
        response.setHeader("Access_Token", accessToken);
    }

    // �������� ��ū ��� ����
    public void setHeaderRefreshToken(HttpServletResponse response, String refreshToken) {
        response.setHeader("Refresh_Token", refreshToken);
    }
}
