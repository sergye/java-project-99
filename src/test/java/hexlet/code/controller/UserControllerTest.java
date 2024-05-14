package hexlet.code.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import hexlet.code.exception.ResourceNotFoundException;
import hexlet.code.model.User;
import hexlet.code.repository.UserRepository;
import net.datafaker.Faker;
import org.instancio.Instancio;
import org.instancio.Select;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;

import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// BEGIN
@SpringBootTest
@AutoConfigureMockMvc
// END
class ApplicationTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private Faker faker;

	@Autowired
	private ObjectMapper om;

	@Autowired
	private UserRepository userRepository;


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
		var result = mockMvc.perform(get("/users"))
				.andExpect(status().isOk())
				.andReturn();

		var body = result.getResponse().getContentAsString();
		assertThatJson(body).isArray();
	}


	private User generateUser() {
		return Instancio.of(User.class)
				.ignore(Select.field(User::getId))
				.supply(Select.field(User::getTitle), () -> faker.lorem().word())
				.supply(Select.field(User::getDescription), () -> faker.lorem().paragraph())
				.create();
	}

	// BEGIN
	@Test
	public void testShow() throws Exception {
		var user = generateUser();
		userRepository.save(user);

		var result = mockMvc.perform(get("/users/" + user.getId()))
				.andExpect(status().isOk())
				.andReturn();

		var body = result.getResponse().getContentAsString();

		assertThatJson(body).and(
				a -> a.node("title").isEqualTo(user.getTitle()),
				a -> a.node("description").isEqualTo(user.getDescription())
		);
	}

	@Test
	public void testCreate() throws Exception {
		var data = new HashMap<>();
		data.put("title", "Shopping");
		data.put("description", "Go to supermarket");

		mockMvc.perform(post("/users")
						.contentType(MediaType.APPLICATION_JSON)
						.content(om.writeValueAsString(data)))
				.andExpect(status().isCreated());

		var user = userRepository.findByTitle("Shopping")
				.orElseThrow(() -> new ResourceNotFoundException("User not found"));

		assertThat(user.getDescription()).isEqualTo(("Go to supermarket"));
	}

	@Test
	public void testUpdate() throws Exception {
		var user = generateUser();
		userRepository.save(user);

		var data = new HashMap<>();
		data.put("title", "Shopping");
		data.put("description", "Go to supermarket");

		var request = put("/users/" + user.getId())
				.contentType(MediaType.APPLICATION_JSON)
				.content(om.writeValueAsString(data));

		mockMvc.perform(request)
				.andExpect(status().isOk());

		user = userRepository.findById(user.getId())
				.orElseThrow(() -> new ResourceNotFoundException("User not found"));

		assertThat(user.getTitle()).isEqualTo(("Shopping"));
		assertThat(user.getDescription()).isEqualTo(("Go to supermarket"));
	}

	@Test
	public void testDelete() throws Exception {
		var user = generateUser();
		userRepository.save(user);

		mockMvc.perform(delete("/users/" + user.getId()))
				.andExpect(status().isOk());

		assertThat(userRepository.findById(user.getId())).isEmpty();
	}
	// END
}