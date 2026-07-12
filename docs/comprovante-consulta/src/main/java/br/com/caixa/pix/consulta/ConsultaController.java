package br.com.caixa.pix.consulta;

import br.com.caixa.pix.contracts.ComprovanteView;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.UUID;

/** CONSULTA: GET por id com cache + fallback no gravador (Aula 3). */
@RestController
@RequestMapping("/comprovantes")
public class ConsultaController {

    private final ConsultaService service;

    public ConsultaController(ConsultaService service) {
        this.service = service;
    }

    @GetMapping("/{id}")
    public ResponseEntity<ComprovanteView> consultar(@PathVariable UUID id) {
        return Optional.ofNullable(service.buscar(id))
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
