package it.italiancoders.mybudget.service.account;

import it.italiancoders.mybudget.model.api.*;

import javax.validation.Valid;
import java.util.Date;

public interface AccountManager {

    void createAccount(AccountCreationRequest request, User user);

    void insertMovement(Movement movement, User user);

    void updateMovement(@Valid Movement movement, User currentUser);

    void deleteMovement(String movementId);

    void generateAutoMovement(Date inDate);

    void insertAutoMovement(Movement movement, ScheduledMovementSettings scheduledMovementSettings, Date execDate);

    void leftAccount(Account myAccount,  String username);
}
