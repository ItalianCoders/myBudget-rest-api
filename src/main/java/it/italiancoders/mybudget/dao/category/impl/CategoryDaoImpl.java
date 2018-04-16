package it.italiancoders.mybudget.dao.category.impl;

import it.italiancoders.mybudget.dao.account.AccountDao;
import it.italiancoders.mybudget.dao.category.CategoryDao;
import it.italiancoders.mybudget.model.api.Account;
import it.italiancoders.mybudget.model.api.Category;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.support.SqlSessionDaoSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

@Repository
public class CategoryDaoImpl extends SqlSessionDaoSupport implements CategoryDao {

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
    public Category findCategoryByIdAndAccount(String id, String accountId){
        Map<String,Object> params = new HashMap<>();
        params.put("accountId", accountId);
        params.put("id", id);
        List<Category> retval = find(params);
        return retval == null  || retval.size() == 0 ? null : retval.get(0);
    }

    @Override
    public List<Category> findCategories(String accountId) {
        Map<String,Object> params = new HashMap<>();
        params.put("accountId", accountId);
        return find(params);
    }

    public void solveTitle(Category category){
        final Locale locale = LocaleContextHolder.getLocale();

        if( category == null || category.getIsEditable()){
            return ;
        }
        String value = category.getValue();

        try{
            value = messageSource.getMessage(value,null,locale);
        }catch (Exception e){

        }

        category.setValue(value);

    }

    private List<Category> find(Map<String, Object> params){
        final Locale locale = LocaleContextHolder.getLocale();
        List<Category> categories = getSqlSession().selectList("it.italiancoders.mybudget.dao.Category.findCategories",params);

        if(categories == null || categories.size() == 0){
            return new ArrayList<>();
        }

        return categories.stream()
                .map(category -> {
                    if(category.getIsEditable()){
                        return category;
                    }
                    solveTitle(category);
                    return category;
                }).sorted((c1,c2) -> c1.getValue().compareToIgnoreCase(c2.getValue()))
                .collect(Collectors.toList());


    }
}
