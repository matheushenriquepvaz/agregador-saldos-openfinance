package com.caixa.ada.gravador.repository;

import com.caixa.ada.gravador.entity.SaldoClienteEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SaldoClienteRepository extends JpaRepository<SaldoClienteEntity, String> {
}

