package mate.academy.spring.boot.service.impl;

import java.util.List;

import lombok.RequiredArgsConstructor;
import mate.academy.spring.boot.dto.book.CreateBookRequestDto;
import mate.academy.spring.boot.dto.book.BookDto;
import mate.academy.spring.boot.dto.book.BookSearchParameters;
import mate.academy.spring.boot.dto.book.UpdateBookRequestDto;
import mate.academy.spring.boot.exception.EntityNotFoundException;
import mate.academy.spring.boot.mapper.BookMapper;
import mate.academy.spring.boot.model.Book;
import mate.academy.spring.boot.repository.book.BookRepository;
import mate.academy.spring.boot.repository.book.BookSpecificationBuilder;
import mate.academy.spring.boot.service.BookService;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class BookServiceImpl implements BookService {
    private final BookRepository bookRepository;
    private final BookMapper bookMapper;
    private final BookSpecificationBuilder bookSpecificationBuilder;

    @Override
    public BookDto save(CreateBookRequestDto requestDto) {
        Book book = bookMapper.toModel(requestDto);
        return bookMapper.toDto(bookRepository.save(book));
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookDto> findAll(Pageable pageable) {
        return bookRepository.findAll(pageable).stream()
                .map(bookMapper::toDto)
                .toList();
    }

    @Override
    public BookDto findById(Long id) {
        Book book = bookRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Cant find book by id " + id)
        );
        return bookMapper.toDto(book);
    }

    public List<BookDto> getAllByTitle(String title) {
        return bookRepository.findAllByTitle(title).stream()
                .map(bookMapper::toDto)
                .toList();
    }

    @Override
    public void deleteById(Long id) {
        bookRepository.deleteById(id);
    }

    @Override
    public List<BookDto> searchBooks(BookSearchParameters bookSearchParameters, Pageable pageable) {
        Specification<Book> bookSpecification = bookSpecificationBuilder.build(bookSearchParameters);
        return bookRepository.findAll(bookSpecification, pageable).stream()
                .map(bookMapper::toDto)
                .toList();
    }

    @Override
    public BookDto update(Long id, UpdateBookRequestDto requestDto) {
        Book book = bookRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Cant find book by id " + id)
        );
        bookMapper.updateModel(book, requestDto);
        return bookMapper.toDto(bookRepository.save(book));
    }
}
