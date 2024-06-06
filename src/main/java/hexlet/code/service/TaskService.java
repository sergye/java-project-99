package hexlet.code.service;

import hexlet.code.dto.task.TaskCreateDTO;
import hexlet.code.dto.task.TaskDTO;
import hexlet.code.dto.task.TaskUpdateDTO;
import hexlet.code.exception.ResourceAlreadyExistsException;
import hexlet.code.exception.ResourceNotFoundException;
import hexlet.code.mapper.TaskMapper;
import hexlet.code.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskService {
    @Autowired
    private TaskRepository repository;

    @Autowired
    private TaskMapper taskMapper;

    public List<TaskDTO> getAll() {
        return repository.findAll().stream()
                .map(taskMapper::map)
                .toList();
    }

    public TaskDTO create(TaskCreateDTO taskCreateDTO) {
        if (repository.findByName(taskCreateDTO.getTitle()).isPresent()) {
            throw new ResourceAlreadyExistsException("Task already exists");
        }

        var task = taskMapper.map(taskCreateDTO);
        repository.save(task);
        return taskMapper.map(task);
    }

    public TaskDTO findById(Long id) {
        var task = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not Found: " + id));
        return taskMapper.map(task);
    }

    public TaskDTO update(TaskUpdateDTO taskUpdateDTO, Long id) {
        var task = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not Found: " + id));
        taskMapper.update(taskUpdateDTO, task);
        repository.save(task);
        return taskMapper.map(task);
    }


    public void delete(Long id) {
        repository.deleteById(id);
    }
}
