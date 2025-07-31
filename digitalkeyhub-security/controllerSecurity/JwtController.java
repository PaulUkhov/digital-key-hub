package controllerSecurity;

import com.audio.entity.UserEntity;
import com.audio.repository.UserRepository;
import dtoSecurity.AuthResponse;
import dtoSecurity.LoginRequest;
import dtoSecurity.RegisterRequest;
import esceptionSecurity.AuthException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import serviceSecurity.AuthService;


@RequiredArgsConstructor
@RestController
@RequestMapping("/api/auth")
public class JwtController {
    private final AuthService authService;
    private final UserEntity userEntity;
    private final UserRepository userRepository;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest registerRequest) {
        if(userRepository.existsByEmail(registerRequest.email())){
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(new AuthException("Email already in use"));
        }

        return ResponseEntity.ok(authService.register(registerRequest));
    }

    @GetMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        if(loginRequest.email() == null || loginRequest.password() == null || loginRequest.password().isEmpty()) {
            throw new AuthException("Invalid request");
        }
        AuthResponse login = authService.login(loginRequest);
        return ResponseEntity.ok(login);
    }

}