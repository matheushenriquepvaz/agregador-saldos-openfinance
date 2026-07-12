package br.com.caixa.pix.consulta;

import br.com.caixa.pix.contracts.ComprovanteView;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

/**
 * Aula 3 — consulta com cache + fallback.
 * Fluxo: cache → (miss) gravador com até 3 retentativas → popula cache → senão 404.
 * O @Cacheable cobre o "buscar primeiro no cache e popular no retorno";
 * unless evita cachear ausência (para o 404 poder ser reconsultado depois).
 */
@Service
public class ConsultaService {

    private static final int MAX_TENTATIVAS = 3;

    private final GravadorReadClient gravador;

    public ConsultaService(GravadorReadClient gravador) {
        this.gravador = gravador;
    }

    @Cacheable(value = "comprovantes", key = "#id", unless = "#result == null")
    public ComprovanteView buscar(UUID id) {
        for (int tentativa = 1; tentativa <= MAX_TENTATIVAS; tentativa++) {
            Optional<ComprovanteView> achado = gravador.buscar(id);
            if (achado.isPresent()) {
                return achado.get();
            }
        }
        return null; // 3 retentativas sem achar → o controller devolve 404
    }
}
