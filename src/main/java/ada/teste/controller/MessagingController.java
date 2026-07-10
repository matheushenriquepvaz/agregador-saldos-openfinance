package ada.teste.controller;

import ada.teste.dto.MockMessageRequest;
import ada.teste.dto.MockRequestResponse;
import ada.teste.service.MockStatementMessagingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/mensageria")
@Tag(name = "Mensageria mockada", description = "Simulação do fluxo produtor/consumidor de extratos")
public class MessagingController {

    private final MockStatementMessagingService messagingService;

    public MessagingController(MockStatementMessagingService messagingService) {
        this.messagingService = messagingService;
    }

    @PostMapping("/pedir-extrato")
    @Operation(summary = "Simula o produtor pedindo o extrato atualizado")
    public MockRequestResponse requestUpdatedStatements(@RequestBody(required = false) MockMessageRequest request) {
        return messagingService.requestUpdatedStatements(request == null ? new MockMessageRequest(null) : request);
    }
}

