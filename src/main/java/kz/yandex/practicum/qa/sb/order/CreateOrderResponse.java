package kz.yandex.practicum.qa.sb.order;

import kz.yandex.practicum.qa.sb.common.CommonApiResponse;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@Accessors(chain = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateOrderResponse extends CommonApiResponse {
    String name;
    Order order;

    public Order getOrder() {
        if(order != null && order.getName() != null && order.getName().isBlank()) {
            order.setName(name);
        }
        return order;
    }
}
