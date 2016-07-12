package org.openlmis.fulfillment.repository;

import org.openlmis.fulfillment.domain.OrderProofOfDelivery;
import org.springframework.data.repository.PagingAndSortingRepository;
import java.util.UUID;

public interface OrderProofOfDeliveryRepository extends
        PagingAndSortingRepository<OrderProofOfDelivery, UUID> {
}

