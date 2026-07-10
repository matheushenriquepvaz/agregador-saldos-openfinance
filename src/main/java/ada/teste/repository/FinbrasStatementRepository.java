package ada.teste.repository;

import ada.teste.entity.FinbrasStatementEntry;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FinbrasStatementRepository extends JpaRepository<FinbrasStatementEntry, Long> {
}

