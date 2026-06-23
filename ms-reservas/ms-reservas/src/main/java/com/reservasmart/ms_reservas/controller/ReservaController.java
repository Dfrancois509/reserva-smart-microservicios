package com.reservasmart.ms_reservas.controller;

import com.reservasmart.ms_reservas.dto.CrearReservaRequest;
import com.reservasmart.ms_reservas.dto.ReservaResponse;
import com.reservasmart.ms_reservas.service.ReservaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/reservas")
@RequiredArgsConstructor
@Tag(name = "Reservas", description = "Microservicio para gestión de reservas")
public class ReservaController {

    private final ReservaService reservaService;

    @PostMapping
    @Operation(summary = "Crear una reserva")
    public ReservaResponse crear(
            @Valid @RequestBody CrearReservaRequest request) {

        return reservaService.crear(request);
    }

    @GetMapping
    @Operation(summary = "Listar todas las reservas")
    public List<ReservaResponse> listarTodas() {

        return reservaService.listarTodas();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar reserva por ID")
    public ReservaResponse buscarPorId(
            @PathVariable Long id) {

        return reservaService.buscarPorId(id);
    }

    @GetMapping("/usuario/{usuarioId}")
    @Operation(summary = "Listar reservas por usuario")
    public List<ReservaResponse> listarPorUsuario(
            @PathVariable Long usuarioId) {

        return reservaService.listarPorUsuario(usuarioId);
    }

    @PutMapping("/{id}/cancelar")
    @Operation(summary = "Cancelar una reserva")
    public ReservaResponse cancelar(
            @PathVariable Long id) {

        return reservaService.cancelar(id);
    }
}