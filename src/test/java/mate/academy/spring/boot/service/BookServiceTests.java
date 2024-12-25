package mate.academy.spring.boot.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import mate.academy.spring.boot.dto.book.BookDto;
import mate.academy.spring.boot.dto.book.BookDtoWithoutCategoryIds;
import mate.academy.spring.boot.dto.book.BookSearchParameters;
import mate.academy.spring.boot.dto.book.CreateBookRequestDto;
import mate.academy.spring.boot.dto.book.UpdateBookRequestDto;
import mate.academy.spring.boot.exception.EntityNotFoundException;
import mate.academy.spring.boot.mapper.BookMapper;
import mate.academy.spring.boot.model.Book;
import mate.academy.spring.boot.repository.book.BookRepository;
import mate.academy.spring.boot.repository.book.BookSpecificationBuilder;
import mate.academy.spring.boot.service.impl.BookServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

@ExtendWith(MockitoExtension.class)
public class BookServiceTests {
    @Mock
    private BookRepository bookRepository;
    @Mock
    private BookMapper bookMapper;
    @Mock
    private BookSpecificationBuilder bookSpecificationBuilder;
    @InjectMocks
    private BookServiceImpl bookService;

    @Test
    @DisplayName("""
            Should save a book with correct fields and return the expected BookDto""")
    void testSave_WithCorrectParameters_ShouldReturnSavedBookDto() {
        String title = "title";
        String author = "Author";
        BigDecimal price = BigDecimal.valueOf(222);
        String isbn = String.valueOf(123123123);
        CreateBookRequestDto requestDto = getCreateBookRequestDto(title, author, price, isbn);
        Book book = getBook(title, author, price, isbn);
        BookDto expected = getBookDto(title, author, price, isbn);

        when(bookMapper.toModel(requestDto)).thenReturn(book);
        when(bookRepository.save(book)).thenReturn(book);
        when(bookMapper.toDto(book)).thenReturn(expected);
        BookDto actual = bookService.save(requestDto);

        assertEquals(expected, actual, "Expected and actual BookDto should match.");
        verify(bookRepository).save(book);
    }

    @Test
    @DisplayName("""
            Book search by known id, we expect a positive result""")
    void testFindById_WithCorrectId_ShouldReturnBookDto() {
        Long id = 1L;
        Book book = new Book();
        BookDto expected = new BookDto();

        when(bookRepository.findById(id)).thenReturn(Optional.of(book));
        when(bookMapper.toDto(book)).thenReturn(expected);
        BookDto actual = bookService.findById(id);

        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("""
            Searching for a book with the wrong id, we expect EntityNotFoundException""")
    void testFindById_WithIncorrectId_ShouldThrowEntityNotFoundException() {
        Long id = 1L;

        when(bookRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> bookService.findById(id));
    }

    @Test
    @DisplayName("""
            Test checks whether the method correctly returns a list of objects of type BookDto""")
    void testFindAll_WithCorrectParameters_ShouldReturnListOfBookDto() {
        Pageable pageable = PageRequest.of(0, 10);
        List<Book> books = List.of(new Book());
        Page<Book> bookPage = new PageImpl<>(books, pageable, books.size());
        List<BookDto> expected = List.of(new BookDto());

        when(bookRepository.findAll(pageable)).thenReturn(bookPage);
        when(bookMapper.toDto(any())).thenReturn(expected.get(0));
        List<BookDto> actual = bookService.findAll(pageable);

        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("""
            Test checks whether the method correctly
            calls the delete method by ID to the repository""")
    void testDeleteById_WithCorrectId_ShouldCallRepository() {
        Long id = 1L;

        bookService.deleteById(id);

        verify(bookRepository).deleteById(id);
    }

    @Test
    @DisplayName("""
            This test tests a service method that should
            update the book data and return the updated BookDto object.""")
    void testUpdate_WithCorrectId_ShouldReturnUpdatedBookDto() {
        Book book = new Book();
        UpdateBookRequestDto requestDto = new UpdateBookRequestDto();
        BookDto expected = new BookDto();

        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(bookRepository.save(book)).thenReturn(book);
        when(bookMapper.toDto(book)).thenReturn(expected);
        BookDto actual = bookService.update(1L, requestDto);

        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("""
            The test checks should determine
            if an empty list is returned when we search
            for books by a nonexistent or empty category""")
    void testFindAllByCategoryId_WithNonExistentCategory_ShouldReturnEmptyList() {
        Long categoryId = 1L;

        when(bookRepository.findAllByCategoryId(categoryId)).thenReturn(List.of());
        List<BookDtoWithoutCategoryIds> actual = bookService.findAllByCategoryId(categoryId);

        assertTrue(actual.isEmpty());
    }

    @Test
    @DisplayName("""
            Test checks whether the searchBooks method
            correctly searches and returns a list of
            books based on the specified search parameters""")
    void testSearchBooks_WithCorrectParameters_ShouldReturnBooks() {
        BookSearchParameters parameters = new BookSearchParameters(
                new String[]{"Author"},
                new String[]{"Category"},
                new String[]{"Title"}
        );
        Pageable pageable = PageRequest.of(0, 10);
        Book book = new Book();
        BookDto bookDto = new BookDto();
        Specification<Book> spec = mock(Specification.class);
        Page<Book> bookPage = new PageImpl<>(List.of(book), pageable, 1);

        when(bookSpecificationBuilder.build(parameters)).thenReturn(spec);
        when(bookRepository.findAll(spec, pageable)).thenReturn(bookPage);
        when(bookMapper.toDto(any(Book.class))).thenReturn(bookDto);
        List<BookDto> actual = bookService.searchBooks(parameters, pageable);
        List<BookDto> expected = List.of(bookDto);

        assertEquals(expected, actual);
    }

    private CreateBookRequestDto getCreateBookRequestDto(String title, String author,
                                                        BigDecimal price, String isbn) {
        CreateBookRequestDto requestDto = new CreateBookRequestDto();
        requestDto.setTitle(title);
        requestDto.setAuthor(author);
        requestDto.setPrice(price);
        requestDto.setIsbn(isbn);
        return requestDto;
    }

    private Book getBook(String title, String author,
                        BigDecimal price, String isbn) {
        Book book = new Book();
        book.setTitle(title);
        book.setAuthor(author);
        book.setPrice(price);
        book.setIsbn(isbn);
        return book;
    }

    private BookDto getBookDto(String title, String author,
                              BigDecimal price, String isbn) {
        BookDto bookDto = new BookDto();
        bookDto.setTitle(title);
        bookDto.setAuthor(author);
        bookDto.setPrice(price);
        bookDto.setIsbn(isbn);
        return bookDto;
    }
}
