package org.openlmis.fulfillment.domain;

import lombok.Getter;
import lombok.Setter;
import org.openlmis.referencedata.domain.BaseEntity;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "order_proof_of_delivery_lines")
public class OrderProofOfDeliveryLine extends BaseEntity {

    @OneToOne
    @JoinColumn(name = "orderLineId", nullable = false)
    @Getter
    @Setter
    private OrderLine orderLine;

    @ManyToOne
    @JoinColumn(name = "orderProofOfDeliveryId", nullable = false)
    @Getter
    @Setter
    private OrderProofOfDelivery orderProofOfDelivery;

    @Column
    @Getter
    @Setter
    private Long packToShip;

    @Column
    @Getter
    @Setter
    private Long quantityShipped;

    @Column
    @Getter
    @Setter
    private Long quantityReceived;

    @Column
    @Getter
    @Setter
    private Long quantityReturned;

    @Column(columnDefinition = "text")
    @Getter
    @Setter
    private String replacedProductCode;

    @Column(columnDefinition = "text")
    @Getter
    @Setter
    private String notes;
}
