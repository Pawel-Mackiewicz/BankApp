package info.mackiewicz.bankapp.Controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import info.mackiewicz.bankapp.controller.UserController;
import info.mackiewicz.bankapp.model.User;
import info.mackiewicz.bankapp.service.UserService;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(UserController.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    // POST /api/users - pozytywny scenariusz tworzenia użytkownika
    @Test
    public void testCreateUserSuccess() throws Exception {
        User user = new User();
        user.setName("Jan Kowalski");
        // Załóżmy, że id jest przypisywane automatycznie – ustawiamy je przy pomocy ReflectionTestUtils
        ReflectionTestUtils.setField(user, "id", 1);

        when(userService.createUser(any(User.class))).thenReturn(user);

        String requestBody = objectMapper.writeValueAsString(user);

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Jan Kowalski"));
    }

    // GET /api/users/{id} - pozytywny scenariusz pobrania użytkownika
    @Test
    public void testGetUserByIdSuccess() throws Exception {
        User user = new User();
        user.setId(1);
        user.setName("Jan Kowalski");

        when(userService.getUserById(1)).thenReturn(user);

        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Jan Kowalski"));
    }

    // GET /api/users/{id} - przypadek błędny: użytkownik nie znaleziony
    @Test
    public void testGetUserByIdNotFound() throws Exception {
        when(userService.getUserById(99)).thenThrow(new RuntimeException("User not found"));

        mockMvc.perform(get("/api/users/99"))
                .andExpect(status().isNotFound());
    }

    // GET /api/users - pobranie wszystkich użytkowników
    @Test
    public void testGetAllUsers() throws Exception {
        User user1 = new User();
        user1.setId(1);
        user1.setName("Jan Kowalski");

        User user2 = new User();
        user2.setId(2);
        user2.setName("Anna Nowak");

        List<User> users = Arrays.asList(user1, user2);

        when(userService.getAllUsers()).thenReturn(users);

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Jan Kowalski"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].name").value("Anna Nowak"));
    }

    // PUT /api/users/{id} - pozytywny scenariusz aktualizacji użytkownika
    @Test
    public void testUpdateUserSuccess() throws Exception {
        User user = new User();
        user.setName("Jan Nowak");
        // W kontrolerze metoda updateUser ustawia id z path variable, więc symulujemy zwrócony obiekt z id = 1
        user.setId(1);

        when(userService.updateUser(any(User.class))).thenReturn(user);

        String requestBody = objectMapper.writeValueAsString(user);

        mockMvc.perform(put("/api/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Jan Nowak"));
    }

    // PUT /api/users/{id} - przypadek błędny: błąd przy aktualizacji (np. użytkownik nie znaleziony)
    @Test
    public void testUpdateUserError() throws Exception {
        User user = new User();
        user.setName("Jan Nowak");
        // Przyjmujemy, że aktualizacja użytkownika, który nie istnieje, rzuca wyjątek
        when(userService.updateUser(any(User.class))).thenThrow(new RuntimeException("User not found"));

        String requestBody = objectMapper.writeValueAsString(user);

        mockMvc.perform(put("/api/users/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isNotFound());
    }

    // DELETE /api/users/{id} - pozytywny scenariusz usunięcia użytkownika
    @Test
    public void testDeleteUserSuccess() throws Exception {
        // Jeśli metoda deleteUser nie rzuca wyjątku, usunięcie przebiega poprawnie
        mockMvc.perform(delete("/api/users/1"))
                .andExpect(status().isNoContent());
    }

    // DELETE /api/users/{id} - przypadek błędny: użytkownik nie znaleziony przy usuwaniu
    @Test
    public void testDeleteUserNotFound() throws Exception {
        doThrow(new RuntimeException("User not found"))
                .when(userService).deleteUser(99);

        mockMvc.perform(delete("/api/users/99"))
                .andExpect(status().isNotFound());
    }
}
