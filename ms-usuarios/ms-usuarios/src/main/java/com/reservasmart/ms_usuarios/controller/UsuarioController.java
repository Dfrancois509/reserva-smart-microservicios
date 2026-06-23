package com.reservasmart.ms_usuarios.controller;

import com.reservasmart.ms_usuarios.dto.ActualizarUsuarioRequest;
import com.reservasmart.ms_usuarios.dto.CrearUsuarioRequest;
import com.reservasmart.ms_usuarios.dto.UsuarioResponse;
import com.reservasmart.ms_usuarios.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/usuarios")
@RequiredArgsConstructor
@Tag(name = "Usuarios", description = "Microservicio para gestión de usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    @PostMapping
    @Operation(summary = "Crear un usuario")
    public UsuarioResponse crear(
            @Valid @RequestBody CrearUsuarioRequest request) {

        return usuarioService.crear(request);
    }

    @GetMapping
    @Operation(summary = "Listar todos los usuarios")
    public List<UsuarioResponse> listarTodos() {

        return usuarioService.listarTodos();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar usuario por ID")
    public UsuarioResponse buscarPorId(
            @PathVariable Long id) {

        return usuarioService.buscarPorId(id);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar usuario")
    public UsuarioResponse actualizar(
            @PathVariable Long id,
            @Valid @RequestBody ActualizarUsuarioRequest request) {

        return usuarioService.actualizar(id, request);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Desactivar usuario")
    public UsuarioResponse desactivar(
            @PathVariable Long id) {

        return usuarioService.desactivar(id);
    }

    @GetMapping("/{id}/activo")
    @Operation(summary = "Verificar si un usuario existe y está activo")
    public boolean existeUsuarioActivo(
            @PathVariable Long id) {

        return usuarioService.existeUsuarioActivo(id);
    }
}