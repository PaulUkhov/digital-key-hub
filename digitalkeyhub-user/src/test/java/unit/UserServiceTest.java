package unit;

import com.audio.dto.response.UserServiceInfoResponse;
import com.audio.entity.UserEntity;
import com.audio.mapper.UserMapper;
import com.audio.repository.UserRepository;
import com.audio.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private UserServiceImpl userServiceImpl;
    @Mock
    private UserMapper userMapper;
    public final static UUID userId = UUID.fromString("11111111-1111-1111-1111-111111111111");
    public final static String email = "test@digitalkeyhub.com";
    public UserEntity testUser;
    public UserServiceInfoResponse testDto;

    @BeforeEach
    void setUp() {
        testUser = new UserEntity();
        testUser.setEmail(email);
        testUser.setId(userId);


        testDto = new UserServiceInfoResponse(userId, email);

    }

    @Test
    void testFindById_UserExists_ReturnsUser() {
        when(userRepository.findInfoById(userId)).thenReturn(Optional.of(testUser));
        when(userMapper.toUserServiceInfoDto(testUser)).thenReturn(testDto);
        Optional<UserServiceInfoResponse> userServiceInfoResponse = userServiceImpl.findInfoById(userId);
        assertTrue(userServiceInfoResponse.isPresent());
        assertEquals(testDto, userServiceInfoResponse.get());
    }

    //
    @Test
    void testFindByEmail_UserExists_ReturnsUser() {
        when(userRepository.findInfoById(userId)).thenReturn(Optional.of(testUser));
        when(userMapper.toUserServiceInfoDto(testUser)).thenReturn(testDto);
        UserServiceInfoResponse userServiceResponse = userServiceImpl.findInfoById(userId)
                .orElseThrow(() -> new AssertionError("User is not found"));
        assertEquals(email, userServiceResponse.email());
    }

    @Test
    void deleteById_shouldThrowWhenUserNotExists() {
        when(userRepository.existsById(userId)).thenReturn(false);
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> {
                    userServiceImpl.deleteById(userId);
                });
        assertEquals("User not found: " + userId, ex.getMessage());
        verify(userRepository, never()).deleteById(any());
    }
}

