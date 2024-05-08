package mate.academy.spring.boot;

import java.math.BigDecimal;
import mate.academy.spring.boot.model.Book;
import mate.academy.spring.boot.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class Application {
    @Autowired
    private BookService bookService;

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public CommandLineRunner commandLineRunner() {
        return new CommandLineRunner() {
            @Override
                public void run(String... args) throws Exception {
                Book bookOne = new Book();
                bookOne.setTitle("Harry Potter and chambers of secrets");
                bookOne.setAuthor("Joan Rowling");
                bookOne.setPrice(BigDecimal.valueOf(200));
                bookService.save(bookOne);
                System.out.println(bookService.findAll());
            }
        };
    }
}
