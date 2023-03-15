package demo.clean.code.service.api;

import demo.clean.code.model.request.cart_service.CreateCartRequest;
import demo.clean.code.model.response.cart_service.CreateCartResponse;
import reactor.core.publisher.Mono;

public interface CartService {
    Mono<CreateCartResponse> create(CreateCartRequest request);
}
