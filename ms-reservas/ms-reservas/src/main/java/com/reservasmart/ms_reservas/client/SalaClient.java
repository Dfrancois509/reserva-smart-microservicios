package com.reservasmart.ms_reservas.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "ms-salas")
public interface SalaClient {

    @GetMapping("/salas/{id}/disponible")
    boolean salaDisponible(@PathVariable Long id);
}