package ada.teste;

import ada.teste.repository.ExternalStatementRepository;
import ada.teste.repository.FinbrasStatementRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
class MainApplicationTest {

    @Autowired
    private ExternalStatementRepository externalStatementRepository;

    @Autowired
    private FinbrasStatementRepository finbrasStatementRepository;

    @Test
    void contextLoadsAndSeedsDemoData() {
        assertThat(externalStatementRepository.count()).isEqualTo(6);
        assertThat(finbrasStatementRepository.count()).isEqualTo(2);
    }
}

