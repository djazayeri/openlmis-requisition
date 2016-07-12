package org.openlmis.fulfillment.repository;

import org.openlmis.fulfillment.domain.OrderProofOfDeliveryLine;
import org.springframework.data.repository.Repository;
import java.util.UUID;

public interface OrderLineProofOfDeliveryRepository extends
        Repository<OrderProofOfDeliveryLine, UUID> {

    OrderProofOfDeliveryLine save(OrderProofOfDeliveryLine entity);
    Iterable<OrderProofOfDeliveryLine> save(Iterable<OrderProofOfDeliveryLine> entities);
}
