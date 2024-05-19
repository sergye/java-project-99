package hexlet.code.component;

import lombok.Getter;
import lombok.Setter;

import org.springframework.stereotype.Component;

@Component
@Getter
@Setter
public class DefaultUserProperties {
    private String email = "hexlet@example.com";
    private String password = "qwerty";
}
