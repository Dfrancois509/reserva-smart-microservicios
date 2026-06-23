package com.reservasmart.ms_salas.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class SalaResponse {

    private Long id;
    private String nombre;
    private Integer capacidad;
    private String ubicacion;
    private Boolean disponible;
    private LocalDateTime fechaCreacion;
}