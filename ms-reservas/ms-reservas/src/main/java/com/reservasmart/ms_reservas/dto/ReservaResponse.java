package com.reservasmart.ms_reservas.dto;

import com.reservasmart.ms_reservas.model.EstadoReserva;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@Builder
public class ReservaResponse {

    private Long id;
    private Long usuarioId;
    private Long salaId;
    private LocalDate fecha;
    private LocalTime horaInicio;
    private LocalTime horaFin;
    private EstadoReserva estado;
    private LocalDateTime fechaCreacion;
}