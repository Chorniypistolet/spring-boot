package mate.academy.spring.boot.dto.book;

import java.math.BigDecimal;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateBookRequestDto {

    @NotNull
    private String title;
    @NotNull
    private String author;
    @NotNull
    @Min(0)
    private BigDecimal price;
    @NotNull
    private String isbn;
    private String description;
    private String coverImage;
}
