package entitySecurity;

import com.audio.entity.UserEntity;
import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
public class RefreshToken {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private String refreshToken;
    private Instant expiresAt;
    @ManyToOne(fetch = FetchType.LAZY)
    private UserEntity user;
}
