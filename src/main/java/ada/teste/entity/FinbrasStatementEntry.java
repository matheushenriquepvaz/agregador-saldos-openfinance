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
@Table(name = "finbras_statement_entries")
public class FinbrasStatementEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal currentBalance;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal movementValue;

    @Column(nullable = false)
    private LocalDateTime movementDate;

    protected FinbrasStatementEntry() {
    }

    public FinbrasStatementEntry(BigDecimal currentBalance, BigDecimal movementValue, LocalDateTime movementDate) {
        this.currentBalance = currentBalance;
        this.movementValue = movementValue;
        this.movementDate = movementDate;
    }

    public Long getId() {
        return id;
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

