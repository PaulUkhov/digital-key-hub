package unit;


import com.audio.dto.request.ProfileUpdateServiceRequest;
import com.audio.dto.response.ProfileServiceResponse;
import com.audio.entity.ProfileEntity;
import com.audio.exception.ProfileNotFoundException;
import com.audio.mapper.UserMapper;
import com.audio.repository.ProfileRepository;
import com.audio.service.FileStorageService;
import com.audio.service.impl.ProfileServiceImpl;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Модульные тесты ProfileService")
class TestProfileService {

    @Mock
    private ProfileRepository profileRepo;

    @Mock
    private UserMapper userMapper;

    @Mock
    private FileStorageService storageService;

    @Mock
    private MultipartFile image;

    @InjectMocks
    private ProfileServiceImpl profileServiceImpl;

    private ProfileEntity profileEntity;
    private ProfileServiceResponse profileServiceResponse;

    private static final UUID USER_ID = UUID.fromString("11111111-1111-1111-1111-111111111111");
    private static final String NAME = "test";
    private static final String BIO = "test";
    private static final String AVATAR_URL = "oldAvatar.png";

    @BeforeEach
    void setUp() {
        profileEntity = ProfileEntity.builder()
                .id(USER_ID)
                .name(NAME)
                .bio(BIO)
                .avatarUrl(AVATAR_URL)
                .build();


        profileServiceResponse = new ProfileServiceResponse(USER_ID, NAME, BIO, "newAvatar.png");
    }

    @Nested
    class UpdateById {
        @DisplayName("Обновить профиль если пользователь существует и возвращаем его")
        void UpdateProfile_UserExists_ReturnsUser() {
            when(profileRepo.findByUserId(eq(USER_ID))).thenReturn(Optional.of(profileEntity));
            when(profileRepo.save(any(ProfileEntity.class))).thenReturn(profileEntity);
            when(userMapper.toProfileResponseDto(profileEntity)).thenReturn(profileServiceResponse);


            ProfileServiceResponse response = profileServiceImpl.updateProfile(USER_ID, new ProfileUpdateServiceRequest(NAME, BIO));

            assertEquals(profileServiceResponse, response);
            verify(profileRepo).findByUserId(eq(USER_ID));
            verify(profileRepo).save(any(ProfileEntity.class));
            verify(userMapper).toProfileResponseDto(profileEntity);
        }

        @Test
        @DisplayName("Выбрасываем исключение если профиль не найден")
        void updateProfile_ProfileNotFound_ThrowsException() {
            when(profileRepo.findByUserId(eq(USER_ID))).thenReturn(Optional.empty());
            assertThrows(ProfileNotFoundException.class, () -> {
                profileServiceImpl.updateProfile(USER_ID, new ProfileUpdateServiceRequest(NAME, BIO));

            });
            verify(profileRepo).findByUserId(eq(USER_ID));
            verify(profileRepo, never()).save(any(ProfileEntity.class));
            verify(userMapper, never()).toProfileResponseDto(any(ProfileEntity.class));
        }
    }

    @Nested
    class UpdateAvatarByUserId {
        @Test
        @DisplayName("Обновить аватар если пользователь существует и возвращаем его")
        void updateAvatar_UserExists_ReturnsUser() throws Exception {
            when(profileRepo.findByUserId(USER_ID)).thenReturn(Optional.of(profileEntity));
            when(image.getOriginalFilename()).thenReturn("photo.jpg");
            doNothing().when(storageService).deleteFile("oldAvatar.png");
            when(storageService.uploadFile(any(MultipartFile.class), anyString())).thenReturn("newAvatarUrl");
            when(profileRepo.save(any(ProfileEntity.class))).thenReturn(profileEntity);
            when(userMapper.toProfileResponseDto(profileEntity)).thenReturn(profileServiceResponse);

            ProfileServiceResponse result = profileServiceImpl.updateAvatar(USER_ID, image);

            assertEquals(profileServiceResponse, result);
            verify(storageService).deleteFile("oldAvatar.png");
            verify(storageService).uploadFile(eq(image), anyString());
            verify(profileRepo).save(profileEntity);
        }

        @Test
        @DisplayName("Выбрасываем исключение, если профиль пользователя не найден при обновлении аватара")
        void updateAvatar_ProfileNotFound() {
            when(profileRepo.findByUserId(USER_ID)).thenReturn(Optional.empty());

            assertThrows(ProfileNotFoundException.class,
                    () -> profileServiceImpl.updateAvatar(USER_ID, image));
        }

        @Test
        @DisplayName("Выбрасываем RuntimeException при ошибке загрузки файла аватара")
        void updateAvatar_UploadFails() throws Exception {
            when(profileRepo.findByUserId(USER_ID)).thenReturn(Optional.of(profileEntity));
            when(image.getOriginalFilename()).thenReturn("photo.jpg");
            doNothing().when(storageService).deleteFile("oldAvatar.png");
            when(storageService.uploadFile(any(), anyString())).thenThrow(new RuntimeException("Upload error"));

            RuntimeException ex = assertThrows(RuntimeException.class,
                    () -> profileServiceImpl.updateAvatar(USER_ID, image));
            assertTrue(ex.getMessage().contains("Failed to update avatar"));
        }
    }
}



