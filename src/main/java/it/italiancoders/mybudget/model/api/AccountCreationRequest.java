package it.italiancoders.mybudget.model.api;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AccountCreationRequest extends Account {

    private Double initialBalance = 0.0D;

}
