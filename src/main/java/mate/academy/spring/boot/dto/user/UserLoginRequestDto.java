package mate.academy.spring.boot.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import org.hibernate.validator.constraints.Length;

public record UserLoginRequestDto(

        @NotEmpty
        @Email
        @Length(min = 7, max = 27)
        String email,
        @NotEmpty
        @Length(min = 6, max = 26)
        String password
) {
}
