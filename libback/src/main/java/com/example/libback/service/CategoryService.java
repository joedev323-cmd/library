package com.example.libback.service;

import com.example.libback.model.Catergory;
import com.example.libback.repository.CategoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public List<Catergory> getAllCategories() {
        return categoryRepository.findAll();
    }

    public Catergory saveCategory(Catergory category) {
        return categoryRepository.save(category);
    }
}