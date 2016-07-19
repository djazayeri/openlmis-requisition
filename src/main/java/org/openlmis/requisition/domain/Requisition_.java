package org.openlmis.requisition.domain;

import org.openlmis.hierarchyandsupervision.domain.User;
import org.openlmis.referencedata.domain.Facility;
import org.openlmis.referencedata.domain.Period;
import org.openlmis.referencedata.domain.Program;

import javax.persistence.metamodel.SetAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import java.time.LocalDateTime;

@StaticMetamodel(Requisition.class)
public class Requisition_ {
  public static volatile SingularAttribute<Requisition, LocalDateTime> createdDate;
  public static volatile SetAttribute<Requisition, RequisitionLine> requisitionLines;
  public static volatile SingularAttribute<Requisition, Facility> facility;
  public static volatile SingularAttribute<Requisition, Program> program;
  public static volatile SingularAttribute<Requisition, Period> processingPeriod;
  public static volatile SingularAttribute<Requisition, RequisitionStatus> requisitionStatus;
  public static volatile SingularAttribute<Requisition, User> creator;
}
