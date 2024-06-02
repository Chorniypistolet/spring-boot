package mate.academy.spring.boot.repository.book.spec;

import java.util.Arrays;
import mate.academy.spring.boot.model.Book;
import mate.academy.spring.boot.repository.SpecificationProvider;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class IsbnSpecificationProvider implements SpecificationProvider<Book> {
    private static final String KEY = "isbn";
    @Override
    public String getKey() {
        return KEY;
    }

    public Specification<Book> getSpecification(String[] params) {
        return (root, query, criteriaBuilder) -> root.get("isbn").in(Arrays.stream(params).toArray());
    }
}
