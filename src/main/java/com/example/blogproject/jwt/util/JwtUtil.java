package com.example.blogproject.jwt.util;

import com.example.blogproject.account.entity.Authority;
import com.example.blogproject.account.entity.RefreshToken;
import com.example.blogproject.account.repository.RefreshTokenRepository;
import com.example.blogproject.jwt.dto.TokenDto;
import com.example.blogproject.redis.RedisService;
import com.example.blogproject.security.user.UserDetailsServiceImpl;
import io.jsonwebtoken.Claims;
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
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtUtil {
    private final UserDetailsServiceImpl userDetailsService;
    private final RefreshTokenRepository refreshTokenRepository;

    private final RedisService redisService;

//    private static final long ACCESS_TIME = 30 * 60 * 1000L;
//    private static final long REFRESH_TIME =  7 * 24 * 60 * 60 * 1000L;

    private static final long ACCESS_TIME = 10 *  60 * 1000L  ;
    private static final long REFRESH_TIME = 10*  2 * 60 * 1000L ;
    public static final String ACCESS_TOKEN = "Access_Token";
    public static final String REFRESH_TOKEN = "Refresh_Token";

    private static final String AUTHORITIES_KEY = "authorities";


    @Value("${jwt.secret.key}")
    private String secretKey;
    private Key key;
    private final SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

    // bean으로 등록 되면서 딱 한번 실행이 됩니다.
    @PostConstruct
    public void init() {
        byte[] bytes = Base64.getDecoder().decode(secretKey);
        key = Keys.hmacShaKeyFor(bytes);
    }

    // header 토큰을 가져오는 기능
    public String getHeaderToken(HttpServletRequest request, String type) {
        return type.equals("Access") ? request.getHeader(ACCESS_TOKEN) :request.getHeader(REFRESH_TOKEN);
    }

    // 토큰 생성
    public TokenDto createAllToken(String email, Set<Authority> authorities) {
        return new TokenDto(createAccessToken(email, authorities), createRefreshToken(email,authorities));
    }

    public String createAccessToken(String email, Set<Authority> authorities) {

        Date date = new Date();

        long time= ACCESS_TIME;


        String str_authorities = authorities.stream()
                .map(Authority::getAuthorityName)
                .collect(Collectors.joining(","));

        return Jwts.builder()
                .setSubject(email)
                .setExpiration(new Date(date.getTime() + time))
                .setIssuedAt(date)
                .claim(AUTHORITIES_KEY,str_authorities)
                .signWith(key, signatureAlgorithm)
                .compact();

    }

    public String createRefreshToken(String email, Set<Authority> authorities) {

        Date date = new Date();

        long time = REFRESH_TIME;
        String str_authorities = authorities.stream()
                .map(Authority::getAuthorityName)
                .collect(Collectors.joining(","));

        return Jwts.builder()
                .setSubject(email)
                .setExpiration(new Date(date.getTime() + time))
                .setIssuedAt(date)
                .claim(AUTHORITIES_KEY,str_authorities)
                .signWith(key, signatureAlgorithm)
                .compact();
    }
    // 토큰 검증
    public Boolean tokenValidation(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (Exception ex) {
            log.error(ex.getMessage());
            return false;
        }
    }

    // refreshToken 토큰 검증
    // db에 저장되어 있는 token과 비교
    // db에 저장한다는 것이 jwt token을 사용한다는 강점을 상쇄시킨다.
    // db 보다는 redis를 사용하는 것이 더욱 좋다. (in-memory db기 때문에 조회속도가 빠르고 주기적으로 삭제하는 기능이 기본적으로 존재합니다.)
    public Boolean refreshTokenValidation(String token) {

        // 1차 토큰 검증
        if(!tokenValidation(token)) return false;

        // DB에 저장한 토큰 비교
        Optional<RefreshToken> refreshToken = refreshTokenRepository.findByAccountEmail(getEmailFromToken(token));

        return refreshToken.isPresent() && token.equals(refreshToken.get().getRefreshToken());
    }

    // 인증 객체 생성
    //이거 사용하지않음
    public Authentication createAuthentication(String email) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(email);
        // spring security 내에서 가지고 있는 객체입니다. (UsernamePasswordAuthenticationToken)
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    // 토큰에서 email 가져오는 기능
    //이거 사용하지않음
    public String getEmailFromToken(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody().getSubject();
    }

    //토큰에서 authorities 가져오는 기능
    public Set<Authority> getAuthoritiesFromToken(String token) {
        Claims claims = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
//        List<String> authorityNames = claims.get("authorities", List.class);
//        return authorityNames.stream()
//                .map(authorityName -> new Authority(authorityName)) // Authority 생성자에 맞게 수정
//                .collect(Collectors.toSet());
        Set<Authority> authorities = Arrays.stream(claims.get(AUTHORITIES_KEY).toString().split(","))
                .map(Authority::new)
                .collect(Collectors.toSet());
        return authorities;
    }

    //토큰을 받아서 토큰정보를 이용해 authentication객체를 리턴
    public Authentication getAuthentication(String token) {
        Claims claims = Jwts
                .parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();

        Collection<? extends GrantedAuthority> authorities =
                Arrays.stream(claims.get(AUTHORITIES_KEY).toString().split(","))
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());

//        User principal = new User(claims.getSubject(), "", authorities);

        /**
         * 여기부턴 내가바꾼거
         * */
        UserDetails userDetails = userDetailsService.loadUserByUsername(claims.getSubject());

//        return new UsernamePasswordAuthenticationToken(principal, token, authorities);
        return new UsernamePasswordAuthenticationToken(userDetails, token, authorities);
    }

    // 어세스 토큰 헤더 설정
    public void setHeaderAccessToken(HttpServletResponse response, String accessToken) {
        response.setHeader("Access_Token", accessToken);
    }

    // 리프레시 토큰 헤더 설정
    public void setHeaderRefreshToken(HttpServletResponse response, String refreshToken) {
        response.setHeader("Refresh_Token", refreshToken);
    }


}
