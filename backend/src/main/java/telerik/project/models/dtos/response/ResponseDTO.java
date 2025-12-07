package telerik.project.models.dtos.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseDTO<T> {

    private boolean success;
    private String message;
    private T data;
    private Object errors;
    private LocalDateTime timestamp;

    private ResponseDTO(boolean success, String message, T data, Object errors) {
        this.success = success;
        this.message = message;
        this.data = data;
        this.errors = errors;
        this.timestamp = LocalDateTime.now();
    }

    public static <T> ResponseDTO<T> success(String message, T data) {
        return new ResponseDTO<>(true, message, data, null);
    }

    public static <T> ResponseDTO<T> success(T data) {
        return new ResponseDTO<>(true, null, data, null);
    }

    public static ResponseDTO<?> success(String message) {
        return new ResponseDTO<>(true, message, null, null);
    }

    public static ResponseDTO<?> error(String message, Object errors) {
        return new ResponseDTO<>(false, message, null, errors);
    }

    public static ResponseDTO<?> error(String message) {
        return new ResponseDTO<>(false, message, null, null);
    }
}