package hexlet.code.util;

import hexlet.code.model.User;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import net.datafaker.Faker;
import org.instancio.Instancio;
import org.instancio.Model;
import org.instancio.Select;
import org.springframework.stereotype.Component;

@Getter
@Component
public class ModelGenerator {
	private static final Faker FAKER = new Faker();

	private Model<User> userModel;

	@PostConstruct
	private void init() {
		userModel = Instancio.of(User.class)
				.ignore(Select.field(User::getId))
				.supply(Select.field(User::getFirstName), () -> FAKER.name().firstName())
				.supply(Select.field(User::getLastName), () -> FAKER.name().lastName())
				.supply(Select.field(User::getEmail), () -> FAKER.internet().emailAddress())
				.supply(Select.field(User::getPassword), () -> FAKER.internet().password(5, 30))
				.toModel();
	}
}