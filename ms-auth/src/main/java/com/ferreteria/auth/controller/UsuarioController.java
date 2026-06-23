package com.ferreteria.auth.controller;

import com.ferreteria.auth.dto.UsuarioRequest;
import com.ferreteria.auth.dto.UsuarioResponse;
import com.ferreteria.auth.service.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService usuarioService;

    @GetMapping
    public ResponseEntity<List<UsuarioResponse>> listar() {
        List<UsuarioResponse> usuarios = usuarioService.listar();
        System.out.println("=============== [MS-AUTH] ===============");
        System.out.println("  Operacion : GET /api/usuarios");
        System.out.println("  Estado    : CONSULTA EXITOSA");
        System.out.println("  Resultado : " + usuarios.size() + " usuario(s) encontrado(s)");
        System.out.println("=========================================");
        return ResponseEntity.ok(usuarios);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioResponse> obtenerPorId(@PathVariable Long id) {
        UsuarioResponse usuario = usuarioService.obtenerPorId(id);
        System.out.println("=============== [MS-AUTH] ===============");
        System.out.println("  Operacion : GET /api/usuarios/" + id);
        System.out.println("  Estado    : CONSULTA EXITOSA");
        System.out.println("  Usuario   : " + usuario.correo());
        System.out.println("=========================================");
        return ResponseEntity.ok(usuario);
    }

    @PostMapping
    public ResponseEntity<UsuarioResponse> crear(@Valid @RequestBody UsuarioRequest request) {
        UsuarioResponse response = usuarioService.crear(request);
        System.out.println("=============== [MS-AUTH] ===============");
        System.out.println("  Operacion : POST /api/usuarios");
        System.out.println("  Estado    : REGISTRO EXITOSO");
        System.out.println("  Usuario   : " + response.correo());
        System.out.println("=========================================");
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UsuarioResponse> actualizar(@PathVariable Long id, @Valid @RequestBody UsuarioRequest request) {
        UsuarioResponse response = usuarioService.actualizar(id, request);
        System.out.println("=============== [MS-AUTH] ===============");
        System.out.println("  Operacion : PUT /api/usuarios/" + id);
        System.out.println("  Estado    : ACTUALIZACION EXITOSA");
        System.out.println("  Usuario   : " + response.correo());
        System.out.println("=========================================");
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        usuarioService.eliminar(id);
        System.out.println("=============== [MS-AUTH] ===============");
        System.out.println("  Operacion : DELETE /api/usuarios/" + id);
        System.out.println("  Estado    : ELIMINACION EXITOSA");
        System.out.println("  ID        : " + id);
        System.out.println("=========================================");
        return ResponseEntity.noContent().build();
    }
}
