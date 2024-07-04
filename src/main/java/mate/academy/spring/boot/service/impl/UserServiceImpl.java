package mate.academy.spring.boot.service.impl;

import lombok.RequiredArgsConstructor;
import mate.academy.spring.boot.dto.user.UserRegistrationRequestDto;
import mate.academy.spring.boot.dto.user.UserResponseDto;
import mate.academy.spring.boot.exception.RegistrationException;
import mate.academy.spring.boot.mapper.UserMapper;
import mate.academy.spring.boot.model.User;
import mate.academy.spring.boot.repository.user.UserRepository;
import mate.academy.spring.boot.service.UserService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public UserResponseDto register(UserRegistrationRequestDto requestDto) throws RegistrationException {
        if(userRepository.existsByEmail(requestDto.getEmail()).isPresent()) {
            throw new RegistrationException(String.format("User with this email: %s already exists"
                    , requestDto.getEmail()));
        }
        User savedUser = userRepository.save(userMapper.toUser(requestDto));
        return userMapper.toUserResponse(savedUser);
    }
}
