package hexlet.code.dto.task;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.openapitools.jackson.nullable.JsonNullable;

@Setter
@Getter
public class TaskCreateDTO {
    private Long index;

    @JsonProperty("assignee_id")
    private JsonNullable<Long> assigneeId;

    @NotBlank
    private String title;

    private JsonNullable<String> content;
    private JsonNullable<String> status;
}
