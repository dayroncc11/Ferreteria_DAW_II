package com.ferreteria.auth.controller;

import com.ferreteria.auth.dto.AuthLoginRequest;
import com.ferreteria.auth.dto.AuthLoginResponse;
import com.ferreteria.auth.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.ferreteria.auth.entity.Usuario;
import com.ferreteria.auth.repository.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public ResponseEntity<AuthLoginResponse> login(@Valid @RequestBody AuthLoginRequest request) {
        AuthLoginResponse response = authService.login(request);
        System.out.println("=============== [MS-AUTH] ===============");
        System.out.println("  Operacion : POST /api/auth/login");
        System.out.println("  Estado    : LOGIN EXITOSO");
        System.out.println("  Usuario   : " + response.correo());
        System.out.println("  Rol       : " + response.rol());
        System.out.println("=========================================");
        log.info("Login exitoso — usuario: {}", response.correo());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/register-admin")
    public ResponseEntity<String> registerAdmin() {
        if (usuarioRepository.findByCorreo("test@ferreteria.com").isEmpty()) {
            Usuario testAdmin = new Usuario();
            testAdmin.setNombre("Admin de Prueba");
            testAdmin.setCorreo("test@ferreteria.com");
            testAdmin.setUsername("testadmin");
            testAdmin.setClave(passwordEncoder.encode("test123"));
            testAdmin.setRol("admin");
            usuarioRepository.save(testAdmin);
            return ResponseEntity.ok("Usuario test@ferreteria.com con password test123 creado exitosamente.");
        }
        return ResponseEntity.ok("El usuario test@ferreteria.com ya existe.");
    }
}
