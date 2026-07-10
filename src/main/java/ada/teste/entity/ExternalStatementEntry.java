package ada.teste.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "external_statement_entries")
public class ExternalStatementEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String institutionId;

    @Column(nullable = false)
    private String sourceBankName;

    @Column(nullable = false)
    private String sourceBankCnpj;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal currentBalance;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal movementValue;

    @Column(nullable = false)
    private LocalDateTime movementDate;

    protected ExternalStatementEntry() {
    }

    public ExternalStatementEntry(String institutionId,
                                  String sourceBankName,
                                  String sourceBankCnpj,
                                  BigDecimal currentBalance,
                                  BigDecimal movementValue,
                                  LocalDateTime movementDate) {
        this.institutionId = institutionId;
        this.sourceBankName = sourceBankName;
        this.sourceBankCnpj = sourceBankCnpj;
        this.currentBalance = currentBalance;
        this.movementValue = movementValue;
        this.movementDate = movementDate;
    }

    public Long getId() {
        return id;
    }

    public String getInstitutionId() {
        return institutionId;
    }

    public void setInstitutionId(String institutionId) {
        this.institutionId = institutionId;
    }

    public String getSourceBankName() {
        return sourceBankName;
    }

    public void setSourceBankName(String sourceBankName) {
        this.sourceBankName = sourceBankName;
    }

    public String getSourceBankCnpj() {
        return sourceBankCnpj;
    }

    public void setSourceBankCnpj(String sourceBankCnpj) {
        this.sourceBankCnpj = sourceBankCnpj;
    }

    public BigDecimal getCurrentBalance() {
        return currentBalance;
    }

    public void setCurrentBalance(BigDecimal currentBalance) {
        this.currentBalance = currentBalance;
    }

    public BigDecimal getMovementValue() {
        return movementValue;
    }

    public void setMovementValue(BigDecimal movementValue) {
        this.movementValue = movementValue;
    }

    public LocalDateTime getMovementDate() {
        return movementDate;
    }

    public void setMovementDate(LocalDateTime movementDate) {
        this.movementDate = movementDate;
    }
}

