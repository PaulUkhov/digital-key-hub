package serviceSecurity;

import com.audio.entity.UserEntity;
import com.audio.repository.UserRepository;
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
    private final JwtService jwtService;
    private final PasswordEncoder encoder;
    private final UserRepository userRepository;

    public AuthResponse register(RegisterRequest request) {
        if (request.email() == null || request.email().isEmpty() || request.password() == null || request.password().isEmpty()) {
            throw new AuthException("Invalid request");
        }
        UserEntity userEntity = new UserEntity();
        userEntity.setEmail(request.email());
        userEntity.setPassword(encoder.encode(request.password()));
        userRepository.save(userEntity);

        return new AuthResponse(jwtService.generateToken(userEntity), jwtService.generateRefreshToken(userEntity));
    }

    public AuthResponse login(LoginRequest request) {
        UserEntity user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new AuthException("Invalid email or password"));
        if (!encoder.matches(request.password(), user.getPassword())) {
            throw new AuthException("Invalid password");
        }
        return new AuthResponse(jwtService.generateToken(user), jwtService.generateRefreshToken(user));
    }

}


