package hexlet.code.service;

import hexlet.code.dto.task.TaskCreateDTO;
import hexlet.code.dto.task.TaskDTO;
import hexlet.code.dto.task.TaskUpdateDTO;
import hexlet.code.exception.ResourceAlreadyExistsException;
import hexlet.code.exception.ResourceNotFoundException;
import hexlet.code.mapper.TaskMapper;
import hexlet.code.model.Task;
import hexlet.code.model.TaskStatus;
import hexlet.code.model.User;
import hexlet.code.repository.TaskRepository;
import hexlet.code.repository.TaskStatusRepository;
import hexlet.code.repository.UserRepository;
import org.openapitools.jackson.nullable.JsonNullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskService {
    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private TaskStatusRepository taskStatusRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TaskMapper taskMapper;

    public List<TaskDTO> getAll() {
        return taskRepository.findAll().stream()
                .map(taskMapper::map)
                .toList();
    }

    public TaskDTO create(TaskCreateDTO taskCreateDTO) {
        if (taskRepository.findByName(taskCreateDTO.getTitle()).isPresent()) {
            throw new ResourceAlreadyExistsException("Task already exists");
        }

        var task = taskMapper.map(taskCreateDTO);
        setTaskData(task, taskCreateDTO);
        taskRepository.save(task);
        return taskMapper.map(task);
    }

    public TaskDTO findById(Long id) {
        var task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not Found: " + id));
        return taskMapper.map(task);
    }

    public TaskDTO update(TaskUpdateDTO taskUpdateDTO, Long id) {
        var task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not Found: " + id));

        setTaskData(task, taskUpdateDTO);
        taskMapper.update(taskUpdateDTO, task);
        taskRepository.save(task);
        return taskMapper.map(task);
    }


    public void delete(Long id) {
        taskRepository.deleteById(id);
    }

    private void setTaskData(Task task, TaskCreateDTO taskCreateDTO) {
        setTaskData(task, taskCreateDTO.getAssigneeId(), taskCreateDTO.getStatus());
    }

    private void setTaskData(Task task, TaskUpdateDTO taskUpdateDTO) {
        setTaskData(task, taskUpdateDTO.getAssigneeId(), taskUpdateDTO.getStatus());
    }


    private void setTaskData(Task task, JsonNullable<Long> assigneeId, JsonNullable<String> status) {

        if (assigneeId != null) {
            User newAssignee = null;

            if (assigneeId.get() != null && assigneeId.get() != 0) {
                newAssignee = userRepository.findById(assigneeId.get())
                        .orElseThrow(() -> new ResourceNotFoundException("Assignee not found"));
            }

            task.setAssignee(newAssignee);
        }

        if (status != null) {
            TaskStatus newStatus = null;

            if (status.get() != null) {
                newStatus = taskStatusRepository.findBySlug(status.get())
                        .orElseThrow(() -> new ResourceNotFoundException("Status not found"));
            }

            task.setTaskStatus(newStatus);
        }
    }
}
