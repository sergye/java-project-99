package hexlet.code.component;

import hexlet.code.dto.user.UserCreateDTO;
import hexlet.code.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;


import hexlet.code.model.User;
import hexlet.code.repository.UserRepository;

import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class DataInitializer implements ApplicationRunner {
    @Autowired
    private final UserRepository userRepository;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private DefaultUserProperties defaultUserProperties;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        createDefaultUser();
    }

    private void createDefaultUser() {
        if (userRepository.findAll().isEmpty()) {
            String email = defaultUserProperties.getEmail();

            if (userRepository.findByEmail(email).isEmpty()) {
                UserCreateDTO userData = new UserCreateDTO();

                userData.setFirstName("Ivan");
                userData.setLastName("Ivanov");
                userData.setEmail(email);

                String password = defaultUserProperties.getPassword();
                String passwordDigest = passwordEncoder.encode(password);

                userData.setPassword(passwordDigest);

                User user = userMapper.map(userData);
                userRepository.save(user);
            }
        }
    }
}
