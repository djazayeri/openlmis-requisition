package org.openlmis.referencedata.repository;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openlmis.Application;
import org.openlmis.fulfillment.domain.Order;
import org.openlmis.fulfillment.domain.OrderLine;
import org.openlmis.fulfillment.domain.OrderProofOfDelivery;
import org.openlmis.fulfillment.domain.OrderProofOfDeliveryLine;
import org.openlmis.fulfillment.domain.OrderStatus;
import org.openlmis.fulfillment.repository.OrderLineRepository;
import org.openlmis.fulfillment.repository.OrderProofOfDeliveryLineRepository;
import org.openlmis.fulfillment.repository.OrderProofOfDeliveryRepository;
import org.openlmis.fulfillment.repository.OrderRepository;
import org.openlmis.hierarchyandsupervision.domain.User;
import org.openlmis.hierarchyandsupervision.repository.UserRepository;
import org.openlmis.product.domain.Product;
import org.openlmis.product.repository.ProductRepository;
import org.openlmis.referencedata.domain.Facility;
import org.openlmis.referencedata.domain.FacilityType;
import org.openlmis.referencedata.domain.GeographicLevel;
import org.openlmis.referencedata.domain.GeographicZone;
import org.openlmis.referencedata.domain.Program;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(Application.class)
@Transactional
public class OrderProofOfDeliveryLineRepositoryIntegrationTest {

    @Autowired
    OrderLineRepository orderLineRepository;

    @Autowired
    OrderProofOfDeliveryRepository orderProofOfDeliveryRepository;

    @Autowired
    OrderProofOfDeliveryLineRepository orderProofOfDeliveryLineRepository;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    OrderRepository orderRepository;

    @Autowired
    ProgramRepository programRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    FacilityRepository facilityRepository;

    String orderProofOfDeliveryLineString = "OrderProofOfDeliveryLineRepositoryIntegrationTest";

    OrderLine orderLine = new OrderLine();

    OrderProofOfDelivery orderProofOfDelivery = new OrderProofOfDelivery();

    @Before
    public void setUp() {
        FacilityType facilityType = new FacilityType();
        facilityType.setCode(orderProofOfDeliveryLineString);

        GeographicLevel level = new GeographicLevel();
        level.setCode(orderProofOfDeliveryLineString);
        level.setLevelNumber(1);

        GeographicZone geographicZone = new GeographicZone();
        geographicZone.setCode(orderProofOfDeliveryLineString);
        geographicZone.setLevel(level);

        Facility facility = new Facility();
        facility.setType(facilityType);
        facility.setGeographicZone(geographicZone);
        facility.setCode(orderProofOfDeliveryLineString);
        facility.setName(orderProofOfDeliveryLineString);
        facility.setDescription("Test facility");
        facility.setActive(true);
        facility.setEnabled(true);
        facilityRepository.save(facility);

        Program program = new Program();
        program.setCode(orderProofOfDeliveryLineString);
        programRepository.save(program);

        User user = new User();
        user.setUsername(orderProofOfDeliveryLineString);
        user.setPassword(orderProofOfDeliveryLineString);
        user.setFirstName("Test");
        user.setLastName("User");
        userRepository.save(user);

        Order order = new Order();
        order.setOrderCode(orderProofOfDeliveryLineString);
        order.setQuotedCost(new BigDecimal("1.29"));
        order.setStatus(OrderStatus.PICKING);
        order.setProgram(program);
        order.setCreatedBy(user);
        order.setRequestingFacility(facility);
        order.setReceivingFacility(facility);
        order.setSupplyingFacility(facility);
        orderRepository.save(order);

        Product product = new Product();
        product.setCode(orderProofOfDeliveryLineString);
        product.setPrimaryName("Product");
        product.setDispensingUnit("unit");
        product.setDosesPerDispensingUnit(10);
        product.setPackSize(1);
        product.setPackRoundingThreshold(0);
        product.setRoundToZero(false);
        product.setActive(true);
        product.setFullSupply(true);
        product.setTracer(false);
        productRepository.save(product);

        orderLine.setOrder(order);
        orderLine.setProduct(product);
        orderLine.setOrderedQuantity(5L);
        orderLineRepository.save(orderLine);

        orderProofOfDelivery.setOrder(order);
        orderProofOfDelivery.setDeliveredBy(orderProofOfDeliveryLineString);
        orderProofOfDelivery.setReceivedBy(orderProofOfDeliveryLineString);
        orderProofOfDelivery.setReceivedDate(new Date());
        orderProofOfDeliveryRepository.save(orderProofOfDelivery);
    }

    @Test
    public void testCreate() {
        OrderProofOfDeliveryLine orderProofOfDeliveryLine = new OrderProofOfDeliveryLine();
        orderProofOfDeliveryLine.setOrderLine(orderLine);
        orderProofOfDeliveryLine.setOrderProofOfDelivery(orderProofOfDelivery);

        Assert.assertNull(orderProofOfDeliveryLine.getId());

        orderProofOfDeliveryLine =
                orderProofOfDeliveryLineRepository.save(orderProofOfDeliveryLine);
        Assert.assertNotNull(orderProofOfDeliveryLine.getId());
    }
}
