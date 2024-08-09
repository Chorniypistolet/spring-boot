package mate.academy.spring.boot.controller;

import java.util.List;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import mate.academy.spring.boot.dto.book.BookDto;
import mate.academy.spring.boot.dto.book.BookSearchParameters;
import mate.academy.spring.boot.dto.book.CreateBookRequestDto;
import mate.academy.spring.boot.dto.book.UpdateBookRequestDto;
import mate.academy.spring.boot.service.BookService;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Book API")
@RequiredArgsConstructor
@RestController
@RequestMapping("/books")
public class BookController {
    private final BookService bookService;

    @GetMapping
    @Operation(summary = "Get all books")
    public List<BookDto> getAll(@ParameterObject @PageableDefault Pageable pageable) {
        return bookService.findAll(pageable);
    }

    @GetMapping("/{id}")
    @Hidden
    public BookDto findById(@PathVariable Long id) {
        return bookService.findById(id);
    }

    @GetMapping("/by-title")
    @Operation(summary = "Get book by title")
    public List<BookDto> getAllByTitle(@RequestParam String title) {
        return bookService.getAllByTitle(title);
    }

    @PostMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(summary = "Create new book")
    public BookDto createBook(@RequestBody @Valid CreateBookRequestDto requestDto) {
        return bookService.save(requestDto);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("/{id}")
    @Hidden
    public void delete(@PathVariable Long id) {
        bookService.deleteById(id);
    }

    @GetMapping("/search")
    @Operation(summary = "Get a books by search params")
    public List<BookDto> searchBooks(BookSearchParameters searchParameters, @ParameterObject @PageableDefault Pageable pageable) {
        return bookService.searchBooks(searchParameters, pageable);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(summary = "Update book by ID")
    public BookDto updateBook(@PathVariable Long id, @RequestBody @Valid UpdateBookRequestDto requestDto) {
        return bookService.update(id, requestDto);
    }
}
