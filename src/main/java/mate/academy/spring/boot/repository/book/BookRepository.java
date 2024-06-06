package mate.academy.spring.boot.repository.book;

import java.util.List;
import mate.academy.spring.boot.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface BookRepository extends JpaRepository<Book, Long>, JpaSpecificationExecutor<Book>{

    List<Book> findAllByTitle(String title);

}
