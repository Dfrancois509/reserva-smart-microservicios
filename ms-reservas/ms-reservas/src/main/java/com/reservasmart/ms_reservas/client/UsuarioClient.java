package com.reservasmart.ms_reservas.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "ms-usuarios")
public interface UsuarioClient {

    @GetMapping("/usuarios/{id}/activo")
    boolean existeUsuarioActivo(@PathVariable Long id);
}