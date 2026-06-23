package com.reservasmart.ms_salas.repository;

import com.reservasmart.ms_salas.model.Sala;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SalaRepository extends JpaRepository<Sala, Long> {

    boolean existsByNombreIgnoreCase(String nombre);
}