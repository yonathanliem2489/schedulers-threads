package demo.clean.code.exception;


import demo.clean.code.service.BaseService.BaseRequest;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import javax.validation.ConstraintViolation;

public class InvalidRequestException extends ServiceException {

    private final String message;

    public InvalidRequestException(BaseRequest request, Set<? extends ConstraintViolation<?>> constraintViolations) {
        super(request, "");
        message = toString(constraintViolations);
    }

    private String toString(Set<? extends ConstraintViolation<?>> constraintViolations) {
        return constraintViolations.stream()
            .filter(Objects::nonNull)
            .map(this::toString)
            .collect(Collectors.joining(", "));
    }

    private String toString(ConstraintViolation cv) {
        String[] arr = cv.getMessageTemplate().split("\\{|\\}");
        int index = Math.max(0, Math.min(1, arr.length - 1));
        String messageCode = arr[index];
        return this.toMessage(messageCode, new Object[]{cv.getPropertyPath()});
    }

    @Override
    public String getMessage() {
        return this.message;
    }
}
