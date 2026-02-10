package com.capestone.hrms_backend.controller.Auth;

import com.capestone.hrms_backend.dto.request.AuthRequestDto;
import com.capestone.hrms_backend.dto.response.AuthResponseDto;
import com.capestone.hrms_backend.service.IAuthService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {
    private final IAuthService authService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDto> login(@Valid @RequestBody AuthRequestDto request, HttpServletResponse response){
        AuthResponseDto result = authService.login(request);

//        Cookie cookie = new Cookie("hrms_token", result.getToken());
//        cookie.setHttpOnly(true);
//        cookie.setPath("/");
//        cookie.setMaxAge(86400);
//        response.addCookie(cookie);


        log.info(result.getRole());
        return ResponseEntity.ok(result);
    }
}
