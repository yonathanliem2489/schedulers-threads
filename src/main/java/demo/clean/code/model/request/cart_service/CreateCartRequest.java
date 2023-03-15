package demo.clean.code.model.request.cart_service;

import demo.clean.code.service.BaseService.BaseRequest;
import java.util.List;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;

@Getter
public class CreateCartRequest extends BaseRequest {
    @Size(min = 1, max = 2)
    protected String scheduleId;


    protected Boolean phoneVerified;

    @Builder(toBuilder = true)
    public CreateCartRequest(String scheduleId,
        Boolean phoneVerified) {
        super();
        this.scheduleId = scheduleId;
        this.phoneVerified = phoneVerified;
    }
}
