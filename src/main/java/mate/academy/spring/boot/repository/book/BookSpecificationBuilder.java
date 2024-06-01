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
    private SpecificationProviderManager<Book> specificationProviderManager;

    @Override
    public Specification<Book> build(BookSearchParameters searchParameters) {
        Specification<Book> bookSpec = Specification.where(null);
        if (searchParameters.titles() != null && searchParameters.titles().length > 0) {
            bookSpec = bookSpec.and(specificationProviderManager
                    .getSpecificationProvider("title")
                    .getSpecification(searchParameters.titles()));
        }
        if (searchParameters.authors() != null && searchParameters.authors().length > 0) {
            bookSpec = bookSpec.and(specificationProviderManager
                    .getSpecificationProvider("author")
                    .getSpecification(searchParameters.authors()));
        }
        if (searchParameters.isbn() != null && searchParameters.isbn().length > 0) {
            bookSpec = bookSpec.and(specificationProviderManager
                    .getSpecificationProvider("isbn")
                    .getSpecification(searchParameters.isbn()));
        }
        return bookSpec;
    }
}
