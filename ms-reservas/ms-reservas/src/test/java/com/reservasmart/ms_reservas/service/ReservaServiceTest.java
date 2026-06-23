package com.reservasmart.ms_reservas.service;

import com.reservasmart.ms_reservas.client.SalaClient;
import com.reservasmart.ms_reservas.client.UsuarioClient;
import com.reservasmart.ms_reservas.dto.CrearReservaRequest;
import com.reservasmart.ms_reservas.dto.ReservaResponse;
import com.reservasmart.ms_reservas.model.EstadoReserva;
import com.reservasmart.ms_reservas.model.Reserva;
import com.reservasmart.ms_reservas.repository.ReservaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReservaServiceTest {

    @Mock
    private ReservaRepository reservaRepository;

    @Mock
    private UsuarioClient usuarioClient;

    @Mock
    private SalaClient salaClient;

    @InjectMocks
    private ReservaService reservaService;

    @Test
    void crearReserva_DeberiaCrearReservaCorrectamente() {

        CrearReservaRequest request = new CrearReservaRequest();
        request.setUsuarioId(1L);
        request.setSalaId(1L);
        request.setFecha(LocalDate.now().plusDays(1));
        request.setHoraInicio(LocalTime.of(10, 0));
        request.setHoraFin(LocalTime.of(11, 0));

        Reserva reservaGuardada = Reserva.builder()
                .id(1L)
                .usuarioId(request.getUsuarioId())
                .salaId(request.getSalaId())
                .fecha(request.getFecha())
                .horaInicio(request.getHoraInicio())
                .horaFin(request.getHoraFin())
                .estado(EstadoReserva.ACTIVA)
                .fechaCreacion(LocalDateTime.now())
                .build();

        when(usuarioClient.existeUsuarioActivo(1L))
                .thenReturn(true);

        when(salaClient.salaDisponible(1L))
                .thenReturn(true);

        when(reservaRepository
                .existsBySalaIdAndFechaAndEstadoAndHoraInicioLessThanAndHoraFinGreaterThan(
                        request.getSalaId(),
                        request.getFecha(),
                        EstadoReserva.ACTIVA,
                        request.getHoraFin(),
                        request.getHoraInicio()
                ))
                .thenReturn(false);

        when(reservaRepository.save(any(Reserva.class)))
                .thenReturn(reservaGuardada);

        ReservaResponse response = reservaService.crear(request);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals(1L, response.getUsuarioId());
        assertEquals(1L, response.getSalaId());
        assertEquals(EstadoReserva.ACTIVA, response.getEstado());

        verify(usuarioClient).existeUsuarioActivo(1L);
        verify(salaClient).salaDisponible(1L);
        verify(reservaRepository).save(any(Reserva.class));
    }

    @Test
    void crearReserva_DeberiaLanzarErrorCuandoUsuarioNoEstaActivo() {

        CrearReservaRequest request = new CrearReservaRequest();
        request.setUsuarioId(99L);
        request.setSalaId(1L);
        request.setFecha(LocalDate.now().plusDays(1));
        request.setHoraInicio(LocalTime.of(10, 0));
        request.setHoraFin(LocalTime.of(11, 0));

        when(usuarioClient.existeUsuarioActivo(99L))
                .thenReturn(false);

        assertThrows(
                ResponseStatusException.class,
                () -> reservaService.crear(request)
        );

        verify(usuarioClient).existeUsuarioActivo(99L);
        verify(salaClient, never()).salaDisponible(anyLong());
        verify(reservaRepository, never()).save(any(Reserva.class));
    }

    @Test
    void crearReserva_DeberiaLanzarErrorCuandoSalaNoEstaDisponible() {

        CrearReservaRequest request = new CrearReservaRequest();
        request.setUsuarioId(1L);
        request.setSalaId(99L);
        request.setFecha(LocalDate.now().plusDays(1));
        request.setHoraInicio(LocalTime.of(10, 0));
        request.setHoraFin(LocalTime.of(11, 0));

        when(usuarioClient.existeUsuarioActivo(1L))
                .thenReturn(true);

        when(salaClient.salaDisponible(99L))
                .thenReturn(false);

        assertThrows(
                ResponseStatusException.class,
                () -> reservaService.crear(request)
        );

        verify(usuarioClient).existeUsuarioActivo(1L);
        verify(salaClient).salaDisponible(99L);
        verify(reservaRepository, never()).save(any(Reserva.class));
    }

    @Test
    void crearReserva_DeberiaLanzarErrorCuandoExisteChoqueDeHorario() {

        CrearReservaRequest request = new CrearReservaRequest();
        request.setUsuarioId(1L);
        request.setSalaId(1L);
        request.setFecha(LocalDate.now().plusDays(1));
        request.setHoraInicio(LocalTime.of(10, 30));
        request.setHoraFin(LocalTime.of(11, 30));

        when(usuarioClient.existeUsuarioActivo(1L))
                .thenReturn(true);

        when(salaClient.salaDisponible(1L))
                .thenReturn(true);

        when(reservaRepository
                .existsBySalaIdAndFechaAndEstadoAndHoraInicioLessThanAndHoraFinGreaterThan(
                        request.getSalaId(),
                        request.getFecha(),
                        EstadoReserva.ACTIVA,
                        request.getHoraFin(),
                        request.getHoraInicio()
                ))
                .thenReturn(true);

        assertThrows(
                ResponseStatusException.class,
                () -> reservaService.crear(request)
        );

        verify(usuarioClient).existeUsuarioActivo(1L);
        verify(salaClient).salaDisponible(1L);
        verify(reservaRepository, never()).save(any(Reserva.class));
    }

    @Test
    void crearReserva_DeberiaLanzarErrorCuandoHoraFinNoEsPosteriorAHoraInicio() {

        CrearReservaRequest request = new CrearReservaRequest();
        request.setUsuarioId(1L);
        request.setSalaId(1L);
        request.setFecha(LocalDate.now().plusDays(1));
        request.setHoraInicio(LocalTime.of(11, 0));
        request.setHoraFin(LocalTime.of(10, 0));

        assertThrows(
                ResponseStatusException.class,
                () -> reservaService.crear(request)
        );

        verify(usuarioClient, never()).existeUsuarioActivo(anyLong());
        verify(salaClient, never()).salaDisponible(anyLong());
        verify(reservaRepository, never()).save(any(Reserva.class));
    }

    @Test
    void cancelarReserva_DeberiaCambiarEstadoACancelada() {

        Reserva reserva = Reserva.builder()
                .id(1L)
                .usuarioId(1L)
                .salaId(1L)
                .fecha(LocalDate.now().plusDays(1))
                .horaInicio(LocalTime.of(10, 0))
                .horaFin(LocalTime.of(11, 0))
                .estado(EstadoReserva.ACTIVA)
                .fechaCreacion(LocalDateTime.now())
                .build();

        Reserva reservaCancelada = Reserva.builder()
                .id(1L)
                .usuarioId(1L)
                .salaId(1L)
                .fecha(reserva.getFecha())
                .horaInicio(reserva.getHoraInicio())
                .horaFin(reserva.getHoraFin())
                .estado(EstadoReserva.CANCELADA)
                .fechaCreacion(reserva.getFechaCreacion())
                .build();

        when(reservaRepository.findById(1L))
                .thenReturn(Optional.of(reserva));

        when(reservaRepository.save(any(Reserva.class)))
                .thenReturn(reservaCancelada);

        ReservaResponse response = reservaService.cancelar(1L);

        assertNotNull(response);
        assertEquals(EstadoReserva.CANCELADA, response.getEstado());

        verify(reservaRepository).findById(1L);
        verify(reservaRepository).save(any(Reserva.class));
    }
}