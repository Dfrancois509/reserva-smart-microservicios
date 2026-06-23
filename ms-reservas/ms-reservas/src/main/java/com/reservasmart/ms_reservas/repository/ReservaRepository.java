package com.reservasmart.ms_reservas.repository;

import com.reservasmart.ms_reservas.model.EstadoReserva;
import com.reservasmart.ms_reservas.model.Reserva;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface ReservaRepository extends JpaRepository<Reserva, Long> {

    List<Reserva> findByUsuarioId(Long usuarioId);

    boolean existsBySalaIdAndFechaAndEstadoAndHoraInicioLessThanAndHoraFinGreaterThan(
            Long salaId,
            LocalDate fecha,
            EstadoReserva estado,
            LocalTime horaFin,
            LocalTime horaInicio
    );
}