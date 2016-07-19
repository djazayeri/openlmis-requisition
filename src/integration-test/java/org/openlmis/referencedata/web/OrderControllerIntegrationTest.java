package org.openlmis.referencedata.web;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openlmis.Application;
import org.openlmis.fulfillment.domain.Order;
import org.openlmis.fulfillment.domain.OrderLine;
import org.openlmis.fulfillment.domain.OrderStatus;
import org.openlmis.fulfillment.repository.OrderLineRepository;
import org.openlmis.fulfillment.repository.OrderRepository;
import org.openlmis.hierarchyandsupervision.domain.User;
import org.openlmis.hierarchyandsupervision.repository.UserRepository;
import org.openlmis.product.domain.Product;
import org.openlmis.product.repository.ProductRepository;
import org.openlmis.referencedata.domain.Facility;
import org.openlmis.referencedata.domain.FacilityType;
import org.openlmis.referencedata.domain.GeographicLevel;
import org.openlmis.referencedata.domain.GeographicZone;
import org.openlmis.referencedata.domain.Period;
import org.openlmis.referencedata.domain.Program;
import org.openlmis.referencedata.domain.Schedule;
import org.openlmis.referencedata.domain.Stock;
import org.openlmis.referencedata.domain.StockInventory;
import org.openlmis.referencedata.repository.FacilityRepository;
import org.openlmis.referencedata.repository.FacilityTypeRepository;
import org.openlmis.referencedata.repository.GeographicLevelRepository;
import org.openlmis.referencedata.repository.GeographicZoneRepository;
import org.openlmis.referencedata.repository.PeriodRepository;
import org.openlmis.referencedata.repository.ProgramRepository;
import org.openlmis.referencedata.repository.ScheduleRepository;
import org.openlmis.referencedata.repository.StockInventoryRepository;
import org.openlmis.referencedata.repository.StockRepository;
import org.openlmis.requisition.domain.Requisition;
import org.openlmis.requisition.domain.RequisitionStatus;
import org.openlmis.requisition.repository.RequisitionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.Iterator;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(Application.class)
@WebIntegrationTest("server.port:8080")
public class OrderControllerIntegrationTest {

  @Autowired
  OrderRepository orderRepository;

  @Autowired
  OrderLineRepository orderLineRepository;

  @Autowired
  UserRepository userRepository;

  @Autowired
  RequisitionRepository requisitionRepository;

  @Autowired
  ProgramRepository programRepository;

  @Autowired
  FacilityRepository facilityRepository;

  @Autowired
  PeriodRepository periodRepository;

  @Autowired
  ScheduleRepository scheduleRepository;

  @Autowired
  ProductRepository productRepository;

  @Autowired
  GeographicZoneRepository geographicZoneRepository;

  @Autowired
  GeographicLevelRepository geographicLevelRepository;

  @Autowired
  FacilityTypeRepository facilityTypeRepository;

  @Autowired
  private StockRepository stockRepository;

  @Autowired
  private StockInventoryRepository stockInventoryRepository;

  private static final String RESOURCE_FINALIZE_URL = System.getenv("BASE_URL") + "/api/orders/finalizeOrder";

  private static final String RESOURCE_URL = System.getenv("BASE_URL") + "/api/orders";

  private Order order = new Order();
  private Product firstProduct = new Product();
  private Product secondProduct = new Product();
  private StockInventory stockInventory = new StockInventory();

  private Order order1 = new Order();
  private Order order2 = new Order();
  private OrderLine orderLine1 = new OrderLine();
  private OrderLine orderLine2 = new OrderLine();
  private OrderLine orderLine3 = new OrderLine();
  private OrderLine orderLine4 = new OrderLine();
  private User user = new User();
  private Requisition requisition1 = new Requisition();
  private Requisition requisition2 = new Requisition();
  private Program program1 = new Program();
  private Program program2 = new Program();
  private Facility facility1 = new Facility();
  private Facility facility2 = new Facility();
  private Period period1 = new Period();
  private Period period2 = new Period();
  private Schedule schedule1 = new Schedule();
  private Schedule schedule2 = new Schedule();
  private Product product1 = new Product();
  private Product product2 = new Product();
  private GeographicZone geographicZone1 = new GeographicZone();
  private GeographicZone geographicZone2 = new GeographicZone();
  private GeographicLevel geographicLevel1 = new GeographicLevel();
  private GeographicLevel geographicLevel2 = new GeographicLevel();
  private FacilityType facilityType1 = new FacilityType();
  private FacilityType facilityType2 = new FacilityType();

  @Before
  public void setUp() {
    cleanUp();

    stockInventory.setName("stockInventoryName");
    stockInventoryRepository.save(stockInventory);

    GeographicLevel geographicLevel = new GeographicLevel();
    geographicLevel.setCode("geographicLevelCode");
    geographicLevel.setLevelNumber(1);

    FacilityType facilityType = new FacilityType();
    facilityType.setCode("facilityTypeCode");

    GeographicZone geographicZone = new GeographicZone();
    geographicZone.setCode("geographicZoneCode");
    geographicZone.setLevel(geographicLevel);

    Facility facility = new Facility();
    facility.setType(facilityType);
    facility.setGeographicZone(geographicZone);
    facility.setCode("facilityCode");
    facility.setName("facilityName");
    facility.setDescription("facilityDescription");
    facility.setActive(true);
    facility.setEnabled(true);
    facility.setStockInventory(stockInventory);
    facilityRepository.save(facility);

    Program program = new Program();
    program.setCode("programCode");
    programRepository.save(program);

    User user1 = new User();
    user1.setUsername("userName");
    user1.setPassword("userPassword");
    user1.setFirstName("userFirstName");
    user1.setLastName("userLastName");
    userRepository.save(user1);

    order.setOrderCode("orderCode");
    order.setQuotedCost(new BigDecimal("1.29"));
    order.setStatus(OrderStatus.PICKING);
    order.setProgram(program);
    order.setCreatedBy(user1);
    order.setRequestingFacility(facility);
    order.setReceivingFacility(facility);
    order.setSupplyingFacility(facility);
    orderRepository.save(order);

    firstProduct.setPrimaryName("firstProductName");
    firstProduct.setCode("firstProductCode");
    firstProduct.setDispensingUnit("unit");
    firstProduct.setDosesPerDispensingUnit(10);
    firstProduct.setPackSize(1);
    firstProduct.setPackRoundingThreshold(0);
    firstProduct.setRoundToZero(false);
    firstProduct.setActive(true);
    firstProduct.setFullSupply(true);
    firstProduct.setTracer(false);
    productRepository.save(firstProduct);

    secondProduct.setPrimaryName("secondProductName");
    secondProduct.setCode("secondProductCode");
    secondProduct.setDispensingUnit("unit");
    secondProduct.setDosesPerDispensingUnit(10);
    secondProduct.setPackSize(1);
    secondProduct.setPackRoundingThreshold(0);
    secondProduct.setRoundToZero(false);
    secondProduct.setActive(true);
    secondProduct.setFullSupply(true);
    secondProduct.setTracer(false);
    productRepository.save(secondProduct);

    BigDecimal cost1 = new BigDecimal(100);
    BigDecimal cost2 = new BigDecimal(200);

    facilityType1.setCode("FT1");

    facilityType2.setCode("FT2");

    geographicLevel1.setCode("GL1");
    geographicLevel1.setLevelNumber(1);

    geographicLevel2.setCode("GL2");
    geographicLevel2.setLevelNumber(1);

    geographicZone1.setCode("GZ1");
    geographicZone1.setLevel(geographicLevel1);

    geographicZone2.setCode("GZ2");
    geographicZone2.setLevel(geographicLevel2);

    schedule1.setCode("S1");
    schedule1.setName("Schedule1");
    scheduleRepository.save(schedule1);

    schedule2.setCode("S2");
    schedule2.setName("Schedule2");
    scheduleRepository.save(schedule2);

    product1.setCode("P1");
    product1.setPrimaryName("Product1");
    product1.setDispensingUnit("pills");
    product1.setDosesPerDispensingUnit(1);
    product1.setPackSize(10);
    product1.setPackRoundingThreshold(10);
    product1.setRoundToZero(false);
    product1.setActive(true);
    product1.setFullSupply(false);
    product1.setTracer(false);
    productRepository.save(product1);

    product2.setCode("P2");
    product2.setPrimaryName("Product2");
    product2.setDispensingUnit("pills");
    product2.setDosesPerDispensingUnit(2);
    product2.setPackSize(20);
    product2.setPackRoundingThreshold(20);
    product2.setRoundToZero(true);
    product2.setActive(true);
    product2.setFullSupply(false);
    product2.setTracer(false);
    productRepository.save(product2);

    program1.setCode("P1");
    program1.setName("Program1");
    programRepository.save(program1);

    program2.setCode("P2");
    program2.setName("Program2");
    programRepository.save(program2);

    facility1.setCode("F1");
    facility1.setName("Facility1");
    facility1.setGeographicZone(geographicZone1);
    facility1.setType(facilityType1);
    facility1.setActive(true);
    facility1.setEnabled(false);
    facilityRepository.save(facility1);

    facility2.setCode("F2");
    facility2.setName("Facility2");
    facility2.setGeographicZone(geographicZone2);
    facility2.setType(facilityType2);
    facility2.setActive(true);
    facility2.setEnabled(false);
    facilityRepository.save(facility2);

    period1.setProcessingSchedule(schedule1);
    period1.setName("P1");
    period1.setStartDate(LocalDate.of(2015, Month.JANUARY, 1));
    period1.setEndDate(LocalDate.of(2015, Month.DECEMBER, 31));
    periodRepository.save(period1);

    period2.setProcessingSchedule(schedule2);
    period2.setName("P2");
    period2.setStartDate(LocalDate.of(2016, Month.JANUARY, 1));
    period2.setEndDate(LocalDate.of(2016, Month.DECEMBER, 31));
    periodRepository.save(period2);

    requisition1.setProgram(program1);
    requisition1.setCreatedDate(LocalDateTime.of(2015, Month.JANUARY, 1, 10, 0, 0));
    requisition1.setFacility(facility1);
    requisition1.setProcessingPeriod(period1);
    requisition1.setStatus(RequisitionStatus.RELEASED);
    requisitionRepository.save(requisition1);

    requisition2.setProgram(program2);
    requisition2.setCreatedDate(LocalDateTime.of(2016, Month.JANUARY, 1, 10, 0, 0));
    requisition2.setFacility(facility1);
    requisition2.setProcessingPeriod(period2);
    requisition2.setStatus(RequisitionStatus.RELEASED);
    requisitionRepository.save(requisition2);

    user.setUsername("user");
    user.setPassword("pass");
    user.setFirstName("Alice");
    user.setLastName("Cat");
    user.setHomeFacility(facility1);
    userRepository.save(user);

    order1.setRequisition(requisition1);
    order1.setStatus(OrderStatus.RECEIVED);
    order1.setCreatedBy(user);
    order1.setOrderCode("O1");
    order1.setProgram(program1);
    order1.setQuotedCost(cost1);
    order1.setSupplyingFacility(facility1);
    order1.setRequestingFacility(facility2);
    order1.setReceivingFacility(facility2);
    orderRepository.save(order1);

    order2.setRequisition(requisition2);
    order2.setStatus(OrderStatus.RECEIVED);
    order2.setCreatedBy(user);
    order2.setOrderCode("O2");
    order2.setProgram(program2);
    order2.setQuotedCost(cost2);
    order2.setSupplyingFacility(facility1);
    order2.setRequestingFacility(facility2);
    order2.setReceivingFacility(facility2);
    orderRepository.save(order2);

    orderLine1.setOrder(order1);
    orderLine1.setProduct(product1);
    orderLine1.setOrderedQuantity(new Long(50));
    orderLine1.setFilledQuantity(new Long(35));
    orderLineRepository.save(orderLine1);

    orderLine2.setOrder(order1);
    orderLine2.setProduct(product2);
    orderLine2.setOrderedQuantity(new Long(20));
    orderLine2.setFilledQuantity(new Long(20));
    orderLineRepository.save(orderLine2);

    orderLine3.setOrder(order2);
    orderLine3.setProduct(product1);
    orderLine3.setOrderedQuantity(new Long(50));
    orderLine3.setFilledQuantity(new Long(50));
    orderLineRepository.save(orderLine3);

    orderLine4.setOrder(order2);
    orderLine4.setProduct(product2);
    orderLine4.setOrderedQuantity(new Long(10));
    orderLine4.setFilledQuantity(new Long(5));
    orderLineRepository.save(orderLine4);
  }

  @After
  public void cleanUp() {
    orderLineRepository.deleteAll();
    orderRepository.deleteAll();
    stockRepository.deleteAll();
    requisitionRepository.deleteAll();
    programRepository.deleteAll();
    periodRepository.deleteAll();
    scheduleRepository.deleteAll();
    productRepository.deleteAll();
    userRepository.deleteAll();
    facilityRepository.deleteAll();
    geographicZoneRepository.deleteAll();
    geographicLevelRepository.deleteAll();
    facilityTypeRepository.deleteAll();
    stockInventoryRepository.deleteAll();
  }

  private void addOrderLine(Product product, Long quantity) {
    OrderLine orderLine = new OrderLine();
    orderLine.setOrder(order);
    orderLine.setProduct(product);
    orderLine.setOrderedQuantity(quantity);
    orderLine.setFilledQuantity(quantity);
    orderLine.setBatch("orderLineBatchNumber");
    orderLine.setExpiryDate(LocalDate.of(2016, 1, 1));
    orderLine.setVvm("orderLineVvm");
    orderLine.setManufacturer("orderLineManufacturer");
    orderLineRepository.save(orderLine);
  }

  private void addStock(Product product, Long quantity) {
    Stock stock = new Stock();
    stock.setStockInventory(stockInventory);
    stock.setProduct(product);
    stock.setStoredQuantity(quantity);
    stockRepository.save(stock);
  }

  @Test
  public void testCorrectOrderToFinalize() throws JsonProcessingException {
    addOrderLine(firstProduct, 123L);
    addStock(firstProduct, 1234L);
    addOrderLine(secondProduct, 12345L);
    addStock(secondProduct, 123456L);

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    RestTemplate restTemplate = new RestTemplate();

    ObjectMapper mapper = new ObjectMapper();
    String orderJson = mapper.writeValueAsString(order.getId());
    HttpEntity<String> entity = new HttpEntity<>(orderJson, headers);

    ResponseEntity<?> result=restTemplate.postForEntity(RESOURCE_FINALIZE_URL, entity, String.class);

    Assert.assertEquals(result.getStatusCode(), HttpStatus.OK);
  }

  @Test(expected = HttpClientErrorException.class)
  public void testWhenStockDoesNotExist() throws JsonProcessingException {
    addOrderLine(firstProduct, 123L);
    addStock(firstProduct, 1234L);
    addOrderLine(secondProduct, 12345L);

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    RestTemplate restTemplate = new RestTemplate();

    ObjectMapper mapper = new ObjectMapper();
    String orderJson = mapper.writeValueAsString(order.getId());
    HttpEntity<String> entity = new HttpEntity<>(orderJson, headers);

    restTemplate.postForEntity(RESOURCE_FINALIZE_URL, entity, String.class);
  }

  @Test(expected = HttpClientErrorException.class)
  public void testStockWithInsufficientQuantity() throws JsonProcessingException {
    addOrderLine(firstProduct, 123L);
    addStock(firstProduct, 1234L);
    addOrderLine(secondProduct, 12345L);
    addStock(secondProduct, 12L);

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    RestTemplate restTemplate = new RestTemplate();

    ObjectMapper mapper = new ObjectMapper();
    String orderJson = mapper.writeValueAsString(order.getId());
    HttpEntity<String> entity = new HttpEntity<>(orderJson, headers);

    restTemplate.postForEntity(RESOURCE_FINALIZE_URL, entity, String.class);
  }

  @Test
  public void testOrderList() throws JsonProcessingException {
    RestTemplate restTemplate = new RestTemplate();

    UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(RESOURCE_URL)
            .queryParam("user", user.getId())
            .queryParam("program", program1.getId())
            .queryParam("period", period1.getId())
            .queryParam("schedule", schedule1.getId());

    ResponseEntity<Iterable<Order>> orderListResponse = restTemplate.exchange(builder.toUriString(),
            HttpMethod.GET,
            null,
            new ParameterizedTypeReference<Iterable<Order>>() {
            });

    Iterable<Order> orderList = orderListResponse.getBody();
    Iterator<Order> orderIterator = orderList.iterator();
    Assert.assertTrue(orderIterator.hasNext());
    Order testOrder = orderIterator.next();
    Assert.assertFalse(orderIterator.hasNext());
    Assert.assertEquals(testOrder.getId(), order1.getId());
    Assert.assertEquals(testOrder.getRequisition().getId(), order1.getRequisition().getId());
    Assert.assertEquals(testOrder.getCreatedBy().getId(), order1.getCreatedBy().getId());
    Assert.assertEquals(testOrder.getOrderCode(), order1.getOrderCode());
    Assert.assertEquals(testOrder.getOrderLines().size(), 2);
    Assert.assertEquals(testOrder.getCreatedDate(), order1.getCreatedDate());
  }

  @Test
  public void testPrintOrderAsCsv() {
    RestTemplate restTemplate = new RestTemplate();
    UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(RESOURCE_URL + "/"
            + order1.getId() + "/print")
            .queryParam("format", "csv");
    ResponseEntity<?> printOrderResponse = restTemplate.exchange(builder.toUriString(),
            HttpMethod.GET,
            null,
            new ParameterizedTypeReference<String>() {
            });
    String csvContent = printOrderResponse.getBody().toString();
    Assert.assertTrue(csvContent.startsWith("Product,Filled Quantity,Ordered Quantity"));
    for (OrderLine o : orderRepository.findOne(order1.getId()).getOrderLines())
    {
      Assert.assertTrue(csvContent.contains(o.getProduct().getPrimaryName()
              + "," + o.getFilledQuantity()
              + "," + o.getOrderedQuantity()));
    }
  }

  @Test
  public void testPrintOrderAsPdf() {
    RestTemplate restTemplate = new RestTemplate();
    UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(RESOURCE_URL + "/" + order2.getId() + "/print")
            .queryParam("format", "pdf");
    ResponseEntity<?> printOrderResponse = restTemplate.exchange(builder.toUriString(),
            HttpMethod.GET,
            null,
            new ParameterizedTypeReference<String>() {
            });
    String pdfContent = printOrderResponse.getBody().toString();
    Assert.assertTrue(pdfContent!=null);
  }
}
