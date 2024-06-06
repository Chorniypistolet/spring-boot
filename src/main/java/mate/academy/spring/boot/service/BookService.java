package mate.academy.spring.boot.service;

import java.util.List;
import mate.academy.spring.boot.controller.CreateBookRequestDto;
import mate.academy.spring.boot.dto.BookDto;
import mate.academy.spring.boot.dto.BookSearchParameters;
import org.springframework.stereotype.Service;

@Service
public interface BookService {

    BookDto save(CreateBookRequestDto requestDto);

    List<BookDto> findAll();

    BookDto findById(Long id);

    List<BookDto> getAllByTitle(String title);

    void deleteById(Long id);

    List<BookDto> searchBooks(BookSearchParameters bookSearchParameters);
}
