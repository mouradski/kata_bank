package fr.bank.account.kata.model;

import lombok.*;

import java.time.OffsetDateTime;
import java.util.Objects;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class Operation {
    private Integer id;
    private OperationType operationType;
    private Double amount;
    @EqualsAndHashCode.Exclude
    private OffsetDateTime date;
    private Double balance;
    private Account account;
}
