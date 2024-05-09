package mate.academy.spring.boot.controller;

import java.util.List;
import lombok.RequiredArgsConstructor;
import mate.academy.spring.boot.dto.BookDto;
import mate.academy.spring.boot.service.BookService;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/books")
public class BookController {
    private final BookService bookService;

    @GetMapping
    public List<BookDto> getAll() {
        return bookService.findAll();
    }

    @GetMapping("/{id}")
    public BookDto findById(@PathVariable Long id) {
        return bookService.findById(id);
    }

    @GetMapping("/by-title")
    public List<BookDto> getAllByTitle(@RequestParam String title) {
        return bookService.getAllByTitle(title);
    }

    @PostMapping
    public BookDto createBook(@RequestBody CreateBookRequestDto requestDto) {
        return bookService.save(requestDto);
    }
}
