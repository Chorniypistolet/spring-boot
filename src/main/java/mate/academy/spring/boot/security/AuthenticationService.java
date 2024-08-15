package mate.academy.spring.boot.security;

import lombok.RequiredArgsConstructor;
import mate.academy.spring.boot.dto.user.UserLoginRequestDto;
import mate.academy.spring.boot.dto.user.UserLoginResponseDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private AuthenticationManager authenticationManager;

    public UserLoginResponseDto authenticate(UserLoginRequestDto userLoginRequestDto) {
        final Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(userLoginRequestDto.email(), userLoginRequestDto.password())
        );
        String token = jwtUtil.generateToken(userLoginRequestDto.email());
        UserLoginResponseDto userLoginResponseDto = new UserLoginResponseDto();
        userLoginResponseDto.setToken(token);
        return userLoginResponseDto;
    }
}
