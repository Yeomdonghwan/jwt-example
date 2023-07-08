package com.example.blogproject.jwt.filter;

import com.example.blogproject.global.dto.GlobalResDto;
import com.example.blogproject.jwt.util.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {
    private final JwtUtil jwtUtil;

    @Override
    // HTTP ��û�� ���� WAS(tomcat)�� HttpServletRequest, HttpServletResponse ��ü�� ����� �ݴϴ�.
    // ���� ���� ���� �޾ƿɴϴ�.
    // ��û�� ������ diFilterInternal �� �� �ѹ� ����ȴ�.
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // WebSecurityConfig ���� ���Ҵ� UsernamePasswordAuthenticationFilter ���� ���� ������ �ϰ� �˴ϴ�.

        // Access / Refresh ������� ��ū�� ������.
        String accessToken = jwtUtil.getHeaderToken(request, "Access");
        String refreshToken = jwtUtil.getHeaderToken(request, "Refresh");

        if(accessToken != null) {
            // ��� ��ū���� ��ȿ�ϴٸ� setAuthentication�� ����
            // security context�� ���� ��������
            if(jwtUtil.tokenValidation(accessToken)){
                setAuthentication(jwtUtil.getEmailFromToken(accessToken));
            }
            // ��� ��ū�� ����� ��Ȳ && �������� ��ū ���� �����ϴ� ��Ȳ
            else if (refreshToken != null) {
                // �������� ��ū ���� && �������� ��ū DB����  ��ū �������� Ȯ��
                boolean isRefreshToken = jwtUtil.refreshTokenValidation(refreshToken);
                // �������� ��ū�� ��ȿ�ϰ� �������� ��ū�� DB�� �������� �Ȱ��ٸ�
                if (isRefreshToken) {
                    // �������� ��ū���� ���̵� ���� ��������
                    String loginId = jwtUtil.getEmailFromToken(refreshToken);
                    // ���ο� ��� ��ū �߱�
                    String newAccessToken = jwtUtil.createToken(loginId, "Access");
                    // ����� ��� ��ū �߰�
                    jwtUtil.setHeaderAccessToken(response, newAccessToken);
                    // Security context�� ���� ���� �ֱ�
                    setAuthentication(jwtUtil.getEmailFromToken(newAccessToken));
                }
                // �������� ��ū�� ���� || �������� ��ū�� DB�� �������� �Ȱ��� �ʴٸ�
                else {
                    jwtExceptionHandler(response, "RefreshToken Expired", HttpStatus.BAD_REQUEST);
                    return;
                }
            }
        }

        filterChain.doFilter(request,response);
    }

    // SecurityContext �� Authentication ��ü�� �����մϴ�.
    public void setAuthentication(String email) {
        Authentication authentication = jwtUtil.createAuthentication(email);
        // security�� ������ִ� securityContextHolder �� �ȿ� authentication�� �־��ݴϴ�.
        // security�� securitycontextholder���� ���� ��ü�� Ȯ���ϴµ�
        // jwtAuthfilter���� authentication�� �־��ָ� UsernamePasswordAuthenticationFilter ���ο��� ������ �� ���� Ȯ���ϰ� �߰����� �۾��� �������� �ʽ��ϴ�.
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    // Jwt ����ó��
    public void jwtExceptionHandler(HttpServletResponse response, String msg, HttpStatus status) {
        response.setStatus(status.value());
        response.setContentType("application/json");
        try {
            String json = new ObjectMapper().writeValueAsString(new GlobalResDto(msg, status.value()));
            response.getWriter().write(json);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }
}
