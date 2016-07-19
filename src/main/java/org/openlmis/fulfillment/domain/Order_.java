package org.openlmis.fulfillment.domain;

import org.openlmis.hierarchyandsupervision.domain.User;
import org.openlmis.referencedata.domain.Facility;
import org.openlmis.referencedata.domain.Program;
import org.openlmis.requisition.domain.Requisition;

import javax.persistence.metamodel.SetAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@StaticMetamodel(Order.class)
public class Order_ {
  public static volatile SingularAttribute<Order, Requisition> requisition;
  public static volatile SingularAttribute<Order, LocalDateTime> createdDate;
  public static volatile SingularAttribute<Order, User> createdBy;
  public static volatile SingularAttribute<Order, Program> program;
  public static volatile SingularAttribute<Order, Facility> requestingFacility;
  public static volatile SingularAttribute<Order, Facility> receivingFacility;
  public static volatile SingularAttribute<Order, Facility> supplyingFacility;
  public static volatile SingularAttribute<Order, String> orderCode;
  public static volatile SingularAttribute<Order, OrderStatus> orderStatus;
  public static volatile SingularAttribute<Order, BigDecimal> quotedCost;
  public static volatile SetAttribute<Order, OrderLine> orderLines;
}
