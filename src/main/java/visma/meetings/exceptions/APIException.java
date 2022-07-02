package visma.meetings.exceptions;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Setter
@Getter
@AllArgsConstructor
public class APIException extends Exception {
    private String message;
    private HttpStatus statusCode;
}
