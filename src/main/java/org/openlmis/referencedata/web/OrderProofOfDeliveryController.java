package org.openlmis.referencedata.web;


import org.openlmis.fulfillment.domain.OrderProofOfDelivery;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

public class OrderProofOfDeliveryController {

    @RequestMapping(value = "/pods/{id}/print", method = RequestMethod.GET)
    public void print(OrderProofOfDelivery orderProofOfDeliveryToPrint) {

        //TODO

    }
}
