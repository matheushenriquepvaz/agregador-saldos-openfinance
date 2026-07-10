package ada.teste.controller;

import ada.teste.dto.SeedDataResponse;
import ada.teste.service.DemoDataService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dev")
@Tag(name = "Dados fictícios", description = "Endpoints auxiliares para popular o banco com dados de teste")
public class DevController {

    private final DemoDataService demoDataService;

    public DevController(DemoDataService demoDataService) {
        this.demoDataService = demoDataService;
    }

    @PostMapping("/popular-dados")
    @Operation(summary = "Popula o banco com dados fictícios de 3 instituições externas e Finbras")
    public SeedDataResponse seedDemoData() {
        return demoDataService.seedDemoData();
    }
}

