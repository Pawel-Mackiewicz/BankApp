package info.mackiewicz.bankapp.core.user;

import info.mackiewicz.bankapp.core.user.model.User;
import info.mackiewicz.bankapp.core.user.model.dto.UpdateUserRequest;
import info.mackiewicz.bankapp.core.user.model.vo.PhoneNumber;
import info.mackiewicz.bankapp.presentation.auth.dto.UserRegistrationRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class UserMapperTest {

    private UserMapper userMapper;
    private static final String TEST_PHONE = "+48123456789";

    @BeforeEach
    void setUp() {
        userMapper = new UserMapper();
    }

    @Test
    void toUser_shouldMapPhoneNumberCorrectly() {
        // given
        UserRegistrationRequest dto = new UserRegistrationRequest();
        dto.setPhoneNumber(TEST_PHONE);
        dto.setFirstname("Jan");
        dto.setLastname("Kowalski");
        dto.setEmail("jan@example.com");
        dto.setPesel("12345678901");
        dto.setDateOfBirth(LocalDate.of(1990, 1, 1));
        dto.setPassword("Password123!");
        dto.setConfirmPassword("Password123!");

        // when
        User user = userMapper.toUser(dto);

        // then
        assertNotNull(user);
        assertEquals(new PhoneNumber(TEST_PHONE), user.getPhoneNumber());
    }

    @Test
    void updateUserFromRequest_shouldUpdatePhoneNumberCorrectly() {
        // given
        User existingUser = new User();
        existingUser.setPhoneNumber(new PhoneNumber("+48111111111"));
        
        UpdateUserRequest request = new UpdateUserRequest();
        request.setPhoneNumber(TEST_PHONE);

        // when
        User updatedUser = userMapper.updateUserFromRequest(existingUser, request);

        // then
        assertNotNull(updatedUser);
        assertEquals(new PhoneNumber(TEST_PHONE), updatedUser.getPhoneNumber());
    }

    @Test
    void updateUserFromRequest_shouldNotUpdatePhoneNumberWhenNull() {
        // given
        String originalPhone = "+48111111111";
        User existingUser = new User();
        existingUser.setPhoneNumber(new PhoneNumber(originalPhone));
        
        UpdateUserRequest request = new UpdateUserRequest();
        request.setPhoneNumber(null);

        // when
        User updatedUser = userMapper.updateUserFromRequest(existingUser, request);

        // then
        assertNotNull(updatedUser);
        assertEquals(new PhoneNumber(originalPhone), updatedUser.getPhoneNumber());
    }
}