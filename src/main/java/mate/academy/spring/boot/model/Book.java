package mate.academy.spring.boot.model;

import jakarta.persistence.*;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;

@RequiredArgsConstructor
@Entity
@Table(name = "books")
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, length = 50)
    private String title;
    private String author;
    private String isbn;
    private BigDecimal price;
    @Column(length = 100)
    private String description;
    private String coverImage;

    @Override
    public String toString() {
        return "Book{"
                + "id= " + id
                + ", title= '" + title + '\''
                + ", author= '" + author + '\''
                + ", isbn= '" + isbn + '\''
                + ", price= " + price
                + ", description= '" + description + '\''
                + ", coverImage= '" + coverImage + '\''
                + '}';
    }
}
