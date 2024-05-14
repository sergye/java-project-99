package hexlet.code.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import hexlet.code.exception.ResourceNotFoundException;
import hexlet.code.model.User;
import hexlet.code.repository.UserRepository;
import hexlet.code.util.ModelGenerator;
import net.datafaker.Faker;
import org.instancio.Instancio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;

import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// BEGIN
@SpringBootTest
@AutoConfigureMockMvc
// END
class ApplicationTest {

    private static final Faker FAKER = new Faker();

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ModelGenerator modelGenerator;

    @Autowired
    private ObjectMapper om;

    @Autowired
    private UserRepository userRepository;

    private User user;

    @BeforeEach
    public void setUp() {
        user = Instancio.of(modelGenerator.getUserModel()).create();
    }


    @Test
    public void testWelcomePage() throws Exception {
        var result = mockMvc.perform(get("/welcome"))
            .andExpect(status().isOk())
            .andReturn();

        var body = result.getResponse().getContentAsString();
        assertThat(body).contains("Welcome to Spring!");
    }

    @Test
    public void testIndex() throws Exception {
        userRepository.save(user);
        var result = mockMvc.perform(get("/users"))
            .andExpect(status().isOk())
            .andReturn();

        var body = result.getResponse().getContentAsString();
        assertThatJson(body).isArray();
    }


    // BEGIN
    @Test
    public void testShow() throws Exception {
        userRepository.save(user);

        var result = mockMvc.perform(get("/users/" + user.getId()))
            .andExpect(status().isOk())
            .andReturn();

        var body = result.getResponse().getContentAsString();

        assertThatJson(body).and(
            a -> a.node("email").isEqualTo(user.getEmail()),
            a -> a.node("firstName").isEqualTo(user.getFirstName())
        );
    }

    @Test
    public void testCreate() throws Exception {
        var data = new HashMap<>();
        data.put("title", "vasya@gmail.com");
        data.put("firstName", "Vasya");

        mockMvc.perform(post("/users")
            .contentType(MediaType.APPLICATION_JSON)
            .content(om.writeValueAsString(data)))
            .andExpect(status().isCreated());

        var user = userRepository.findByEmail("vasya@gmail.com")
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        assertThat(user.getFirstName()).isEqualTo(("Vasya"));
    }

    @Test
    public void testUpdate() throws Exception {
        userRepository.save(user);

        var data = new HashMap<>();
        data.put("email", "vasya@gmail.com");
        data.put("firstName", "Vasya");

        var request = put("/users/" + user.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .content(om.writeValueAsString(data));

        mockMvc.perform(request)
            .andExpect(status().isOk());

        user = userRepository.findById(user.getId())
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        assertThat(user.getEmail()).isEqualTo(("vasya@gmail.com"));
        assertThat(user.getFirstName()).isEqualTo(("Vasya"));
    }

    @Test
    public void testDelete() throws Exception {
        userRepository.save(user);

        mockMvc.perform(delete("/users/" + user.getId()))
            .andExpect(status().isOk());

        assertThat(userRepository.findById(user.getId())).isEmpty();
    }
    // END
}
