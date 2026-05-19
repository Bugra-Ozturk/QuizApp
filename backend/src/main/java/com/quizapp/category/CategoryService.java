package com.quizapp.category;

import com.quizapp.common.exception.ApiException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Kategori CRUD iş mantığı.
 * Okuma işlemleri herkese açık; oluşturma/silme yalnızca ADMIN.
 */
@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public List<Category> findAll() {
        return categoryRepository.findAll();
    }

    public Category findById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new ApiException("Kategori bulunamadı: " + id, HttpStatus.NOT_FOUND));
    }

    public Category create(Category category) {
        if (categoryRepository.existsByName(category.getName())) {
            throw new ApiException("Bu kategori adı zaten mevcut", HttpStatus.CONFLICT);
        }
        return categoryRepository.save(category);
    }

    public Category update(Long id, Category updated) {
        Category existing = findById(id);
        existing.setName(updated.getName());
        existing.setDescription(updated.getDescription());
        return categoryRepository.save(existing);
    }

    public void delete(Long id) {
        if (!categoryRepository.existsById(id)) {
            throw new ApiException("Kategori bulunamadı: " + id, HttpStatus.NOT_FOUND);
        }
        categoryRepository.deleteById(id);
    }
}
