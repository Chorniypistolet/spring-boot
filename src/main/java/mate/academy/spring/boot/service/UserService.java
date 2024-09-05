package mate.academy.spring.boot.service;

import mate.academy.spring.boot.dto.user.UserRegistrationRequestDto;
import mate.academy.spring.boot.dto.user.UserResponseDto;
import mate.academy.spring.boot.exception.RegistrationException;
import mate.academy.spring.boot.model.User;

public interface UserService {
    UserResponseDto register(UserRegistrationRequestDto requestDto) throws RegistrationException;

    User findById(Long id);
}
