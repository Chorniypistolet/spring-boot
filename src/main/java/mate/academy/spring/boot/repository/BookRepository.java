package mate.academy.spring.boot.repository;

import java.util.List;
import mate.academy.spring.boot.model.Book;
import org.springframework.stereotype.Repository;

@Repository
public interface BookRepository {

    Book save(Book book);

    List<Book> findAll();
}
