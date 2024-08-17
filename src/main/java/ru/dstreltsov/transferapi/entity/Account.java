package ru.dstreltsov.transferapi.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.builder.ToStringBuilder;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import java.math.BigDecimal;

@Entity
@Table(name = "account")
@Getter
@Setter
@NoArgsConstructor
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
    @Column(nullable = false, precision = 18, scale = 2)
    private BigDecimal balance;
    @Column(nullable = false, precision = 18, scale = 2)
    private BigDecimal initialBalance;

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("id", id)
                .append("balance", balance)
                .append("initialBalance", initialBalance)
                .toString();
    }
}
