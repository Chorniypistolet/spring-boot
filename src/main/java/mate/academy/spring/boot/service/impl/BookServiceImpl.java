package mate.academy.spring.boot.service.impl;

import java.util.List;

import lombok.RequiredArgsConstructor;
import mate.academy.spring.boot.controller.CreateBookRequestDto;
import mate.academy.spring.boot.dto.BookDto;
import mate.academy.spring.boot.exception.EntityNotFoundException;
import mate.academy.spring.boot.mapper.BookMapper;
import mate.academy.spring.boot.model.Book;
import mate.academy.spring.boot.repository.BookRepository;
import mate.academy.spring.boot.service.BookService;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class BookServiceImpl implements BookService {
    private final BookRepository bookRepository;
    private final BookMapper bookMapper;

    @Override
    public BookDto save(CreateBookRequestDto requestDto) {
        Book book = bookMapper.toModel(requestDto);
        return bookMapper.toDto(bookRepository.save(book));
    }

    @Override
    public List<BookDto> findAll() {
        return bookRepository.findAll().stream()
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

    @Override
    public List<BookDto> getAllByTitle(String title) {
        return bookRepository.findAllByTitle(title).stream()
                .map(bookMapper::toDto)
                .toList();
    }
}
