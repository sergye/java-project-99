package hexlet.code.component;

import hexlet.code.dto.status.TaskStatusCreateDTO;
import hexlet.code.dto.user.UserCreateDTO;
import hexlet.code.mapper.TaskStatusMapper;
import hexlet.code.mapper.UserMapper;
import hexlet.code.repository.TaskStatusRepository;
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
    private TaskStatusRepository taskStatusRepository;

    @Autowired
    private TaskStatusMapper taskStatusMapper;

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

    private void createDefaultTaskStatuses() {
        if (taskStatusRepository.findAll().isEmpty()) {
            TaskStatusCreateDTO draftDTO = new TaskStatusCreateDTO();

            draftDTO.setName("Draft");
            draftDTO.setSlug("draft");

            TaskStatusCreateDTO toViewDTO = new TaskStatusCreateDTO();

            toViewDTO.setName("To view");
            toViewDTO.setSlug("to_review");

            TaskStatusCreateDTO toBeFixedDTO = new TaskStatusCreateDTO();

            toBeFixedDTO.setName("To be fixed");
            toBeFixedDTO.setSlug("to_be_fixed");

            TaskStatusCreateDTO toPublishDTO = new TaskStatusCreateDTO();

            toPublishDTO.setName("To publish");
            toPublishDTO.setSlug("to_publish");

            TaskStatusCreateDTO publishedDTO = new TaskStatusCreateDTO();

            publishedDTO.setName("Published");
            publishedDTO.setSlug("published");

            taskStatusRepository.save(taskStatusMapper.map(draftDTO));
            taskStatusRepository.save(taskStatusMapper.map(toViewDTO));
            taskStatusRepository.save(taskStatusMapper.map(toBeFixedDTO));
            taskStatusRepository.save(taskStatusMapper.map(toPublishDTO));
            taskStatusRepository.save(taskStatusMapper.map(publishedDTO));
        }
    }
}
