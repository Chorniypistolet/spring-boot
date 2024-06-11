package mate.academy.spring.boot.repository.book;

import lombok.RequiredArgsConstructor;
import mate.academy.spring.boot.dto.BookSearchParameters;
import mate.academy.spring.boot.model.Book;
import mate.academy.spring.boot.repository.SpecificationBuilder;
import mate.academy.spring.boot.repository.SpecificationProviderManager;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class BookSpecificationBuilder implements SpecificationBuilder<Book> {
    private final SpecificationProviderManager<Book> specificationProviderManager;

    @Override
    public Specification<Book> build(BookSearchParameters searchParameters) {
        Specification<Book> bookSpec = Specification.where(null);
        if (searchParameters.title() != null && searchParameters.title().length > 0) {
            bookSpec = bookSpec.and(specificationProviderManager
                    .getSpecificationProvider("title")
                    .getSpecification(searchParameters.title()));
        }
        if (searchParameters.author() != null && searchParameters.author().length > 0) {
            bookSpec = bookSpec.and(specificationProviderManager
                    .getSpecificationProvider("author")
                    .getSpecification(searchParameters.author()));
        }
        if (searchParameters.isbn() != null && searchParameters.isbn().length > 0) {
            bookSpec = bookSpec.and(specificationProviderManager
                    .getSpecificationProvider("isbn")
                    .getSpecification(searchParameters.isbn()));
        }
        return bookSpec;
    }
}
