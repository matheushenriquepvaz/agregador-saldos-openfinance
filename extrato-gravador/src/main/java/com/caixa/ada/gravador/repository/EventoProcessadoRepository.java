package com.caixa.ada.gravador.repository;

import com.caixa.ada.gravador.entity.EventoProcessadoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface EventoProcessadoRepository extends JpaRepository<EventoProcessadoEntity, UUID> {
}

