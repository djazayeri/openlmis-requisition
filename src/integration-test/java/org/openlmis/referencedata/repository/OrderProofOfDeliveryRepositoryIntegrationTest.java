package org.openlmis.referencedata.repository;


import org.junit.Before;
import org.openlmis.fulfillment.domain.Order;
import org.openlmis.fulfillment.domain.OrderProofOfDelivery;
import org.openlmis.fulfillment.domain.OrderStatus;
import org.openlmis.fulfillment.repository.OrderProofOfDeliveryRepository;
import org.openlmis.fulfillment.repository.OrderRepository;
import org.openlmis.hierarchyandsupervision.domain.User;
import org.openlmis.hierarchyandsupervision.repository.UserRepository;
import org.openlmis.referencedata.domain.Facility;
import org.openlmis.referencedata.domain.FacilityType;
import org.openlmis.referencedata.domain.GeographicLevel;
import org.openlmis.referencedata.domain.GeographicZone;
import org.openlmis.referencedata.domain.Program;

import org.springframework.beans.factory.annotation.Autowired;
import java.math.BigDecimal;

public class OrderProofOfDeliveryRepositoryIntegrationTest extends
        BaseCrudRepositoryIntegrationTest<OrderProofOfDelivery> {

    @Autowired
    OrderProofOfDeliveryRepository orderProofOfDeliveryRepository;

    @Autowired
    OrderRepository orderRepository;

    @Autowired
    FacilityRepository facilityRepository;

    @Autowired
    ProgramRepository programRepository;

    @Autowired
    UserRepository userRepository;

    OrderProofOfDeliveryRepository getRepository() {
        return this.orderProofOfDeliveryRepository;
    }

    private String orderProofOfDeliveryString = "OrderProofOfDeliveryRepositoryIntegrationTest";

    private Order order = new Order();

    @Before
    public void setUp() {
        FacilityType facilityType = new FacilityType();
        facilityType.setCode(orderProofOfDeliveryString);

        GeographicLevel level = new GeographicLevel();
        level.setCode(orderProofOfDeliveryString);
        level.setLevelNumber(1);

        GeographicZone geographicZone = new GeographicZone();
        geographicZone.setCode(orderProofOfDeliveryString);
        geographicZone.setLevel(level);

        Facility facility = new Facility();
        facility.setType(facilityType);
        facility.setGeographicZone(geographicZone);
        facility.setCode(orderProofOfDeliveryString);
        facility.setName(orderProofOfDeliveryString);
        facility.setDescription("Test facility");
        facility.setActive(true);
        facility.setEnabled(true);
        facilityRepository.save(facility);

        Program program = new Program();
        program.setCode(orderProofOfDeliveryString);
        programRepository.save(program);

        User user = new User();
        user.setUsername(orderProofOfDeliveryString);
        user.setPassword(orderProofOfDeliveryString);
        user.setFirstName("Test");
        user.setLastName("User");
        userRepository.save(user);

        order.setOrderCode(orderProofOfDeliveryString);
        order.setQuotedCost(new BigDecimal("1.29"));
        order.setStatus(OrderStatus.PICKING);
        order.setProgram(program);
        order.setCreatedBy(user);
        order.setRequestingFacility(facility);
        order.setReceivingFacility(facility);
        order.setSupplyingFacility(facility);
        orderRepository.save(order);
    }

    OrderProofOfDelivery generateInstance() {
        OrderProofOfDelivery orderProofOfDelivery = new OrderProofOfDelivery();
        orderProofOfDelivery.setOrder(order);
        return orderProofOfDelivery;
    }
}
