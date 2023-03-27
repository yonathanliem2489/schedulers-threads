
package demo.clean.code.model.bus.sync.order;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@SuperBuilder(toBuilder = true)
@ToString
@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)
public class OrderData implements Serializable {

    private static final long serialVersionUID = 4513326650288922580L;
    private Long orderId;
    private String orderHash;
    private String accountId;
    private String lang;
    private String currency;
    private String paymentUrl;
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime paymentExpired;
    private List<OrderCartDetailResponse> orderCartDetails;

    @Data
    @Builder(toBuilder = true)
    public static class OrderCartDetailResponse implements Serializable{
        @JsonProperty("itemId")
        private Long itemId;
        @JsonProperty("itemType")
        private String itemType;
        @JsonProperty("vertical")
        private String vertical;
        @JsonProperty("orderCartInsurances")
        private List<OrderCartInsurances> orderCartInsurances;

        public OrderCartDetailResponse(@JsonProperty("itemId") Long itemId, @JsonProperty("item_type") String itemType, @JsonProperty("vertical") String vertical,
            @JsonProperty("orderCartInsurances") List<OrderCartInsurances> orderCartInsurances) {
            this.itemId = itemId;
            this.itemType = itemType;
            this.vertical = vertical;
            this.orderCartInsurances = orderCartInsurances;
        }

        @Data
        @Builder(toBuilder = true)
        public static class OrderCartInsurances implements Serializable{
            private Long parentId;
            private String type;
        }
    }

}
