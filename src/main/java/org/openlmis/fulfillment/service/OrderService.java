package org.openlmis.fulfillment.service;

import org.openlmis.fulfillment.domain.Order;
import org.openlmis.fulfillment.domain.Order_;
import org.openlmis.referencedata.domain.Facility;
import org.openlmis.referencedata.domain.Period;
import org.openlmis.referencedata.domain.Program;
import org.openlmis.referencedata.domain.Schedule;
import org.openlmis.requisition.domain.Requisition;
import org.openlmis.requisition.domain.Requisition_;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.time.LocalDate;
import java.util.List;

@Service
public class OrderService {

  @PersistenceContext
  EntityManager entityManager;

  /**
   * Finds orders matching all of provided parameters.
   */
  public List<Order> searchOrders(Facility supplyingFacility,
                                      Facility requestingFacility,
                                      Program program,
                                      Period period,
                                      Schedule schedule,
                                      LocalDate startDate,
                                      LocalDate endDate) {
    CriteriaBuilder builder = entityManager.getCriteriaBuilder();
    CriteriaQuery<Order> query = builder.createQuery(Order.class);
    Root<Order> orders = query.from(Order.class);
    Join<Order, Requisition> requisitions = orders.join(Order_.requisition);
    Join<Requisition, Period> periods = requisitions.join(Requisition_.processingPeriod);

    query.select(orders);
    Predicate predicate = builder.conjunction();

    predicate = builder.and(predicate,
                            builder.equal(orders.get("supplyingFacility"), supplyingFacility));
    if (requestingFacility != null) {
      predicate = builder.and(predicate,
                              builder.equal(orders.get("requestingFacility"), requestingFacility));
    }
    if (program != null) {
      predicate = builder.and(predicate, builder.equal(orders.get("program"), program));
    }
    if (period != null) {
      predicate = builder.and(predicate, builder.equal(requisitions.get("processingPeriod"), period));
    }
    if (schedule != null) {
      predicate = builder.and(predicate,
                              builder.equal(periods.get("processingSchedule"), schedule));
    }
    if (startDate != null) {
      predicate = builder.and(predicate,
                              builder.greaterThanOrEqualTo(periods.get("startDate"), startDate));
    }
    if (endDate != null) {
      predicate = builder.and(predicate,
                              builder.lessThanOrEqualTo(periods.get("endDate"), endDate));
    }
    query.where(predicate);
    return entityManager.createQuery(query).getResultList();
  }

}
