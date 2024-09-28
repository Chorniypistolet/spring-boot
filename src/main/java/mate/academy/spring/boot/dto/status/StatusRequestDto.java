package mate.academy.spring.boot.dto.status;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class StatusRequestDto {
    @NotNull
    private String status;
}
