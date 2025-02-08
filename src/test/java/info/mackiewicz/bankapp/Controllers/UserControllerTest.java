package info.mackiewicz.bankapp.Controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import info.mackiewicz.bankapp.controller.GlobalExceptionHandler;
import info.mackiewicz.bankapp.controller.UserController;
import info.mackiewicz.bankapp.exception.UserNotFoundException;
import info.mackiewicz.bankapp.model.User;
import info.mackiewicz.bankapp.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(userController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void testCreateUser() throws Exception {
        User user = new User();
        ReflectionTestUtils.setField(user, "id", 1);
        user.setPESEL("12345678901");
        user.setName("John Doe");

        when(userService.createUser(org.mockito.ArgumentMatchers.any(User.class)))
                .thenReturn(user);

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.pesel", is("12345678901")))
                .andExpect(jsonPath("$.name", is("John Doe")));
    }

    @Test
    void testGetUserById() throws Exception {
        User user = new User();
        ReflectionTestUtils.setField(user, "id", 1);
        user.setPESEL("12345678901");
        user.setName("Alice");

        when(userService.getUserById(1))
                .thenReturn(user);

        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.pesel", is("12345678901")))
                .andExpect(jsonPath("$.name", is("Alice")));
    }

    @Test
    void testGetUserById_NotFound() throws Exception {
        when(userService.getUserById(anyInt()))
                .thenThrow(new UserNotFoundException("User with ID 999 not found"));

        mockMvc.perform(get("/api/users/999"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("User with ID 999 not found"));
    }

    @Test
    void testGetAllUsers() throws Exception {
        User user1 = new User();
        ReflectionTestUtils.setField(user1, "id", 1);
        user1.setPESEL("11111111111");
        user1.setName("User One");

        User user2 = new User();
        ReflectionTestUtils.setField(user2, "id", 2);
        user2.setPESEL("22222222222");
        user2.setName("User Two");

        List<User> users = Arrays.asList(user1, user2);

        when(userService.getAllUsers())
                .thenReturn(users);

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", is(users.size())))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[1].id", is(2)));
    }

    @Test
    void testUpdateUser() throws Exception {
        User user = new User();
        ReflectionTestUtils.setField(user, "id", 1);
        user.setPESEL("12345678901");
        user.setName("Updated Name");

        when(userService.updateUser(org.mockito.ArgumentMatchers.any(User.class)))
                .thenReturn(user);

        // W teście symulujemy, że wysyłamy JSON z danymi
        User requestBody = new User();
        requestBody.setPESEL("12345678901");
        requestBody.setName("Updated Name");

        mockMvc.perform(put("/api/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestBody)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.pesel", is("12345678901")))
                .andExpect(jsonPath("$.name", is("Updated Name")));
    }

    @Test
    void testDeleteUser() throws Exception {
        mockMvc.perform(delete("/api/users/1"))
                .andExpect(status().isNoContent());
    }
}
