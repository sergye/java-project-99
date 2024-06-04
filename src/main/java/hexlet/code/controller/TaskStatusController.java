package hexlet.code.controller;

import java.util.List;

import hexlet.code.dto.status.TaskStatusCreateDTO;
import hexlet.code.dto.status.TaskStatusDTO;
import hexlet.code.dto.status.TaskStatusUpdateDTO;
import hexlet.code.service.TaskStatusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/task_statuses")
public class TaskStatusController {
    @Autowired
    private TaskStatusService taskStatusService;

    @GetMapping
    ResponseEntity<List<TaskStatusDTO>> index() {
        List<TaskStatusDTO> taskStatuses = taskStatusService.getAll();

        return ResponseEntity.ok()
                .header("X-Total-Count", String.valueOf(taskStatuses.size()))
                .body(taskStatuses);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public TaskStatusDTO show(@PathVariable Long id) {
        return taskStatusService.findById(id);

    }

    @PostMapping("")
    @ResponseStatus(HttpStatus.CREATED)
    public TaskStatusDTO create(@Valid @RequestBody TaskStatusCreateDTO userCreateDTO) {
        return taskStatusService.create(userCreateDTO);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public TaskStatusDTO update(@RequestBody @Valid TaskStatusUpdateDTO userUpdateDTO, @PathVariable Long id) {
        return taskStatusService.update(userUpdateDTO, id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        taskStatusService.delete(id);
    }
}
