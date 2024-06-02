package mate.academy.spring.boot.repository.book.spec;

import java.util.Arrays;
import mate.academy.spring.boot.model.Book;
import mate.academy.spring.boot.repository.SpecificationProvider;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class AuthorSpecificationProvider implements SpecificationProvider<Book> {
    private static final String KEY = "author";

    @Override
    public String getKey() {
        return KEY;
    }

    public Specification<Book> getSpecification(String[] params) {
        return (root, query, criteriaBuilder) -> root.get("author").in(Arrays.stream(params).toArray());
    }
}
