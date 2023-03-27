
package demo.clean.code.model.bus.sync.order;

import java.io.Serializable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@ToString
@Getter
@NoArgsConstructor
public class OrderDetail implements Serializable {

    private static final long serialVersionUID = 5976816675579510247L;
    private String localOrderId;
    private String orderDetailId;

}
