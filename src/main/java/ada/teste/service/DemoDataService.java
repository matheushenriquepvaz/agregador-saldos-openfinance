package ada.teste.service;

import ada.teste.dto.SeedDataResponse;
import ada.teste.entity.ExternalStatementEntry;
import ada.teste.entity.FinbrasStatementEntry;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class DemoDataService {

    private final FinbrasStatementService finbrasStatementService;
    private final ExternalStatementService externalStatementService;

    public DemoDataService(FinbrasStatementService finbrasStatementService,
                           ExternalStatementService externalStatementService) {
        this.finbrasStatementService = finbrasStatementService;
        this.externalStatementService = externalStatementService;
    }

    @Transactional
    public SeedDataResponse seedDemoData() {
        externalStatementService.deleteAll();
        finbrasStatementService.deleteAll();

        List<String> institutions = List.of("BANCO_ALPHA", "BANCO_BETA", "BANCO_GAMMA");

        externalStatementService.saveReceivedStatements("BANCO_ALPHA", List.of(
                new ExternalStatementEntry("BANCO_ALPHA", "Banco Alpha", "12.345.678/0001-90", new BigDecimal("1540.32"), new BigDecimal("-120.50"), LocalDateTime.now().minusMinutes(90)),
                new ExternalStatementEntry("BANCO_ALPHA", "Banco Alpha", "12.345.678/0001-90", new BigDecimal("1660.82"), new BigDecimal("220.00"), LocalDateTime.now().minusMinutes(40))
        ));

        externalStatementService.saveReceivedStatements("BANCO_BETA", List.of(
                new ExternalStatementEntry("BANCO_BETA", "Banco Beta", "98.765.432/0001-10", new BigDecimal("2210.11"), new BigDecimal("-75.40"), LocalDateTime.now().minusMinutes(70)),
                new ExternalStatementEntry("BANCO_BETA", "Banco Beta", "98.765.432/0001-10", new BigDecimal("2285.51"), new BigDecimal("155.00"), LocalDateTime.now().minusMinutes(25))
        ));

        externalStatementService.saveReceivedStatements("BANCO_GAMMA", List.of(
                new ExternalStatementEntry("BANCO_GAMMA", "Banco Gamma", "11.222.333/0001-44", new BigDecimal("540.00"), new BigDecimal("-10.00"), LocalDateTime.now().minusMinutes(60)),
                new ExternalStatementEntry("BANCO_GAMMA", "Banco Gamma", "11.222.333/0001-44", new BigDecimal("550.00"), new BigDecimal("45.00"), LocalDateTime.now().minusMinutes(10))
        ));

        finbrasStatementService.saveAll(List.of(
                new FinbrasStatementEntry(new BigDecimal("10120.15"), new BigDecimal("-250.00"), LocalDateTime.now().minusMinutes(55)),
                new FinbrasStatementEntry(new BigDecimal("10370.15"), new BigDecimal("500.00"), LocalDateTime.now().minusMinutes(15))
        ));

        return new SeedDataResponse(
                "Dados fictícios gerados com sucesso.",
                institutions,
                finbrasStatementService.findAll().size(),
                externalStatementService.findAll().size()
        );
    }
}

