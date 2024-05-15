package hexlet.code.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import hexlet.code.dto.UserUpdateDTO;
import hexlet.code.exception.ResourceNotFoundException;
import hexlet.code.model.User;
import hexlet.code.repository.UserRepository;
import hexlet.code.util.ModelGenerator;
import net.datafaker.Faker;
import org.instancio.Instancio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openapitools.jackson.nullable.JsonNullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

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
            a -> a.node("firstName").isEqualTo(user.getFirstName()),
            a -> a.node("lastName").isEqualTo(user.getLastName())
        );
    }

    @Test
    public void testCreate() throws Exception {
        var userData = Instancio.of(modelGenerator.getUserModel()).create();

        mockMvc.perform(post("/users")
            .contentType(MediaType.APPLICATION_JSON)
            .content(om.writeValueAsString(userData)))
            .andExpect(status().isCreated());

        var user = userRepository.findByEmail(userData.getEmail())
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        assertThat(user.getFirstName()).isEqualTo((userData.getFirstName()));
        assertThat(user.getLastName()).isEqualTo((userData.getLastName()));
        assertThat(user.getEmail()).isEqualTo((userData.getEmail()));
        assertThat(user.getPassword()).isEqualTo((userData.getPassword()));
    }

    @Test
    public void testUpdate() throws Exception {
        userRepository.save(user);

        UserUpdateDTO userData = new UserUpdateDTO();
        userData.setFirstName(JsonNullable.of(FAKER.name().firstName()));
        userData.setEmail(JsonNullable.of(FAKER.internet().emailAddress()));

        var request = put("/users/" + user.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .content(om.writeValueAsString(userData));

        mockMvc.perform(request)
            .andExpect(status().isOk());

        var updatedUser = userRepository.findById(user.getId())
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        assertThat(updatedUser.getFirstName()).isEqualTo((userData.getFirstName().get()));
        assertThat(updatedUser.getEmail()).isEqualTo((userData.getEmail().get()));
    }

    @Test
    public void testDelete() throws Exception {
        userRepository.save(user);

        mockMvc.perform(delete("/users/" + user.getId()))
            .andExpect(status().isNoContent());

        assertThat(userRepository.findById(user.getId())).isEmpty();
    }
    // END
}
