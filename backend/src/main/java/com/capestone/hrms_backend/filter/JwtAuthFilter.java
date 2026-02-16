package com.capestone.hrms_backend.filter;

import com.capestone.hrms_backend.entity.organization.Employee;
import com.capestone.hrms_backend.entity.organization.User;
import com.capestone.hrms_backend.repository.organization.UserRepository;
import com.capestone.hrms_backend.security.HrmsUserDetails;
import com.capestone.hrms_backend.security.HrmsUserDetailsService;
import com.capestone.hrms_backend.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthFilter extends OncePerRequestFilter {
    private final JwtUtil jwtUtil;
    private final HrmsUserDetailsService hrmsUserDetailsService;

    @Override
    public void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        //String header = request.getHeader("Authorization");
        String token = resolveToken(request);
        log.info("Token: {}",token);
        if(token != null && jwtUtil.isValid(token)){
            String email = jwtUtil.getEmail(token);
            var user = hrmsUserDetailsService.loadUserByUsername(email);

            UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(user,null,user.getAuthorities());
            auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(auth);
        }
        filterChain.doFilter(request,response);
    }

    private String resolveToken(HttpServletRequest request){
        String header = request.getHeader("Authorization");
        log.info("header: {}",header);
        if(header != null && header.startsWith("Bearer "))
            return header.substring(7);

        if(request.getCookies() != null){
            for(Cookie cookie: request.getCookies()){
                if("hrms_token".equals(cookie.getName())) {
                    log.info("Cookie: {}", cookie.getValue());
                    return cookie.getValue();
                }
            }
        }
        return null;
    }


}
