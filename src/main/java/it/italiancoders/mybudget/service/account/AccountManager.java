package it.italiancoders.mybudget.service.account;

import it.italiancoders.mybudget.model.api.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Date;

public interface AccountManager {

    void createAccount(AccountCreationRequest request, User user);

    void insertMovement(Movement movement, User user);

    void updateMovement(@Valid Movement movement, User currentUser);

    void deleteMovement(String movementId);

    void generateAutoMovement(Date inDate);

    void insertAutoMovement(Movement movement, AutoMovementSettings autoMovementSettings, Date execDate);

    void leftAccount(Account myAccount,  String username);
}
