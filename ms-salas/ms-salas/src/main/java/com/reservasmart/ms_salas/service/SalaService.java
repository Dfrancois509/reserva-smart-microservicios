package com.reservasmart.ms_salas.service;

import com.reservasmart.ms_salas.dto.ActualizarEstadoSalaRequest;
import com.reservasmart.ms_salas.dto.ActualizarSalaRequest;
import com.reservasmart.ms_salas.dto.CrearSalaRequest;
import com.reservasmart.ms_salas.dto.SalaResponse;
import com.reservasmart.ms_salas.model.Sala;
import com.reservasmart.ms_salas.repository.SalaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SalaService {

    private final SalaRepository salaRepository;

    public SalaResponse crear(CrearSalaRequest request) {

        if (salaRepository.existsByNombreIgnoreCase(request.getNombre())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Ya existe una sala con este nombre"
            );
        }

        Sala sala = Sala.builder()
                .nombre(request.getNombre())
                .capacidad(request.getCapacidad())
                .ubicacion(request.getUbicacion())
                .disponible(true)
                .fechaCreacion(LocalDateTime.now())
                .build();

        return convertirAResponse(
                salaRepository.save(sala)
        );
    }

    public List<SalaResponse> listarTodas() {

        return salaRepository.findAll()
                .stream()
                .map(this::convertirAResponse)
                .toList();
    }

    public SalaResponse buscarPorId(Long id) {

        Sala sala = obtenerEntidadPorId(id);

        return convertirAResponse(sala);
    }

    public SalaResponse actualizar(
            Long id,
            ActualizarSalaRequest request) {

        Sala sala = obtenerEntidadPorId(id);

        sala.setNombre(request.getNombre());
        sala.setCapacidad(request.getCapacidad());
        sala.setUbicacion(request.getUbicacion());

        return convertirAResponse(
                salaRepository.save(sala)
        );
    }

    public SalaResponse cambiarEstado(
            Long id,
            ActualizarEstadoSalaRequest request) {

        Sala sala = obtenerEntidadPorId(id);

        sala.setDisponible(request.getDisponible());

        return convertirAResponse(
                salaRepository.save(sala)
        );
    }

    public boolean salaDisponible(Long id) {

        Sala sala = obtenerEntidadPorId(id);

        return Boolean.TRUE.equals(sala.getDisponible());
    }

    public String eliminar(Long id) {

        Sala sala = obtenerEntidadPorId(id);

        salaRepository.delete(sala);

        return "Sala eliminada correctamente";
    }

    private Sala obtenerEntidadPorId(Long id) {

        return salaRepository.findById(id)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Sala no encontrada"
                        )
                );
    }

    private SalaResponse convertirAResponse(Sala sala) {

        return SalaResponse.builder()
                .id(sala.getId())
                .nombre(sala.getNombre())
                .capacidad(sala.getCapacidad())
                .ubicacion(sala.getUbicacion())
                .disponible(sala.getDisponible())
                .fechaCreacion(sala.getFechaCreacion())
                .build();
    }
}