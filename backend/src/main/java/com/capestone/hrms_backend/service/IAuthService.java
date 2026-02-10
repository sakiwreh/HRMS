package com.capestone.hrms_backend.service;

import com.capestone.hrms_backend.dto.request.AuthRequestDto;
import com.capestone.hrms_backend.dto.response.AuthResponseDto;

public interface IAuthService {
    AuthResponseDto login(AuthRequestDto requestDto);
}
