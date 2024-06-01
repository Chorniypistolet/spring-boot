package mate.academy.spring.boot.repository.book;

import lombok.RequiredArgsConstructor;
import mate.academy.spring.boot.model.Book;
import mate.academy.spring.boot.repository.SpecificationProvider;
import mate.academy.spring.boot.repository.SpecificationProviderManager;
import org.springframework.stereotype.Component;

import java.util.List;

@RequiredArgsConstructor
@Component
public class BookSpecificationProviderManager implements SpecificationProviderManager<Book> {
    private List<SpecificationProvider<Book>> bookSpecificationProviders;

    @Override
    public SpecificationProvider<Book> getSpecificationProvider(String key) {
        return bookSpecificationProviders.stream()
                .filter(b -> b.getKey().equals(key))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Cant find correct specification provider for key " + key));
    }
}
