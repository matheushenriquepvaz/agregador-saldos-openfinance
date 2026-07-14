package com.caixa.ada.gravador.repository;

import com.caixa.ada.gravador.entity.TransacaoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface TransacaoRepository extends JpaRepository<TransacaoEntity, UUID> {

    List<TransacaoEntity> findTop10ByClienteIdOrderByDataProcessamentoDesc(String clienteId);
}

