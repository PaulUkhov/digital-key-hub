package unit;

import com.audio.dto.response.UserServiceInfoResponse;
import com.audio.entity.UserEntity;
import com.audio.exception.UserNotFoundException;
import com.audio.mapper.UserMapper;
import com.audio.repository.UserRepository;
import com.audio.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class DigitalKeyHubApplicationUserTests {

    @Test
   void ContextLoads(){

    }
}

