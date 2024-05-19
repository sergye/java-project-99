package hexlet.code.service;

import hexlet.code.dto.UserCreateDTO;
import hexlet.code.dto.UserDTO;
import hexlet.code.dto.UserUpdateDTO;
import hexlet.code.exception.ResourceNotFoundException;
import hexlet.code.mapper.UserMapper;
import hexlet.code.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    @Autowired
    private UserRepository repository;

    @Autowired
    private UserMapper userMapper;

    public List<UserDTO> getAll() {
        var users = repository.findAll();
        return users.stream()
                .map(userMapper::map)
                .toList();
    }

    public UserDTO create(UserCreateDTO userCreateDTO) {
        var user = userMapper.map(userCreateDTO);
        repository.save(user);
        return userMapper.map(user);
    }

    public UserDTO findById(Long id) {
        var user = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Not Found: " + id));
        return userMapper.map(user);
    }

    public UserDTO update(UserUpdateDTO userUpdateDTO, Long id) {
        var user = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Not Found: " + id));
        userMapper.update(userUpdateDTO, user);
        repository.save(user);
        return userMapper.map(user);
    }


    public void delete(Long id) {
        repository.deleteById(id);
    }
}
