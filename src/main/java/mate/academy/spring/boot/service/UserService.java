package mate.academy.spring.boot.service;

import mate.academy.spring.boot.dto.user.UserRegistrationRequestDto;
import mate.academy.spring.boot.dto.user.UserResponseDto;
import mate.academy.spring.boot.exception.RegistrationException;

public interface UserService {
    UserResponseDto register(UserRegistrationRequestDto requestDto) throws RegistrationException;
}
