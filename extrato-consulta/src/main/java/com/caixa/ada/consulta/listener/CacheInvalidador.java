package com.caixa.ada.consulta.listener;

import com.caixa.ada.consulta.configuration.KafkaConfig;
import com.caixa.ada.contracts.ExtratoAtualizadoEvent;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class CacheInvalidador {

    private final CacheManager cacheManager;

    public CacheInvalidador(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    @KafkaListener(topics = KafkaConfig.TOPICO_EXTRATO_ATUALIZADO, groupId = KafkaConfig.GRUPO_CONSUMIDOR)
    public void aoReceber(ExtratoAtualizadoEvent event) {
        Cache cache = cacheManager.getCache("extratos");
        if (cache != null) {
            cache.evict(event.clienteId());
        }
    }
}

