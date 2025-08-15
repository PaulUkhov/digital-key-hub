package unit;

import com.audio.dto.response.UserServiceInfoResponse;
import com.audio.dto.response.UserServiceResponse;
import com.audio.entity.UserEntity;
import com.audio.exception.UserNotFoundException;
import com.audio.mapper.UserMapper;
import com.audio.repository.UserRepository;
import com.audio.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Модульные тесты UserService")
class UserServiceTest {

    private static final UUID USER_ID = UUID.randomUUID();
    private static final String EMAIL = "test@example.com";

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserServiceImpl userService;

    private UserEntity testUser;
    private UserServiceResponse testResponse;
    private UserServiceInfoResponse testInfoResponse;

    @BeforeEach
    void setUp() {
        testUser = UserEntity.builder()
                .id(USER_ID)
                .email(EMAIL)
                .build();

        testResponse = new UserServiceResponse(USER_ID, EMAIL);
        testInfoResponse = new UserServiceInfoResponse(USER_ID, EMAIL);
    }

    @Nested
    class FindByIdTests {
        @Test
        void whenUserExists_thenReturnsUser() {
            when(userRepository.findById(USER_ID)).thenReturn(Optional.of(testUser));
            when(userMapper.toUserResponseDto(testUser)).thenReturn(testResponse);

            Optional<UserServiceResponse> result = userService.findById(USER_ID);

            assertThat(result)
                    .isPresent()
                    .contains(testResponse);
            verify(userRepository).findById(USER_ID);
            verify(userMapper).toUserResponseDto(testUser);
        }

        @Test
        void whenUserNotExists_thenReturnsEmpty() {
            when(userRepository.findById(USER_ID)).thenReturn(Optional.empty());

            Optional<UserServiceResponse> result = userService.findById(USER_ID);

            assertThat(result).isEmpty();
            verify(userRepository).findById(USER_ID);
            verifyNoInteractions(userMapper);
        }
    }

    @Nested
    class FindInfoByIdTests {
        @Test
        void whenUserExists_thenReturnsUserInfo() {
            when(userRepository.findInfoById(USER_ID)).thenReturn(Optional.of(testUser));
            when(userMapper.toUserServiceInfoDto(testUser)).thenReturn(testInfoResponse);

            Optional<UserServiceInfoResponse> result = userService.findInfoById(USER_ID);

            assertThat(result)
                    .isPresent()
                    .contains(testInfoResponse);
            verify(userRepository).findInfoById(USER_ID);
            verify(userMapper).toUserServiceInfoDto(testUser);
        }

        @Test
        void whenUserNotExists_thenReturnsEmpty() {
            when(userRepository.findInfoById(USER_ID)).thenReturn(Optional.empty());

            Optional<UserServiceInfoResponse> result = userService.findInfoById(USER_ID);

            assertThat(result).isEmpty();
            verify(userRepository).findInfoById(USER_ID);
            verifyNoInteractions(userMapper);
        }
    }

    @Nested
    class FindByEmailTests {
        @Test
        void whenUserExists_thenReturnsUser() {
            when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(testUser));
            when(userMapper.toUserResponseDto(testUser)).thenReturn(testResponse);

            Optional<UserServiceResponse> result = userService.findByEmail(EMAIL);

            assertThat(result)
                    .isPresent()
                    .contains(testResponse);
            verify(userRepository).findByEmail(EMAIL);
            verify(userMapper).toUserResponseDto(testUser);
        }

        @Test
        void whenUserNotExists_thenReturnsEmpty() {
            when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.empty());

            Optional<UserServiceResponse> result = userService.findByEmail(EMAIL);

            assertThat(result).isEmpty();
            verify(userRepository).findByEmail(EMAIL);
            verifyNoInteractions(userMapper);
        }
    }

    @Nested
    class FindByUsernameTests {
        @Test
        void whenUserExists_thenReturnsUserInfo() {
            when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(testUser));
            when(userMapper.toUserServiceInfoDto(testUser)).thenReturn(testInfoResponse);

            Optional<UserServiceInfoResponse> result = userService.findByUsername(EMAIL);

            assertThat(result)
                    .isPresent()
                    .contains(testInfoResponse);
            verify(userRepository).findByEmail(EMAIL);
            verify(userMapper).toUserServiceInfoDto(testUser);
        }

        @Test
        void whenUserNotExists_thenReturnsEmpty() {
            when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.empty());

            Optional<UserServiceInfoResponse> result = userService.findByUsername(EMAIL);

            assertThat(result).isEmpty();
            verify(userRepository).findByEmail(EMAIL);
            verifyNoInteractions(userMapper);
        }
    }

    @Nested
    class DeleteByIdTests {
        @Test
        void whenUserExists_thenDeletesUser() {
            when(userRepository.existsById(USER_ID)).thenReturn(true);

            userService.deleteById(USER_ID);

            verify(userRepository).existsById(USER_ID);
            verify(userRepository).deleteById(USER_ID);
        }

        @Test
        void whenUserNotExists_thenThrowsException() {
            when(userRepository.existsById(USER_ID)).thenReturn(false);

            assertThatThrownBy(() -> userService.deleteById(USER_ID))
                    .isInstanceOf(UserNotFoundException.class)
                    .hasMessage("User not found: " + USER_ID);

            verify(userRepository).existsById(USER_ID);
            verify(userRepository, never()).deleteById(any());
        }
    }

    @Nested
    class ExistsTests {
        @Test
        void existsById_whenCalled_thenReturnsTrue() {
            when(userRepository.existsById(USER_ID)).thenReturn(true);

            boolean result = userService.existsById(USER_ID);

            assertThat(result).isTrue();
            verify(userRepository).existsById(USER_ID);
        }

        @Test
        void existsByEmail_whenCalled_thenReturnsTrue() {
            when(userRepository.existsByEmail(EMAIL)).thenReturn(true);

            boolean result = userService.existsByEmail(EMAIL);

            assertThat(result).isTrue();
            verify(userRepository).existsByEmail(EMAIL);
        }
    }
}