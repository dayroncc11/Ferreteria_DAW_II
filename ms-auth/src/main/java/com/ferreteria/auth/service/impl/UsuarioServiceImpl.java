package com.ferreteria.auth.service.impl;

import com.ferreteria.auth.dto.UsuarioRequest;
import com.ferreteria.auth.dto.UsuarioResponse;
import com.ferreteria.auth.entity.Usuario;
import com.ferreteria.auth.exception.BusinessException;
import com.ferreteria.auth.exception.ResourceNotFoundException;
import com.ferreteria.auth.repository.UsuarioRepository;
import com.ferreteria.auth.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public List<UsuarioResponse> listar() {
        return usuarioRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Override
    public UsuarioResponse obtenerPorId(Long id) {
        return toResponse(find(id));
    }

    @Override
    @Transactional
    public UsuarioResponse crear(UsuarioRequest request) {
        if (usuarioRepository.existsByCorreo(request.correo())) {
            throw new BusinessException("Ya existe un usuario con ese correo");
        }
        Usuario usuario = Usuario.builder()
            .nombre(request.nombre())
            .correo(request.correo())
            .clave(passwordEncoder.encode(request.clave()))
            .rol(request.rol())
            .username(request.correo())
            .foto(request.foto())
            .build();
        return toResponse(usuarioRepository.save(usuario));
    }

    @Override
    @Transactional
    public UsuarioResponse actualizar(Long id, UsuarioRequest request) {
        Usuario usuario = find(id);
        usuario.setNombre(request.nombre());
        usuario.setCorreo(request.correo());
        usuario.setRol(request.rol());
        if (request.clave() != null && !request.clave().isBlank()) {
            usuario.setClave(passwordEncoder.encode(request.clave()));
        }
        if (request.foto() != null) {
            usuario.setFoto(request.foto());
        }
        return toResponse(usuarioRepository.save(usuario));
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        usuarioRepository.delete(find(id));
    }

    private Usuario find(Long id) {
        return usuarioRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con id " + id));
    }

    private UsuarioResponse toResponse(Usuario u) {
        return new UsuarioResponse(u.getId(), u.getNombre(), u.getCorreo(), "", u.getRol(), u.getFoto());
    }
}
