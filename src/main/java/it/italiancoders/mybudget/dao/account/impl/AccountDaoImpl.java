package it.italiancoders.mybudget.dao.account.impl;

import it.italiancoders.mybudget.dao.account.AccountDao;
import it.italiancoders.mybudget.model.api.Account;
import it.italiancoders.mybudget.model.api.User;
import it.italiancoders.mybudget.model.api.UserRole;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.support.SqlSessionDaoSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
public class AccountDaoImpl extends SqlSessionDaoSupport implements AccountDao {
    @Override
    public List<HashMap<String, Object>> getAccountRelations(Map<String, Object> map) {
       return getSqlSession().selectList("it.italiancoders.mybudget.dao.Account.getAccountRelations", map);
    }

    @Override
    public void changeGrantUserAccount(Map<String, Object> map) {
        getSqlSession().update("it.italiancoders.mybudget.dao.Account.changeGrantUserAccount", map);
    }

    @Override
    public void deleteAccount(String id) {
        Map<String, Object> map = new HashMap<>();
        map.put("id",id);
        getSqlSession().delete("it.italiancoders.mybudget.dao.Account.deleteAccount", map);

    }

    @Override
    public void modifyPrivs(String accountId, String username, UserRole role) {
        boolean isAdmin = false;

        if(role == UserRole.Admin){
            isAdmin = true;
        }

        Map<String,Object> params = new HashMap<>();
        params.put("username", username);
        params.put("isAdmin", isAdmin);
        params.put("idAccount",accountId);

        changeGrantUserAccount(params);




    }

    @Override
    public List<String> getAdministrators(String accountId) {
        Map<String,Object> params = new HashMap<>();
        params.put("isAdmin", true);
        params.put("idAccount",accountId);
        List<Map<String,Object>> retval = getSqlSession().selectList("it.italiancoders.mybudget.dao.Account.getAccountRelations", params);

        return retval == null ? null:
                retval.stream().map(el -> (String)el.get("username")).collect(Collectors.toList());

    }

    @Autowired
    @Qualifier("errorMessageSource")
    MessageSource messageSource;


    private Map<String, Object> toHashMap(Account account){
        Map<String,Object> params = new HashMap<>();
        params.put("id", account.getId());
        params.put("name", account.getName());
        params.put("description", account.getDescription());
        params.put("status",  account.getStatus() == null ? null : account.getStatus().getValue());
        params.put("defaultUsername", account.getDefaultUsername());
        return params;
    }

    @Override
    @Autowired
    public void setSqlSessionFactory(SqlSessionFactory sqlSessionFactory) {
        super.setSqlSessionFactory(sqlSessionFactory);
    }

    @Override
    public void solveTitle(Account account){
        final Locale locale = LocaleContextHolder.getLocale();
        String name = null;

        if( account == null || StringUtils.isEmpty(account.getDefaultUsername())){
            return ;
        }

        account.setDescription(getDefaultDescription(account.getDescription()));
        account.setName(getDefaultName(account.getName()));

    }

    @Override
    public List<User> findAccountMembers(String accountId) {
        Map<String,Object> params = new HashMap<>();
        params.put("accountId",accountId);

        return  getSqlSession().selectList("it.italiancoders.mybudget.dao.Account.findAccountMembers",params);
    }

    @Override
    public List<Account> findAccountsByName(User currentUser, String name) {
        Map<String,Object> params = new HashMap<>();
        params.put("name",name);
        params.put("username", currentUser.getUsername());

        List<Account> accounts = getSqlSession().selectList("it.italiancoders.mybudget.dao.Account.findAccounts",params);
        return accounts;
    }

    @Override
    public void updateAccount(Account account) {
        Map<String,Object> params = toHashMap(account);
        getSqlSession().update("it.italiancoders.mybudget.dao.Account.updateAccount",params);

    }

    @Override
    public void deleteUserAccount(Map<String, Object> map) {
        getSqlSession().delete("it.italiancoders.mybudget.dao.Account.deleteUserAccount",map);

    }


    private List<Account> findAccounts(Map<String,Object> params){
        List<Account> accounts = getSqlSession().selectList("it.italiancoders.mybudget.dao.Account.findAccounts",params);

        if(accounts != null && accounts.size() > 0){
            accounts.stream().filter(account -> !StringUtils.isEmpty(account.getDefaultUsername())).forEach(
                    account -> {
                        solveTitle(account);
                    }
            );
        }
        return accounts;
    }

    @Override
    public List<Account> findAccountsByUsername(String username) {
        Map<String,Object> params = new HashMap<>();
        params.put("username", username);

        return findAccounts(params);

    }

    @Override
    public void insertAccount(Account account, String username, boolean insertRelation, boolean isAdmin) {
        Map<String,Object> params = toHashMap(account);
        if(insertRelation){
            getSqlSession().insert("it.italiancoders.mybudget.dao.Account.insertAccount", params);
            insertAccountUserRelation(account.getId(), username, isAdmin);
        }


    }
    @Override
    public void insertAccountUserRelation(String accountId, String username, boolean isAdmin) {
        Map<String,Object> params = new HashMap<>();
        params.put("username", username);
        params.put("idAccount", accountId);
        params.put("isAdmin", isAdmin);
        getSqlSession().insert("it.italiancoders.mybudget.dao.Account.insertUserAccount", params);
    }
    public String getDefaultName(String key){
        String i18nKey = "Predefinito";

        Locale locale = LocaleContextHolder.getLocale();

        try{
            i18nKey = messageSource.getMessage(key,null,locale);
        }catch (Exception e){

        }

        return i18nKey;
    }

    public String getDefaultDescription (String key){
        String i18nKey = "Account  personale";

        Locale locale = LocaleContextHolder.getLocale();

        try{
            i18nKey = messageSource.getMessage(key,null,locale);
        }catch (Exception e){

        }

        return i18nKey;
    }

    @Override
    public Account findById(String id, String username) {
        Map<String,Object> params = new HashMap<>();
        params.put("username", username);
        params.put("id", id);
        List<Account> retval = findAccounts(params);
        return retval == null || retval.size() == 0 ? null : retval.get(0);
    }

    @Override
    public List<Account> findAccounts(String username) {
        Map<String,Object> params = new HashMap<>();
        params.put("username", username);
        List<Account> retval = findAccounts(params);
        return retval;
    }
}
