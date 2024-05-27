package mate.academy.spring.boot.repository;

import java.util.List;
import mate.academy.spring.boot.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

    List<Book> findAllByTitleContainsIgnoreCase(String title);
}
