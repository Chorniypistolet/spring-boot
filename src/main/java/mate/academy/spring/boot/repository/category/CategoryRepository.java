package mate.academy.spring.boot.repository.category;

import mate.academy.spring.boot.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {

}
