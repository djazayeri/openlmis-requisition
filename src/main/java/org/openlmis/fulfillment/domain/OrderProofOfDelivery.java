package org.openlmis.fulfillment.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.openlmis.referencedata.domain.BaseEntity;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import java.util.Date;
import java.util.Set;

@Entity
@Table(name = "orders_pod")
@NoArgsConstructor
public class OrderProofOfDelivery extends BaseEntity {

    @OneToOne
    @JoinColumn(name = "orderId", nullable = false)
    @Getter
    @Setter
    private Order order;

    @OneToMany(mappedBy = "orderProofOfDelivery")
    @Getter
    private Set<OrderProofOfDeliveryLine> profOfDeliveryLineItems;

    @Column
    @Getter
    @Setter
    private Integer totalShippedPacks;

    @Column
    @Getter
    @Setter
    private Integer totalReceivedPacks;

    @Column
    @Getter
    @Setter
    private Integer totalReturnedPacks;

    @Column(nullable = false, columnDefinition = "text")
    @Getter
    @Setter
    private String deliveredBy;

    @Column(nullable = false, columnDefinition = "text")
    @Getter
    @Setter
    private String receivedBy;

    @Column(nullable = false)
    @Getter
    @Setter
    private Date receivedDate;

}
