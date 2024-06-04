package hexlet.code.service;

import hexlet.code.dto.status.TaskStatusCreateDTO;
import hexlet.code.dto.status.TaskStatusDTO;
import hexlet.code.dto.status.TaskStatusUpdateDTO;
import hexlet.code.exception.ResourceAlreadyExistsException;
import hexlet.code.exception.ResourceNotFoundException;
import hexlet.code.mapper.TaskStatusMapper;
import hexlet.code.repository.TaskStatusRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskStatusService {
    @Autowired
    private TaskStatusRepository repository;

    @Autowired
    private TaskStatusMapper taskStatusMapper;

    public List<TaskStatusDTO> getAll() {
        return repository.findAll().stream()
                .map(taskStatusMapper::map)
                .toList();
    }

    public TaskStatusDTO create(TaskStatusCreateDTO taskStatusCreateDTO) {
        if (repository.findByName(taskStatusCreateDTO.getName()).isPresent()) {
            throw new ResourceAlreadyExistsException("Status already exists");
        }

        if (repository.findBySlug(taskStatusCreateDTO.getSlug()).isPresent()) {
            throw new ResourceAlreadyExistsException("Status already exists");
        }

        var taskStatus = taskStatusMapper.map(taskStatusCreateDTO);
        repository.save(taskStatus);
        return taskStatusMapper.map(taskStatus);
    }

    public TaskStatusDTO findById(Long id) {
        var taskStatus = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Status not Found: " + id));
        return taskStatusMapper.map(taskStatus);
    }

    public TaskStatusDTO update(TaskStatusUpdateDTO taskStatusUpdateDTO, Long id) {
        var taskStatus = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Status not Found: " + id));
        taskStatusMapper.update(taskStatusUpdateDTO, taskStatus);
        repository.save(taskStatus);
        return taskStatusMapper.map(taskStatus);
    }


    public void delete(Long id) {
        repository.deleteById(id);
    }
}
