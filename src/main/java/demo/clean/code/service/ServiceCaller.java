package demo.clean.code.service;

import demo.clean.code.exception.InvalidRequestException;
import demo.clean.code.service.BaseService.BaseRequest;
import demo.clean.code.service.BaseService.BaseResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import reactor.core.publisher.Mono;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.Set;

public abstract class ServiceCaller {
    @Autowired
    private ApplicationContext context;

    @Autowired
    private Validator validator;

    protected <TClass extends BaseService, TRequest extends BaseRequest, TResponse extends BaseResponse> Mono<TResponse> call(Class<TClass> aClass, TRequest request) {
        TClass service = context.getBean(aClass, request);
        validateService(request, service);

        return service.call();
    }

    private <TClass extends BaseService, TRequest extends BaseRequest> void validateService(TRequest request, TClass service) {
        Set<ConstraintViolation<TClass>> violations = validator.validate(service);
        if (!violations.isEmpty()) {
            throw new InvalidRequestException(request, violations);
        }
    }
}
