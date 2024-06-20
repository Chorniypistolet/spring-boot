package mate.academy.spring.boot.service;

import java.util.List;
import mate.academy.spring.boot.controller.CreateBookRequestDto;
import mate.academy.spring.boot.dto.BookDto;
import mate.academy.spring.boot.dto.BookSearchParameters;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public interface BookService {

    BookDto save(CreateBookRequestDto requestDto);

    List<BookDto> findAll(Pageable pageable);

    BookDto findById(Long id);

    List<BookDto> getAllByTitle(String title);

    void deleteById(Long id);

    List<BookDto> searchBooks(BookSearchParameters bookSearchParameters, Pageable pageable);
}
