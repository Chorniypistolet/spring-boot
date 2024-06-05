package mate.academy.spring.boot.repository;

import java.util.List;
import java.util.Optional;

import mate.academy.spring.boot.model.Book;
import org.springframework.stereotype.Repository;

@Repository
public interface BookRepository {

    Book save(Book book);

    Optional<Book> findById(Long id);

    List<Book> findAll();

    List<Book> findAllByTitle(String title);
}
