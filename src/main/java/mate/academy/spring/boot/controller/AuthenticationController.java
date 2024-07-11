package mate.academy.spring.boot.controller;

import lombok.RequiredArgsConstructor;
import mate.academy.spring.boot.dto.user.UserResponseDto;
import mate.academy.spring.boot.dto.user.UserRegistrationRequestDto;
import mate.academy.spring.boot.exception.RegistrationException;
import mate.academy.spring.boot.service.UserService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthenticationController {
    private final UserService userService;

    @PostMapping("/registration")
    public UserResponseDto register(@RequestBody UserRegistrationRequestDto requestDto) throws RegistrationException {
        return userService.register(requestDto);
    }
}
