package mate.academy.spring.boot.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;

public record UserLoginRequestDto(
        @NotBlank
        @Email
        @Length(min = 7, max = 27)
        String email,
        @NotBlank
        @Length(min = 4, max = 26)
        String password
) {
}
