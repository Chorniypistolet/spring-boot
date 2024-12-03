package mate.academy.spring.boot.repositoryTests;

import mate.academy.spring.boot.model.Book;
import mate.academy.spring.boot.repository.book.BookRepository;
import mate.academy.spring.boot.repository.category.CategoryRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class BookRepositoryTest {
    @Autowired
    private BookRepository bookRepository;
    @Autowired
    private CategoryRepository categoryRepository;

    @Test
    @DisplayName("Test findAllByCategoryId should return books for an existing category")
    @Sql(scripts = {
            "classpath:database/add-books-to-books-table.sql",
            "classpath:database/add-categories-to-categories-table.sql",
            "classpath:database/add-books-categories-to-books-categories-table.sql"
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {
            "classpath:database/remove-books-categories-from-books-categories-table.sql",
            "classpath:database/remove-categories-from-categories-table.sql",
            "classpath:database/remove-books-from-books-table.sql"
    }, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void testFindAllByCategoryId_WithExistingCategory_ShouldReturnBooks() {
        List<Book> result = bookRepository.findAllByCategoryId(1L);
        assertEquals(2, result.size());
        assertTrue(result.stream().noneMatch(Book::isDeleted));
    }

    @Test
    @DisplayName("Test findAllByCategoryId should return empty list for a non-existing category")
    void testFindAllByCategoryId_WithNonExistingCategory_ShouldReturnEmptyList() {
        Long nonExistingCategoryId = -1L;
        List<Book> result = bookRepository.findAllByCategoryId(nonExistingCategoryId);
        assertTrue(result.isEmpty());
    }
}
