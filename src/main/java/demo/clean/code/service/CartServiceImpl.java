package demo.clean.code.service;

import demo.clean.code.model.request.cart_service.CreateCartRequest;
import demo.clean.code.model.response.cart_service.CreateCartResponse;
import demo.clean.code.service.api.CartService;
import demo.clean.code.service.core.CreateCartService;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class CartServiceImpl extends ServiceCaller implements CartService {

    @Override
    public Mono<CreateCartResponse> create(CreateCartRequest request) {
        return call(CreateCartService.class, request);
    }

}
