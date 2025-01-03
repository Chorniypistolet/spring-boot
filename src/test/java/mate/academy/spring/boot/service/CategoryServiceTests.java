package mate.academy.spring.boot.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import mate.academy.spring.boot.dto.category.CategoryDto;
import mate.academy.spring.boot.dto.category.CategoryRequestDto;
import mate.academy.spring.boot.exception.EntityNotFoundException;
import mate.academy.spring.boot.mapper.CategoryMapper;
import mate.academy.spring.boot.model.Category;
import mate.academy.spring.boot.repository.category.CategoryRepository;
import mate.academy.spring.boot.service.impl.CategoryServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class CategoryServiceTests {
    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private CategoryMapper categoryMapper;
    @InjectMocks
    private CategoryServiceImpl categoryService;

    @Test
    @DisplayName("""
        Test checks whether the method correctly returns a list of CategoryDto""")
    void testFindAll_WithCorrectParameters_ShouldReturnListOfCategoryDto() {
        List<Category> categories = List.of(new Category(), new Category());
        List<CategoryDto> expected = List.of(new CategoryDto(), new CategoryDto());
        when(categoryRepository.findAll()).thenReturn(categories);
        when(categoryMapper.toDto(any(Category.class)))
                .thenReturn(expected.get(0), expected.get(1));
        List<CategoryDto> actual = categoryService.findAll();
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("""
            Test checks whether the method correctly returns a CategoryDto by ID
            """)
    void testGetById_WithValidId_ShouldReturnCategoryDto() {
        Long id = 1L;
        Category category = new Category();
        CategoryDto expected = new CategoryDto();

        when(categoryRepository.findById(id)).thenReturn(Optional.of(category));
        when(categoryMapper.toDto(category)).thenReturn(expected);
        CategoryDto actual = categoryService.getById(id);

        assertEquals(expected, actual);
        verify(categoryRepository, times(1)).findById(id);
        verify(categoryMapper, times(1)).toDto(category);
    }

    @Test
    @DisplayName("""
            Test checks whether the method throws EntityNotFoundException
            when the ID is not found
            """)
    void testGetById_WithInvalidId_ShouldThrowEntityNotFoundException() {
        Long id = 1L;

        when(categoryRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> categoryService.getById(id));
        verify(categoryRepository, times(1)).findById(id);
        verify(categoryMapper, never()).toDto(any(Category.class));
    }

    @Test
    @DisplayName("""
            Test checks whether the method saves a new category and returns a CategoryDto
            """)
    void testSave_WithCorrectParameters_ShouldReturnSavedCategoryDto() {
        CategoryRequestDto requestDto = new CategoryRequestDto();
        Category category = new Category();
        CategoryDto expected = new CategoryDto();

        when(categoryMapper.toEntity(requestDto)).thenReturn(category);
        when(categoryRepository.save(category)).thenReturn(category);
        when(categoryMapper.toDto(category)).thenReturn(expected);
        CategoryDto actual = categoryService.save(requestDto);

        assertEquals(expected, actual);
        verify(categoryMapper, times(1)).toEntity(requestDto);
        verify(categoryRepository, times(1)).save(category);
        verify(categoryMapper, times(1)).toDto(category);
    }

    @Test
    @DisplayName("""
            Test checks whether the method updates an
            existing category and returns an updated CategoryDto
            """)
    void testUpdate_WithCorrectParameters_ShouldReturnUpdatedCategoryDto() {
        Long id = 1L;
        String name = "New Category Name";
        String description = "New Description";
        Category category = getCategory(id);
        CategoryRequestDto requestDto = getCategoryRequestDto(name, description);
        CategoryDto expected = getCategoryDto(name, description);

        when(categoryRepository.findById(id)).thenReturn(Optional.of(category));
        when(categoryRepository.save(category)).thenReturn(category);
        when(categoryMapper.updateCategoryFromDto(requestDto, category)).thenAnswer(invocation -> {
            category.setName(requestDto.getName());
            category.setDescription(requestDto.getDescription());
            return null;
        });
        when(categoryMapper.toDto(category)).thenReturn(expected);
        CategoryDto actual = categoryService.update(id, requestDto);

        assertEquals(expected, actual);
        verify(categoryRepository, times(1)).findById(id);
        verify(categoryMapper, times(1)).updateCategoryFromDto(requestDto, category);
        verify(categoryRepository, times(1)).save(category);
        verify(categoryMapper, times(1)).toDto(category);
    }

    @Test
    @DisplayName("""
            Test checks whether the method deletes a category by ID
            """)
    void testDeleteById_WithValidId_ShouldDeleteCategory() {
        Long id = 1L;

        categoryService.deleteById(id);

        verify(categoryRepository, times(1)).deleteById(id);
    }

    private CategoryRequestDto getCategoryRequestDto(String name, String description) {
        CategoryRequestDto requestDto = new CategoryRequestDto();
        requestDto.setName(name);
        requestDto.setDescription(description);
        return requestDto;
    }

    private Category getCategory(Long id) {
        Category category = new Category();
        category.setId(id);
        return category;
    }

    private CategoryDto getCategoryDto(String name, String description) {
        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setName(name);
        categoryDto.setDescription(description);
        return categoryDto;
    }

}
