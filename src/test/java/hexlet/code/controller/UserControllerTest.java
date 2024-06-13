package hexlet.code.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import hexlet.code.component.DefaultUserProperties;
import hexlet.code.dto.user.UserUpdateDTO;
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
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.nio.charset.StandardCharsets;
import java.util.Optional;

import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;



@SpringBootTest
@AutoConfigureMockMvc

class UserControllerTest {

    private static final Faker FAKER = new Faker();

    @Autowired
    private WebApplicationContext wac;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ModelGenerator modelGenerator;

    @Autowired
    private ObjectMapper om;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DefaultUserProperties defaultUserProperties;

    private JwtRequestPostProcessor token;

    private User user;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac)
                .defaultResponseCharacterEncoding(StandardCharsets.UTF_8)
                .apply(springSecurity())
                .build();

        user = Instancio.of(modelGenerator.getUserModel()).create();
        token = jwt().jwt(builder -> builder.subject(user.getEmail()));
        userRepository.save(user);
    }

    @Test
    public void testUnauthenticatedAccess() throws Exception {
        mockMvc.perform(get("/api/users"))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized())
                .andReturn();
    }

    @Test
    public void testIndex() throws Exception {
        var result = mockMvc.perform(get("/api/users").with(token))
                .andExpect(status().isOk())
                .andReturn();

        var body = result.getResponse().getContentAsString();
        assertThatJson(body).isArray();
    }

    @Test
    public void testShow() throws Exception {

        var result = mockMvc.perform(get("/api/users/" + user.getId()).with(token))
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

        var request = post("/api/users")
                .with(token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(userData));

        mockMvc.perform(request)
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

        UserUpdateDTO userData = new UserUpdateDTO();
        userData.setFirstName(JsonNullable.of(FAKER.name().firstName()));
        userData.setEmail(JsonNullable.of(FAKER.internet().emailAddress()));

        var request = put("/api/users/" + user.getId())
            .with(token)
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

        mockMvc.perform(delete("/api/users/" + user.getId()).with(token))
            .andExpect(status().isNoContent());

        assertThat(userRepository.findById(user.getId())).isEmpty();
    }

    @Test
    public void testCreateDefaultUser() {
        String email = defaultUserProperties.getEmail();
        Optional<User> userOptional = userRepository.findByEmail(email);

        assertThat(userOptional).isPresent();
        User user = userOptional.get();

        assertThat(user.getEmail()).isEqualTo(defaultUserProperties.getEmail());
        assertThat(user.getPassword()).isNotEqualTo(defaultUserProperties.getPassword());
    }
}
