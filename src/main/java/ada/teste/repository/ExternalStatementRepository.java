package ada.teste.repository;

import ada.teste.entity.ExternalStatementEntry;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ExternalStatementRepository extends JpaRepository<ExternalStatementEntry, Long> {

    List<ExternalStatementEntry> findByInstitutionId(String institutionId);
}

