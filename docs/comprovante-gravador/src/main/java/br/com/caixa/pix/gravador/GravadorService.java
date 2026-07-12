package br.com.caixa.pix.gravador;

import br.com.caixa.pix.contracts.ComprovanteGravadoEvent;
import br.com.caixa.pix.contracts.ComprovanteView;
import br.com.caixa.pix.contracts.GravarComprovanteCommand;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

/** GRAVAÇÃO: persiste (idempotente), expõe leitura e publica o evento de gravação. */
@Service
public class GravadorService {

    private final ComprovanteRepository repository;
    private final EventoPublisher eventos;

    public GravadorService(ComprovanteRepository repository, EventoPublisher eventos) {
        this.repository = repository;
        this.eventos = eventos;
    }

    public boolean gravar(GravarComprovanteCommand command) {
        if (repository.existsById(command.identificadorComprovante())) {
            return false;
        }
        repository.save(ComprovanteEntity.de(command.identificadorComprovante(), command.dados()));
        eventos.publicar(new ComprovanteGravadoEvent(command.identificadorComprovante(), LocalDateTime.now()));
        return true;
    }

    public Optional<ComprovanteView> buscar(UUID id) {
        return repository.findById(id).map(this::toView);
    }

    private ComprovanteView toView(ComprovanteEntity e) {
        return new ComprovanteView(e.getId(), e.getNome(), e.getNumeroDocumento(),
                e.getValorTransacao(), e.getChavePixDestino(),
                e.getDataHoraTransacao(), e.getDataHoraGravacao());
    }
}
