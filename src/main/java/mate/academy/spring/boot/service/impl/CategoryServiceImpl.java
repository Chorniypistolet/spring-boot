package mate.academy.spring.boot.service.impl;

import mate.academy.spring.boot.exception.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import mate.academy.spring.boot.dto.category.CategoryDto;
import mate.academy.spring.boot.dto.category.CategoryRequestDto;
import mate.academy.spring.boot.mapper.CategoryMapper;
import mate.academy.spring.boot.model.Category;
import mate.academy.spring.boot.repository.category.CategoryRepository;
import mate.academy.spring.boot.service.CategoryService;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Override
    public List<CategoryDto> findAll() {
        return categoryRepository.findAll().stream()
                .map(categoryMapper::toDto)
                .toList();
    }

    @Override
    public CategoryDto getById(Long id) {
        return categoryRepository.findById(id)
                .map(categoryMapper::toDto)
                .orElseThrow(() -> new EntityNotFoundException("No such a category with id: " + id));
    }

    @Override
    public CategoryDto save(CategoryRequestDto categoryRequestDtoDto) {
        Category category = categoryMapper.toEntity(categoryRequestDtoDto);
        category = categoryRepository.save(category);
        return categoryMapper.toDto(category);
    }

    @Override
    public CategoryDto update(Long id, CategoryRequestDto categoryRequestDto) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("No such a category with id: " + id));
        categoryMapper.updateCategoryFromDto(categoryRequestDto, category);
        categoryRepository.save(category);
        return categoryMapper.toDto(category);
    }

    @Override
    public void deleteById(Long id) {
        categoryRepository.deleteById(id);
    }
}
