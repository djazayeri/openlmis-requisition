package org.openlmis.fulfillment.web;

import org.openlmis.fulfillment.domain.OrderProofOfDelivery;
import org.openlmis.fulfillment.repository.OrderProofOfDeliveryRepository;
import org.openlmis.referencedata.domain.Period;
import org.openlmis.referencedata.domain.Schedule;
import org.openlmis.referencedata.repository.PeriodRepository;
import org.openlmis.referencedata.repository.ScheduleRepository;
import org.openlmis.requisition.domain.Requisition;
import org.openlmis.requisition.repository.RequisitionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.RepositoryRestController;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RepositoryRestController
public class OrderProofOfDeliveryController {

    @Autowired
    private OrderProofOfDeliveryRepository orderProofOfDeliveryRepository;

    @Autowired
    private PeriodRepository periodRepository;

    @Autowired
    private ScheduleRepository scheduleRepository;

    @Autowired
    private RequisitionRepository requisitionRepository;

    @RequestMapping(value = "/orderProofOfDeliveries/{id}/print", method = RequestMethod.GET)
    @ResponseBody
    public ModelAndView print(HttpServletRequest request, HttpServletResponse response,
                              @PathVariable("id") UUID orderProofOfDeliveryId) throws Exception {

        OrderProofOfDelivery orderProofOfDelivery =
                orderProofOfDeliveryRepository.findOne(orderProofOfDeliveryId);
        Requisition requisition = null;

        //tests();

        //requisition = findRequisition(orderProofOfDelivery, requisition);
        Map<Requisition, OrderProofOfDelivery> orderProofOfDeliveries = new HashMap<>();
        orderProofOfDeliveries.put(requisition, orderProofOfDelivery);

        ModelAndView modelAndView = new ModelAndView("pdfView", "orderProofOfDeliveries",
                orderProofOfDeliveries);

        return modelAndView;
    }

    private Requisition findRequisition(OrderProofOfDelivery orderProofOfDelivery,
                                        Requisition requisition) {
        if (orderProofOfDelivery.getOrder().getRequisitionCode() != null) {
            String requisitionCode = orderProofOfDelivery.getOrder().getRequisitionCode();
            UUID requisitionId = UUID.fromString(requisitionCode);
            requisition = requisitionRepository.findOne(requisitionId);
        }
        return requisition;
    }

    private void tests() {
        Schedule schedule = new Schedule();
        schedule.setCode("wewe");
        schedule.setName("wfwefwef");

        scheduleRepository.save(schedule);
        Period period = new Period();
        period.setProcessingSchedule(schedule);
        period.setName("period");
        period.setId(UUID.fromString("123e4567-e89b-12d3-a456-426655440018"));
        period.setStartDate(LocalDate.of(2016, 2, 2));
        period.setEndDate(LocalDate.of(2016, 3, 2));
        periodRepository.save(period);
    }
}
