package com.reservasmart.ms_reservas.service;

import com.reservasmart.ms_reservas.client.SalaClient;
import com.reservasmart.ms_reservas.client.UsuarioClient;
import com.reservasmart.ms_reservas.dto.CrearReservaRequest;
import com.reservasmart.ms_reservas.dto.ReservaResponse;
import com.reservasmart.ms_reservas.model.EstadoReserva;
import com.reservasmart.ms_reservas.model.Reserva;
import com.reservasmart.ms_reservas.repository.ReservaRepository;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReservaService {

    private final ReservaRepository reservaRepository;
    private final UsuarioClient usuarioClient;
    private final SalaClient salaClient;

    public ReservaResponse crear(CrearReservaRequest request) {

        validarHorario(request);

        if (!usuarioActivo(request.getUsuarioId())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El usuario no existe o no está activo"
            );
        }

        if (!salaDisponible(request.getSalaId())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "La sala no existe o no está disponible"
            );
        }

        boolean existeChoque = reservaRepository
                .existsBySalaIdAndFechaAndEstadoAndHoraInicioLessThanAndHoraFinGreaterThan(
                        request.getSalaId(),
                        request.getFecha(),
                        EstadoReserva.ACTIVA,
                        request.getHoraFin(),
                        request.getHoraInicio()
                );

        if (existeChoque) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "La sala ya tiene una reserva activa en ese horario"
            );
        }

        Reserva reserva = Reserva.builder()
                .usuarioId(request.getUsuarioId())
                .salaId(request.getSalaId())
                .fecha(request.getFecha())
                .horaInicio(request.getHoraInicio())
                .horaFin(request.getHoraFin())
                .estado(EstadoReserva.ACTIVA)
                .fechaCreacion(LocalDateTime.now())
                .build();

        return convertirAResponse(
                reservaRepository.save(reserva)
        );
    }

    public List<ReservaResponse> listarTodas() {

        return reservaRepository.findAll()
                .stream()
                .map(this::convertirAResponse)
                .toList();
    }

    public ReservaResponse buscarPorId(Long id) {

        Reserva reserva = obtenerEntidadPorId(id);

        return convertirAResponse(reserva);
    }

    public List<ReservaResponse> listarPorUsuario(Long usuarioId) {

        return reservaRepository.findByUsuarioId(usuarioId)
                .stream()
                .map(this::convertirAResponse)
                .toList();
    }

    public ReservaResponse cancelar(Long id) {

        Reserva reserva = obtenerEntidadPorId(id);

        if (reserva.getEstado() == EstadoReserva.CANCELADA) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "La reserva ya se encuentra cancelada"
            );
        }

        reserva.setEstado(EstadoReserva.CANCELADA);

        return convertirAResponse(
                reservaRepository.save(reserva)
        );
    }

    private void validarHorario(CrearReservaRequest request) {

        if (!request.getHoraFin().isAfter(request.getHoraInicio())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "La hora de fin debe ser posterior a la hora de inicio"
            );
        }
    }

    private boolean usuarioActivo(Long usuarioId) {

        try {
            return usuarioClient.existeUsuarioActivo(usuarioId);
        } catch (FeignException.NotFound e) {
            return false;
        } catch (FeignException e) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_GATEWAY,
                    "Error al comunicarse con ms-usuarios"
            );
        }
    }

    private boolean salaDisponible(Long salaId) {

        try {
            return salaClient.salaDisponible(salaId);
        } catch (FeignException.NotFound e) {
            return false;
        } catch (FeignException e) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_GATEWAY,
                    "Error al comunicarse con ms-salas"
            );
        }
    }

    private Reserva obtenerEntidadPorId(Long id) {

        return reservaRepository.findById(id)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Reserva no encontrada"
                        )
                );
    }

    private ReservaResponse convertirAResponse(Reserva reserva) {

        return ReservaResponse.builder()
                .id(reserva.getId())
                .usuarioId(reserva.getUsuarioId())
                .salaId(reserva.getSalaId())
                .fecha(reserva.getFecha())
                .horaInicio(reserva.getHoraInicio())
                .horaFin(reserva.getHoraFin())
                .estado(reserva.getEstado())
                .fechaCreacion(reserva.getFechaCreacion())
                .build();
    }
}