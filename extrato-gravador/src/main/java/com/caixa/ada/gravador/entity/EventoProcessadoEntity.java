package com.caixa.ada.gravador.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "eventos_processados")
public class EventoProcessadoEntity {

    @Id
    private UUID eventoId;

    private LocalDateTime dataProcessamento;

    protected EventoProcessadoEntity() {
    }

    public EventoProcessadoEntity(UUID eventoId, LocalDateTime dataProcessamento) {
        this.eventoId = eventoId;
        this.dataProcessamento = dataProcessamento;
    }

    public UUID getEventoId() {
        return eventoId;
    }

    public LocalDateTime getDataProcessamento() {
        return dataProcessamento;
    }
}

