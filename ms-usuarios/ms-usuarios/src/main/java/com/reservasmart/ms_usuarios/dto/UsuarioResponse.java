package com.reservasmart.ms_usuarios.dto;

import com.reservasmart.ms_usuarios.model.EstadoUsuario;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class UsuarioResponse {

    private Long id;
    private String nombre;
    private String email;
    private String telefono;
    private EstadoUsuario estado;
    private LocalDateTime fechaRegistro;
}