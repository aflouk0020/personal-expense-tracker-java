package com.taha.expensetracker.service;

import com.taha.expensetracker.dao.CategoryDao;
import com.taha.expensetracker.model.Category;
import com.taha.expensetracker.model.enums.TransactionType;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;

public class CategoryService {

    private final CategoryDao categoryDao;

    public CategoryService() {
        this(new CategoryDao());
    }

    public CategoryService(CategoryDao categoryDao) {
        this.categoryDao = Objects.requireNonNull(categoryDao, "CategoryDao must not be null.");
    }

    public List<Category> getAllCategories() {
        return categoryDao.findAll()
                .stream()
                .sorted(Comparator.comparing(Category::getName, String.CASE_INSENSITIVE_ORDER)
                        .thenComparing(category -> category.getType().name()))
                .toList();
    }

    public List<Category> getCategoriesByType(TransactionType type) {
        Predicate<Category> matchesType = category -> category.getType() == type;

        return categoryDao.findAll()
                .stream()
                .filter(matchesType)
                .sorted(Comparator.comparing(Category::getName, String.CASE_INSENSITIVE_ORDER))
                .toList();
    }

    public Optional<Category> getCategoryById(long id) {
        if (id <= 0) {
            throw new IllegalArgumentException("Category id must be greater than zero.");
        }
        return categoryDao.findById(id);
    }

    public Category saveCategory(Category category) {
        validateCategory(category);
        return categoryDao.save(category);
    }

    public boolean deleteCategory(long id) {
        if (id <= 0) {
            throw new IllegalArgumentException("Category id must be greater than zero.");
        }
        return categoryDao.deleteById(id);
    }

    public boolean categoryExistsByNameAndType(String name, TransactionType type) {
        String cleanedName = Objects.requireNonNull(name, "Category name must not be null.").trim();

        if (cleanedName.isEmpty()) {
            return false;
        }

        return categoryDao.findAll()
                .stream()
                .anyMatch(category ->
                        category.getName().equalsIgnoreCase(cleanedName)
                                && category.getType() == type);
    }

    private void validateCategory(Category category) {
        Objects.requireNonNull(category, "Category must not be null.");

        if (categoryExistsByNameAndType(category.getName(), category.getType())
                && category.getId() == null) {
            throw new IllegalArgumentException("A category with the same name and type already exists.");
        }
    }
}