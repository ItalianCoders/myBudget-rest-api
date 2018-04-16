package it.italiancoders.mybudget.dao.category;

import it.italiancoders.mybudget.model.api.Category;

import java.util.List;

public interface CategoryDao {
    List<Category> findCategories(String accountId);
    Category findCategoryByIdAndAccount(String id, String accountId);
    void solveTitle(Category category);
}
