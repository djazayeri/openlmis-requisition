package org.openlmis.fulfillment.web;

import org.openlmis.fulfillment.domain.Order;
import org.openlmis.fulfillment.domain.OrderLine;
import org.openlmis.fulfillment.domain.OrderProofOfDelivery;
import org.openlmis.fulfillment.domain.OrderProofOfDeliveryLine;
import org.openlmis.fulfillment.domain.OrderStatus;
import org.openlmis.fulfillment.repository.OrderProofOfDeliveryRepository;
import org.openlmis.hierarchyandsupervision.domain.User;
import org.openlmis.product.domain.Product;
import org.openlmis.referencedata.domain.Facility;
import org.openlmis.referencedata.domain.Program;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.RepositoryRestController;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@RepositoryRestController
public class OrderProofOfDeliveryController {

    @Autowired
    private OrderProofOfDeliveryRepository orderProofOfDeliveryRepository;

//    @Autowired
//    private RequisitionRepository requisitionRepository;

    @RequestMapping(value = "/orderProofOfDeliveries/{id}/print", method = RequestMethod.GET)
    @ResponseBody
    public ModelAndView print(HttpServletRequest request, HttpServletResponse response,
                              @PathVariable("id") UUID orderProofOfDeliveryId) throws Exception {

        //OrderProofOfDelivery orderProofOfDelivery =
                orderProofOfDeliveryRepository.findOne(orderProofOfDeliveryId);

        //to testing - temporary object
        OrderProofOfDelivery orderProofOfDelivery = initializeObjectToTesting();
        //testing - temporary object

        List<OrderProofOfDelivery> orderProofOfDeliveries = new ArrayList<OrderProofOfDelivery>();
        orderProofOfDeliveries.add(orderProofOfDelivery);

        ModelAndView modelAndView = new ModelAndView("pdfView", "orderProofOfDeliveries",
                orderProofOfDeliveries);

        return modelAndView;
    }

    private OrderProofOfDelivery initializeObjectToTesting() {
        OrderProofOfDelivery orderProofOfDelivery = new OrderProofOfDelivery();
        Order order = new Order();
        orderProofOfDelivery.setOrder(order);

//        Requisition requisition = new Requisition();
//        Period period = new Period();
//        period.setStartDate(LocalDate.now());
//        period.setEndDate(LocalDate.now());
//        requisition.setProcessingPeriod(period);
//
//        orderProofOfDelivery.getOrder().setRequisitionCode(requisition.getId().toString());
//
//        String reqCode = orderProofOfDelivery.getOrder().getRequisitionCode();
//        Requisition requisition1 = requisitionRepository.findOne(UUID.fromString(reqCode));
//
//        orderProofOfDelivery.getOrder().setRequisitionCode(requisition1.getId().toString());

        order.setCreatedBy(new User());

        Program program = new Program();
        program.setName("ART");
        order.setProgram(program);

        order.setOrderCode("23");

        Facility facility = new Facility();
        facility.setName("F3020A - Steinbach Hospital");
        order.setReceivingFacility(facility);
        Facility facility1 = new Facility();
        facility1.setName("Manitoba Warehouse");
        order.setSupplyingFacility(facility1);

        order.setCreatedDate(LocalDateTime.now());

        order.setStatus(OrderStatus.ORDERED);

        orderProofOfDelivery.setDeliveredBy("Ben Bensky");
        orderProofOfDelivery.setReceivedDate(new Date("14/07/2016"));
        orderProofOfDelivery.setReceivedBy("Josh Iksky");
        orderProofOfDelivery.setTotalReceivedPacks(1000);
        orderProofOfDelivery.setTotalReturnedPacks(500);
        orderProofOfDelivery.setTotalShippedPacks(1200);


        OrderProofOfDeliveryLine orderProofOfDeliveryLine = new OrderProofOfDeliveryLine();
        OrderLine orderLine = new OrderLine();
        orderLine.setOrderedQuantity(new Long(20));
        Product product = new Product();
        product.setCode("product code");
        product.setFullSupply(Boolean.TRUE);
        product.setDispensingUnit("dispansing unit");
        product.setPrimaryName("primary name of product");
        orderLine.setProduct(product);
        orderProofOfDeliveryLine.setOrderLine(orderLine);
        orderProofOfDeliveryLine.setNotes("abc 123");
        orderProofOfDeliveryLine.setPackToShip(new Long(30));
        orderProofOfDeliveryLine.setQuantityReceived(new Long(100));
        orderProofOfDeliveryLine.setQuantityReturned(new Long(10));
        orderProofOfDeliveryLine.setReplacedProductCode("rep product code");
        orderProofOfDeliveryLine.setQuantityShipped(
                orderProofOfDeliveryLine.getOrderLine().getOrderedQuantity());
        List<OrderProofOfDeliveryLine> list = new ArrayList<OrderProofOfDeliveryLine>();
        list.add(orderProofOfDeliveryLine);
        orderProofOfDelivery.setProfOfDeliveryLineItems(list);
        return orderProofOfDelivery;
    }
}
