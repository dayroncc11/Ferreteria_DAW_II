package com.ferreteria.auth.service;

import com.ferreteria.auth.dto.AuthLoginRequest;
import com.ferreteria.auth.dto.AuthLoginResponse;

public interface AuthService {
    AuthLoginResponse login(AuthLoginRequest request);
}
