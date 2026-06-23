package com.reservasmart.ms_reservas.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "reservas")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Reserva {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long usuarioId;

    private Long salaId;

    private LocalDate fecha;

    private LocalTime horaInicio;

    private LocalTime horaFin;

    @Enumerated(EnumType.STRING)
    private EstadoReserva estado;

    private LocalDateTime fechaCreacion;
}