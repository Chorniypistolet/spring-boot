package mate.academy.spring.boot.repository;

import mate.academy.spring.boot.model.Book;
import org.springframework.data.jpa.domain.Specification;

public interface SpecificationProvider<T> {
    String getKey();
    Specification<T> getSpecification(String[] params);
}
