package com.reservasmart.ms_salas.service;

import com.reservasmart.ms_salas.dto.ActualizarEstadoSalaRequest;
import com.reservasmart.ms_salas.dto.CrearSalaRequest;
import com.reservasmart.ms_salas.dto.SalaResponse;
import com.reservasmart.ms_salas.model.Sala;
import com.reservasmart.ms_salas.repository.SalaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SalaServiceTest {

    @Mock
    private SalaRepository salaRepository;

    @InjectMocks
    private SalaService salaService;

    @Test
    void crearSala_DeberiaCrearSalaCorrectamente() {

        CrearSalaRequest request = new CrearSalaRequest();
        request.setNombre("Sala Reuniones A");
        request.setCapacidad(10);
        request.setUbicacion("Piso 1");

        Sala salaGuardada = Sala.builder()
                .id(1L)
                .nombre(request.getNombre())
                .capacidad(request.getCapacidad())
                .ubicacion(request.getUbicacion())
                .disponible(true)
                .fechaCreacion(LocalDateTime.now())
                .build();

        when(salaRepository.existsByNombreIgnoreCase(request.getNombre()))
                .thenReturn(false);

        when(salaRepository.save(any(Sala.class)))
                .thenReturn(salaGuardada);

        SalaResponse response = salaService.crear(request);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("Sala Reuniones A", response.getNombre());
        assertEquals(10, response.getCapacidad());
        assertEquals("Piso 1", response.getUbicacion());
        assertTrue(response.getDisponible());

        verify(salaRepository).existsByNombreIgnoreCase(request.getNombre());
        verify(salaRepository).save(any(Sala.class));
    }

    @Test
    void crearSala_DeberiaLanzarErrorCuandoNombreExiste() {

        CrearSalaRequest request = new CrearSalaRequest();
        request.setNombre("Sala Reuniones A");
        request.setCapacidad(10);
        request.setUbicacion("Piso 1");

        when(salaRepository.existsByNombreIgnoreCase(request.getNombre()))
                .thenReturn(true);

        assertThrows(
                ResponseStatusException.class,
                () -> salaService.crear(request)
        );

        verify(salaRepository).existsByNombreIgnoreCase(request.getNombre());
        verify(salaRepository, never()).save(any(Sala.class));
    }

    @Test
    void buscarPorId_DeberiaRetornarSalaCuandoExiste() {

        Sala sala = Sala.builder()
                .id(1L)
                .nombre("Sala Reuniones A")
                .capacidad(10)
                .ubicacion("Piso 1")
                .disponible(true)
                .fechaCreacion(LocalDateTime.now())
                .build();

        when(salaRepository.findById(1L))
                .thenReturn(Optional.of(sala));

        SalaResponse response = salaService.buscarPorId(1L);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("Sala Reuniones A", response.getNombre());
        assertTrue(response.getDisponible());

        verify(salaRepository).findById(1L);
    }

    @Test
    void cambiarEstado_DeberiaCambiarDisponibilidadDeSala() {

        Sala sala = Sala.builder()
                .id(1L)
                .nombre("Sala Reuniones A")
                .capacidad(10)
                .ubicacion("Piso 1")
                .disponible(true)
                .fechaCreacion(LocalDateTime.now())
                .build();

        ActualizarEstadoSalaRequest request = new ActualizarEstadoSalaRequest();
        request.setDisponible(false);

        Sala salaActualizada = Sala.builder()
                .id(1L)
                .nombre("Sala Reuniones A")
                .capacidad(10)
                .ubicacion("Piso 1")
                .disponible(false)
                .fechaCreacion(sala.getFechaCreacion())
                .build();

        when(salaRepository.findById(1L))
                .thenReturn(Optional.of(sala));

        when(salaRepository.save(any(Sala.class)))
                .thenReturn(salaActualizada);

        SalaResponse response = salaService.cambiarEstado(1L, request);

        assertNotNull(response);
        assertFalse(response.getDisponible());

        verify(salaRepository).findById(1L);
        verify(salaRepository).save(any(Sala.class));
    }

    @Test
    void salaDisponible_DeberiaRetornarTrueCuandoSalaEstaDisponible() {

        Sala sala = Sala.builder()
                .id(1L)
                .nombre("Sala Reuniones A")
                .capacidad(10)
                .ubicacion("Piso 1")
                .disponible(true)
                .fechaCreacion(LocalDateTime.now())
                .build();

        when(salaRepository.findById(1L))
                .thenReturn(Optional.of(sala));

        boolean resultado = salaService.salaDisponible(1L);

        assertTrue(resultado);

        verify(salaRepository).findById(1L);
    }
}