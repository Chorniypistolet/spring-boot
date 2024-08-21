package mate.academy.spring.boot.service;

import java.util.List;
import mate.academy.spring.boot.dto.book.*;
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

    BookDto update(Long id, UpdateBookRequestDto requestDto);

    List<BookDtoWithoutCategoryIds> findAllByCategoryId(Long id);
}
