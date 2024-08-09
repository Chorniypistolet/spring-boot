package mate.academy.spring.boot.security;

import lombok.RequiredArgsConstructor;
import mate.academy.spring.boot.exception.EntityNotFoundException;
import mate.academy.spring.boot.repository.user.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByEmailWithRoles(username)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Can't find user by email: " + username));
    }
}
