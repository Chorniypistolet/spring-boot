package mate.academy.spring.boot.dto.status;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class StatusRequestDto {
    @NotBlank
    private String status;
}
