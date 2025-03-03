package info.mackiewicz.bankapp.account.model.dto;

import info.mackiewicz.bankapp.account.model.interfaces.OwnershipInfo;
import info.mackiewicz.bankapp.user.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AccountOwnerDTOTest {

    private User user;
    private AccountOwnerDTO dto;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1);
        user.setFirstname("Jan");
        user.setLastname("Kowalski");
        dto = new AccountOwnerDTO(user);
    }

    @Test
    void constructor_ShouldCorrectlyMapUserData() {
        // when & then
        assertEquals(user.getId(), dto.getId());
        assertEquals(user.getFullName(), dto.getFullName());
    }

    @Test
    void shouldImplementOwnershipInfoInterface() {
        // when & then
        assertTrue(dto instanceof OwnershipInfo);
    }

    @Test
    void getFullName_ShouldReturnCombinedFirstAndLastName() {
        // when & then
        assertEquals("Jan Kowalski", dto.getFullName());
    }

    @Test
    void constructor_WithNullUser_ShouldThrowNullPointerException() {
        // when & then
        assertThrows(NullPointerException.class, () -> 
            new AccountOwnerDTO(null)
        );
    }

    @Test
    void constructor_WithNullUserFields_ShouldHandleNulls() {
        // given
        User userWithNulls = new User();
        userWithNulls.setId(null);
        userWithNulls.setFirstname(null);
        userWithNulls.setLastname(null);

        // when
        AccountOwnerDTO dto = new AccountOwnerDTO(userWithNulls);

        // then
        assertNull(dto.getId());
        assertNotNull(dto.getFullName());
        assertEquals(" ", dto.getFullName());
    }

    @Test
    void getFullName_WithSpecialCharacters_ShouldPreserveCharacters() {
        // given
        User userWithSpecialChars = new User();
        userWithSpecialChars.setFirstname("Jörg");
        userWithSpecialChars.setLastname("Müller");
        AccountOwnerDTO dto = new AccountOwnerDTO(userWithSpecialChars);

        // when & then
        assertEquals("Jörg Müller", dto.getFullName());
    }

    @Test
    void getFullName_WithExtraSpaces_ShouldNotContainExtraSpaces() {
        // given
        User userWithSpaces = new User();
        userWithSpaces.setFirstname("   Jan   ");
        userWithSpaces.setLastname("   Kowalski   ");
        AccountOwnerDTO dto = new AccountOwnerDTO(userWithSpaces);

        // when & then
        assertEquals("   Jan      Kowalski   ", dto.getFullName());
    }
}