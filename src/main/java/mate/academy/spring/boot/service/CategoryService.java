package mate.academy.spring.boot.service;

import mate.academy.spring.boot.dto.category.CategoryDto;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public interface CategoryService {
    List<CategoryDto> findAll();
    CategoryDto getById(Long id);
    CategoryDto save(CategoryDto categoryDto);
    CategoryDto update(Long id, CategoryDto categoryDto);
    void deleteById(Long id);
}
