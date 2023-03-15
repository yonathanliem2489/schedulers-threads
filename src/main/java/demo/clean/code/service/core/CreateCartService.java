package demo.clean.code.service.core;

import demo.clean.code.model.request.cart_service.CreateCartRequest;
import demo.clean.code.model.response.cart_service.CreateCartResponse;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@Slf4j
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class CreateCartService extends BaseCartService<CreateCartRequest, CreateCartResponse> {


    public CreateCartService(CreateCartRequest request) {
        super(request);
    }

    @Override
    protected Mono<CreateCartResponse> perform(CreateCartRequest createCartRequest) {
        return Mono.just(new CreateCartResponse())
            .flatMap(resp -> addNewProfile(createCartRequest, resp))
            .flatMap(response -> buildResponse(createCartRequest, response))
        ;
    }

    private Mono<? extends CreateCartResponse> buildResponse(CreateCartRequest createCartRequest,
        CreateCartResponse response) {
        return Mono.fromCallable(() -> {
            log.info("newSingle receive request");
            try {
                Thread.sleep(3000L);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            return true;
        })
        .doOnNext(result -> {
            log.info("request map scheduleId {} variable result scheduleId {}",
                createCartRequest.getScheduleId(),
                Objects.nonNull(this.cart) ? this.cart.getScheduleId() : null);
        })
        .then(Mono.just(response));
    }

    private Mono<CreateCartResponse> addNewProfile(CreateCartRequest createCartRequest,
        CreateCartResponse createCartResponse) {

        this.cart = Cart.builder()
            .scheduleId(createCartRequest.getScheduleId())
            .build();

        return Mono.just(createCartResponse);
    }

}
