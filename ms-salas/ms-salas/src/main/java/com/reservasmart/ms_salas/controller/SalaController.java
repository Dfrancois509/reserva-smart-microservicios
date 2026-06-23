package com.reservasmart.ms_salas.controller;

import com.reservasmart.ms_salas.dto.ActualizarEstadoSalaRequest;
import com.reservasmart.ms_salas.dto.ActualizarSalaRequest;
import com.reservasmart.ms_salas.dto.CrearSalaRequest;
import com.reservasmart.ms_salas.dto.SalaResponse;
import com.reservasmart.ms_salas.service.SalaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/salas")
@RequiredArgsConstructor
@Tag(name = "Salas", description = "Microservicio para gestión de salas")
public class SalaController {

    private final SalaService salaService;

    @PostMapping
    @Operation(summary = "Crear una sala")
    public SalaResponse crear(
            @Valid @RequestBody CrearSalaRequest request) {

        return salaService.crear(request);
    }

    @GetMapping
    @Operation(summary = "Listar todas las salas")
    public List<SalaResponse> listarTodas() {

        return salaService.listarTodas();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar sala por ID")
    public SalaResponse buscarPorId(
            @PathVariable Long id) {

        return salaService.buscarPorId(id);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar sala")
    public SalaResponse actualizar(
            @PathVariable Long id,
            @Valid @RequestBody ActualizarSalaRequest request) {

        return salaService.actualizar(id, request);
    }

    @PutMapping("/{id}/estado")
    @Operation(summary = "Cambiar disponibilidad de una sala")
    public SalaResponse cambiarEstado(
            @PathVariable Long id,
            @Valid @RequestBody ActualizarEstadoSalaRequest request) {

        return salaService.cambiarEstado(id, request);
    }

    @GetMapping("/{id}/disponible")
    @Operation(summary = "Verificar si una sala está disponible")
    public boolean salaDisponible(
            @PathVariable Long id) {

        return salaService.salaDisponible(id);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar sala")
    public String eliminar(
            @PathVariable Long id) {

        return salaService.eliminar(id);
    }
}