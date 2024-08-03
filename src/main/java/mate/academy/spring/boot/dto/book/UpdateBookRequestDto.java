package mate.academy.spring.boot.dto.book;

import lombok.Data;

@Data
public class UpdateBookRequestDto {
    private String title;
    private String author;
    private Double price;
    private String isbn;
    private String description;
    private String coverImage;
}
