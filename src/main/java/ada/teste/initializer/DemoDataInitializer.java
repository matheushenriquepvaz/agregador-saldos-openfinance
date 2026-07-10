package ada.teste.initializer;

import ada.teste.entity.ExternalStatementEntry;
import ada.teste.entity.FinbrasStatementEntry;
import ada.teste.repository.ExternalStatementRepository;
import ada.teste.repository.FinbrasStatementRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Component
public class DemoDataInitializer implements CommandLineRunner {

    private final ExternalStatementRepository externalStatementRepository;
    private final FinbrasStatementRepository finbrasStatementRepository;

    public DemoDataInitializer(ExternalStatementRepository externalStatementRepository,
                               FinbrasStatementRepository finbrasStatementRepository) {
        this.externalStatementRepository = externalStatementRepository;
        this.finbrasStatementRepository = finbrasStatementRepository;
    }

    @Override
    public void run(String... args) {
        if (externalStatementRepository.count() > 0 || finbrasStatementRepository.count() > 0) {
            return;
        }

        externalStatementRepository.saveAll(List.of(
                new ExternalStatementEntry("BANCO_ALPHA", "Banco Alpha", "12.345.678/0001-90", new BigDecimal("1540.32"), new BigDecimal("-120.50"), LocalDateTime.now().minusMinutes(90)),
                new ExternalStatementEntry("BANCO_ALPHA", "Banco Alpha", "12.345.678/0001-90", new BigDecimal("1660.82"), new BigDecimal("220.00"), LocalDateTime.now().minusMinutes(40)),
                new ExternalStatementEntry("BANCO_BETA", "Banco Beta", "98.765.432/0001-10", new BigDecimal("2210.11"), new BigDecimal("-75.40"), LocalDateTime.now().minusMinutes(70)),
                new ExternalStatementEntry("BANCO_BETA", "Banco Beta", "98.765.432/0001-10", new BigDecimal("2285.51"), new BigDecimal("155.00"), LocalDateTime.now().minusMinutes(25)),
                new ExternalStatementEntry("BANCO_GAMMA", "Banco Gamma", "11.222.333/0001-44", new BigDecimal("540.00"), new BigDecimal("-10.00"), LocalDateTime.now().minusMinutes(60)),
                new ExternalStatementEntry("BANCO_GAMMA", "Banco Gamma", "11.222.333/0001-44", new BigDecimal("550.00"), new BigDecimal("45.00"), LocalDateTime.now().minusMinutes(10))
        ));

        finbrasStatementRepository.saveAll(List.of(
                new FinbrasStatementEntry(new BigDecimal("10120.15"), new BigDecimal("-250.00"), LocalDateTime.now().minusMinutes(55)),
                new FinbrasStatementEntry(new BigDecimal("10370.15"), new BigDecimal("500.00"), LocalDateTime.now().minusMinutes(15))
        ));
    }
}

