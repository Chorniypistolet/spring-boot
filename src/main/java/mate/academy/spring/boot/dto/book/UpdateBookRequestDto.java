package mate.academy.spring.boot.dto.book;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class UpdateBookRequestDto {
    private String title;
    private String author;
    private BigDecimal price;
    private String isbn;
    private String description;
    private String coverImage;
}
