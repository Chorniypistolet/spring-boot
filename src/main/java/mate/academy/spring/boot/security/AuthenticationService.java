package mate.academy.spring.boot.security;

import lombok.RequiredArgsConstructor;
import mate.academy.spring.boot.dto.user.UserLoginRequestDto;
import mate.academy.spring.boot.model.User;
import mate.academy.spring.boot.repository.user.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserRepository userRepository;
    public boolean login(UserLoginRequestDto requestDto) {
        Optional<User> user = userRepository.findByEmail(requestDto.email());
        return user.isPresent() && user.get().getPassword().equals(requestDto.password());
    }
}
