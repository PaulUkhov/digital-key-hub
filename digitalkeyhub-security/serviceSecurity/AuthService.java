package serviceSecurity;

import com.audio.entity.UserEntity;
import com.audio.repository.UserRepository;
import com.audio.service.UserService;
import dtoSecurity.AuthResponse;
import dtoSecurity.LoginRequest;
import dtoSecurity.RegisterRequest;
import esceptionSecurity.AuthException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserService userService; // Из user-модуля
    private final JwtService jwtService;
    private final PasswordEncoder encoder;
    private final UserEntity userEntity;
    private final UserRepository userRepository;

    public AuthResponse register(RegisterRequest request) {
        if(request.email() == null || request.email().isEmpty() || request.password() == null || request.password().isEmpty()) {
            throw new AuthException("Invalid request");
        }
        return new AuthResponse(jwtService.generateToken(userEntity), jwtService.generateRefreshToken(userEntity));
    }

    public AuthResponse login(LoginRequest request) {
        UserEntity userEntity = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new AuthException("User is not found"));

        if (!encoder.matches(request.password(), userEntity.getPassword())) {
            throw new AuthException("Wrong password");
        }

        return new AuthResponse(jwtService.generateToken(userEntity), jwtService.generateRefreshToken(userEntity));
    }
}

