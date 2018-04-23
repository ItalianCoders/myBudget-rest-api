package it.italiancoders.mybudget.service.account.impl;

import it.italiancoders.mybudget.dao.account.AccountDao;
import it.italiancoders.mybudget.dao.movement.MovementDao;
import it.italiancoders.mybudget.model.api.*;
import it.italiancoders.mybudget.service.account.AccountManager;
import it.italiancoders.mybudget.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.Valid;
import java.util.*;

@Service
public class AccountManagerImpl implements AccountManager {

    @Value("${movement.defaultExpense.category}")
    private String defaultExpenseCategory;

    @Value("${movement.defaultIncoming.category}")
    private String defaultIncomingCategory;

    @Autowired
    MovementDao movementDao;

    @Autowired
    AccountDao accountDao;

    @Override
    public void createAccount(AccountCreationRequest request, User user) {
        Account account = (Account) request;
        Double initialQty = request.getInitialBalance();

        request.setId(UUID.randomUUID().toString());
        request.setStatus(AccountStatusEnum.Ok);

        accountDao.insertAccount(request,user.getUsername(),true,true);

        if(initialQty != 0){

            Movement movement = Movement.newBuilder()
                    .id(UUID.randomUUID().toString())
                    .type(initialQty < 0 ? MovementType.Expense : MovementType.Incoming)
                    .amount(initialQty < 0 ? initialQty * -1 : initialQty)
                    .executedBy(user)
                    .executedAt(DateUtils.getUnixTime(new Date()))
                    .account(account)
                    .isAuto(false)
                    .build();

            insertMovement(movement, user);

        }

    }

    @Override
    public void insertMovement(Movement movement, User user) {
        movement.setId(UUID.randomUUID().toString());

        if(movement.getExecutedBy() == null){
            movement.setExecutedBy(user);
        }

        if(movement.getExecutedAt() == null){
            movement.setExecutedAt(DateUtils.getUnixTime(new Date()));
        }

        movement.setUptadedAt(DateUtils.getUnixTime(new Date()));

        if(movement.getCategory() == null){
            switch (movement.getType()){
                case Expense:
                    movement.setCategory(Category.newBuilder().id(defaultExpenseCategory).build());
                    break;
                case Incoming:
                    movement.setCategory(Category.newBuilder().id(defaultIncomingCategory).build());
                    break;
            }
        }

        movementDao.inserMovement(movement);

    }

    @Override
    public void updateMovement(@Valid Movement movement, User currentUser) {
        if(movement.getExecutedBy() == null){
            movement.setExecutedBy(currentUser);
        }

        if(movement.getExecutedAt() == null){
            movement.setExecutedAt(DateUtils.getUnixTime(new Date()));
        }

        movement.setUptadedAt(DateUtils.getUnixTime(new Date()));

        if(movement.getCategory() == null){
            switch (movement.getType()){
                case Expense:
                    movement.setCategory(Category.newBuilder().id(defaultExpenseCategory).build());
                    break;
                case Incoming:
                    movement.setCategory(Category.newBuilder().id(defaultIncomingCategory).build());
                    break;
            }
        }

        movementDao.updateMovement(movement);

    }

    @Override
    public void deleteMovement(String movementId) {
        movementDao.deleteMovement(movementId);
    }


    @Override
    public void generateAutoMovement(Date inDate) {
        List<ScheduledMovementSettings> scheduledMovementSettingsList = movementDao.findAutoMovementToGenerate(inDate);

        if(scheduledMovementSettingsList == null || scheduledMovementSettingsList.size() == 0){
            return;
        }

        scheduledMovementSettingsList.forEach(scheduledMovementSettings -> {
            Movement movement = Movement.newBuilder()
                                    .id(UUID.randomUUID().toString())
                                    .type(scheduledMovementSettings.getType())
                                    .amount(scheduledMovementSettings.getAmount())
                                    .executedBy(scheduledMovementSettings.getUser())
                                    .executedAt(scheduledMovementSettings.getNextMovementExecution())
                                    .account(scheduledMovementSettings.getAccount())
                                    .category(scheduledMovementSettings.getCategory())
                                    .isAuto(true)
                                    .build();

            insertAutoMovement(movement, scheduledMovementSettings, inDate);
        });
    }

    @Override
    @Transactional
    public void insertAutoMovement(Movement movement, ScheduledMovementSettings scheduledMovementSettings, Date execDate) {

        movementDao.inserMovement(movement);
        movementDao.setExecutedMovementSettings(scheduledMovementSettings, execDate);
    }

    @Transactional
    @Override
    public void leftAccount(Account myAccount, String username) {
        Map<String, Object> map = new HashMap<>();
        map.put("username", username);
        map.put("idAccount",myAccount.getId());

        accountDao.deleteUserAccount(map);

        List<HashMap<String, Object>> relationsMap = accountDao.getAccountRelations(map);
        boolean deleteAccount = true;
        String newAdmin = null;

        if(relationsMap != null && relationsMap.size() >0){
            deleteAccount = false;
            long userAlreadyAdmin =relationsMap.stream()
                                    .filter(m -> (boolean) m.get("isAdmin") == true)
                                    .count();

            if(userAlreadyAdmin == 0){
                newAdmin = (String) relationsMap.get(0).get("isAdmin");
            }

            if(newAdmin != null){
                map.put("username", newAdmin);
                map.put("isAdmin", true);
                map.put("idAccount", myAccount.getId());

                accountDao.changeGrantUserAccount(map);
            }

            if(deleteAccount){
                accountDao.deleteAccount(myAccount.getId());
            }
        }
    }
}
