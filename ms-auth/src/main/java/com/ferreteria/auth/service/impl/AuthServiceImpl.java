package com.ferreteria.auth.service.impl;

import com.ferreteria.auth.config.JwtService;
import com.ferreteria.auth.dto.AuthLoginRequest;
import com.ferreteria.auth.dto.AuthLoginResponse;
import com.ferreteria.auth.entity.Usuario;
import com.ferreteria.auth.exception.BusinessException;
import com.ferreteria.auth.repository.UsuarioRepository;
import com.ferreteria.auth.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Override
    public AuthLoginResponse login(AuthLoginRequest request) {
        log.info("Intento de login: {}", request.correo());

        Usuario usuario = usuarioRepository.findByCorreo(request.correo())
            .orElseThrow(() -> new BusinessException("Credenciales incorrectas"));

        if (!passwordEncoder.matches(request.contrasena(), usuario.getClave())) {
            log.warn("Contraseña incorrecta para: {}", request.correo());
            throw new BusinessException("Credenciales incorrectas");
        }

        String token = jwtService.generateToken(usuario.getCorreo(), usuario.getRol());
        log.info("Login exitoso: {} [{}]", usuario.getCorreo(), usuario.getRol());

        return new AuthLoginResponse(token, usuario.getId(), usuario.getNombre(),
            usuario.getCorreo(), usuario.getRol(), usuario.getFoto());
    }
}
