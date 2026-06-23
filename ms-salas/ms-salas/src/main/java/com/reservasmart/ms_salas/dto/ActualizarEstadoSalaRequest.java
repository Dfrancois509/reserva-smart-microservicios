package com.reservasmart.ms_salas.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ActualizarEstadoSalaRequest {

    @NotNull(message = "El estado disponible es obligatorio")
    private Boolean disponible;
}