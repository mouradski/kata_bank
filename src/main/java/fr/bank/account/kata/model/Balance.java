package fr.bank.account.kata.model;

import lombok.*;

import java.time.OffsetDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Balance {
    private OffsetDateTime date;
    private Double amount;
}
