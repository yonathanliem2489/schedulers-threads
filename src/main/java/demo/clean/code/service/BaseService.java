package demo.clean.code.service;

import demo.clean.code.service.BaseService.BaseRequest;
import demo.clean.code.service.BaseService.BaseResponse;
import java.io.Serializable;
import java.util.Optional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import reactor.core.publisher.Mono;

public abstract class BaseService<TBaseRequest extends BaseRequest, TBaseResponse extends BaseResponse> {


    @Valid
    protected TBaseRequest request;

    protected TBaseResponse response;

    public BaseService(TBaseRequest request) {
        this.request = request;
    }

    public Mono<TBaseResponse> call() {
        return Mono.just(request)
                .flatMap(this::beforePerform)
                .map(this::storeRequest)
                .flatMap(this::perform)
                .map(this::storeResponse)
                .flatMap(this::afterPerform)
                .map(this::storeResponse);
    }

    protected Mono<TBaseRequest> beforePerform(TBaseRequest request) {
        return Mono.just(request)
                .flatMap(this::validateRequest);
    }

    protected Mono<TBaseRequest> validateRequest(TBaseRequest request) {
        return Mono.just(request);
    }

    protected TBaseRequest storeRequest(TBaseRequest request) {
        this.request = request;
        return request;
    }

    protected abstract Mono<TBaseResponse> perform(TBaseRequest request);

    protected Mono<TBaseResponse> afterPerform(TBaseResponse response) {
        return Mono.just(response);
    }

    protected TBaseResponse storeResponse(TBaseResponse response) {
        this.response = response;
        return this.response;
    }



    @Getter
    @AllArgsConstructor
    public static class BaseRequest {
    }

    public static class BaseResponse implements Serializable {
    }
}
