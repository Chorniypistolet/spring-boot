package mate.academy.spring.boot.repository.book.spec;

import lombok.RequiredArgsConstructor;
import mate.academy.spring.boot.model.Book;
import mate.academy.spring.boot.repository.SpecificationProvider;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class BookSpecificationProvider {

    @Component
    private static class TitleSpecification implements SpecificationProvider<Book> {
        private static final String TITLE_KEY = "title";

        @Override
        public String getKey() {
            return TITLE_KEY;
        }

        @Override
        public Specification<Book> getSpecification(String[] params) {
            return (root, query, criteriaBuilder) -> root.get("title").in((Object[]) params);
        }
    }

    @Component
    private static class AuthorSpecification implements SpecificationProvider<Book> {
        private static final String AUTHOR_KEY = "author";

        @Override
        public String getKey() {
            return AUTHOR_KEY;
        }

        @Override
        public Specification<Book> getSpecification(String[] params) {
            return (root, query, criteriaBuilder) -> root.get("author").in((Object[]) params);
        }
    }

    @Component
    private static class IsbnSpecification implements SpecificationProvider<Book> {
        private static final String ISBN_KEY = "isbn";

        @Override
        public String getKey() {
            return ISBN_KEY;
        }

        @Override
        public Specification<Book> getSpecification(String[] params) {
            return (root, query, criteriaBuilder) -> root.get("isbn").in((Object[]) params);
        }
    }
}
