package it.italiancoders.mybudget.dao.account;

import it.italiancoders.mybudget.model.api.Account;
import it.italiancoders.mybudget.model.api.User;
import it.italiancoders.mybudget.model.api.UserRole;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface AccountDao {

    List<Account> findAccountsByUsername(String username);

    void insertAccount(Account account, String username, boolean insertRealation, boolean isAdmin);

    void insertAccountUserRelation(String accountId, String username, boolean isAdmin);

    String getDefaultName(String key);

    String getDefaultDescription (String key);

    Account findById(String id, String username);

    List<Account> findAccounts(String username);

    void solveTitle(Account account);

    List<User> findAccountMembers(String accountId);

    List<Account> findAccountsByName(User currentUser,  String name);

    void updateAccount( Account account);

    void deleteUserAccount(Map<String, Object> map);

    List<HashMap<String,Object>> getAccountRelations(Map<String, Object> map);

    void changeGrantUserAccount(Map<String, Object> map);

    void deleteAccount(String id);

    void modifyPrivs(String accountId, String username, UserRole role);

    List<String> getAdministrators (String accountId);
}
