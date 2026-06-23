package com.reservasmart.ms_usuarios.service;

import com.reservasmart.ms_usuarios.dto.ActualizarUsuarioRequest;
import com.reservasmart.ms_usuarios.dto.CrearUsuarioRequest;
import com.reservasmart.ms_usuarios.dto.UsuarioResponse;
import com.reservasmart.ms_usuarios.model.EstadoUsuario;
import com.reservasmart.ms_usuarios.model.Usuario;
import com.reservasmart.ms_usuarios.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioResponse crear(CrearUsuarioRequest request) {

        if (usuarioRepository.existsByEmail(request.getEmail())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Ya existe un usuario con este email"
            );
        }

        Usuario usuario = Usuario.builder()
                .nombre(request.getNombre())
                .email(request.getEmail())
                .telefono(request.getTelefono())
                .estado(EstadoUsuario.ACTIVO)
                .fechaRegistro(LocalDateTime.now())
                .build();

        return convertirAResponse(
                usuarioRepository.save(usuario)
        );
    }

    public List<UsuarioResponse> listarTodos() {

        return usuarioRepository.findAll()
                .stream()
                .map(this::convertirAResponse)
                .toList();
    }

    public UsuarioResponse buscarPorId(Long id) {

        Usuario usuario = obtenerEntidadPorId(id);

        return convertirAResponse(usuario);
    }

    public UsuarioResponse actualizar(
            Long id,
            ActualizarUsuarioRequest request) {

        Usuario usuario = obtenerEntidadPorId(id);

        usuario.setNombre(request.getNombre());
        usuario.setTelefono(request.getTelefono());

        return convertirAResponse(
                usuarioRepository.save(usuario)
        );
    }

    public UsuarioResponse desactivar(Long id) {

        Usuario usuario = obtenerEntidadPorId(id);

        usuario.setEstado(EstadoUsuario.INACTIVO);

        return convertirAResponse(
                usuarioRepository.save(usuario)
        );
    }

    public boolean existeUsuarioActivo(Long id) {

        Usuario usuario = obtenerEntidadPorId(id);

        return usuario.getEstado() == EstadoUsuario.ACTIVO;
    }

    private Usuario obtenerEntidadPorId(Long id) {

        return usuarioRepository.findById(id)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Usuario no encontrado"
                        )
                );
    }

    private UsuarioResponse convertirAResponse(Usuario usuario) {

        return UsuarioResponse.builder()
                .id(usuario.getId())
                .nombre(usuario.getNombre())
                .email(usuario.getEmail())
                .telefono(usuario.getTelefono())
                .estado(usuario.getEstado())
                .fechaRegistro(usuario.getFechaRegistro())
                .build();
    }
}