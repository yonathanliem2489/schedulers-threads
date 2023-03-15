package demo.clean.code.service.core;


import demo.clean.code.service.BaseService;
import demo.clean.code.service.BaseService.BaseRequest;
import demo.clean.code.service.BaseService.BaseResponse;
import java.time.LocalDateTime;
import lombok.Value;

public abstract class BaseCartService<TBaseCartRequest extends BaseRequest, TBaseCartResponse extends BaseResponse> extends
    BaseService<TBaseCartRequest, TBaseCartResponse> {
    Cart cart;

    public BaseCartService(TBaseCartRequest request) {
        super(request);
    }

    @Value
    public static class Cart {
        private String name;

        private String scheduleId;

        private LocalDateTime localDateTime;

        @lombok.Builder(builderClassName = "Builder")
        public Cart(String name, String scheduleId, LocalDateTime localDateTime) {
            this.name = name;
            this.scheduleId = scheduleId;
            this.localDateTime = localDateTime;
        }
    }
}
