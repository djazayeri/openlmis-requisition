package org.openlmis.fulfillment.web;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.openlmis.fulfillment.domain.Order;
import org.openlmis.fulfillment.domain.OrderLine;
import org.openlmis.fulfillment.domain.OrderStatus;
import org.openlmis.fulfillment.repository.OrderRepository;
import org.openlmis.fulfillment.service.OrderService;
import org.openlmis.hierarchyandsupervision.domain.User;
import org.openlmis.hierarchyandsupervision.repository.UserRepository;
import org.openlmis.referencedata.domain.Facility;
import org.openlmis.referencedata.domain.Period;
import org.openlmis.referencedata.domain.Program;
import org.openlmis.referencedata.domain.Schedule;
import org.openlmis.referencedata.domain.Stock;
import org.openlmis.referencedata.repository.FacilityRepository;
import org.openlmis.referencedata.repository.PeriodRepository;
import org.openlmis.referencedata.repository.ProgramRepository;
import org.openlmis.referencedata.repository.ScheduleRepository;
import org.openlmis.csv.CsvGenerator;
import org.openlmis.referencedata.repository.StockRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.RepositoryRestController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


@RepositoryRestController
public class OrderController {
  Logger logger = LoggerFactory.getLogger(OrderController.class);

  @Autowired
  OrderRepository orderRepository;

  @Autowired
  UserRepository userRepository;

  @Autowired
  ProgramRepository programRepository;

  @Autowired
  FacilityRepository facilityRepository;

  @Autowired
  PeriodRepository periodRepository;

  @Autowired
  ScheduleRepository scheduleRepository;

  @Autowired
  OrderService orderService;

  @Autowired
  private StockRepository stockRepository;


  /**
   * Allows finalizing orders.
   *
   * @param orderId The UUID of the order to finalize
   * @return ResponseEntity with the "#200 OK" HTTP response status on success
  or ResponseEntity containing the error description and "#400 Bad Request" status
   */

  @RequestMapping(value = "/orders/finalizeOrder", method = RequestMethod.POST)
  public ResponseEntity<?> finalizeOrder(@RequestBody UUID orderId) {

    Order order = orderRepository.findOne(orderId);

    if (order == null) {
      return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    } else {
      for (OrderLine orderLine : order.getOrderLines()) {
        //Searching for a corresponding stock to the current orderline
        Stock stock = stockRepository.findByStockInventoryAndProduct(
                order.getSupplyingFacility().getStockInventory(),
                orderLine.getProduct()
        );
        //Checking if the stock exists.
        String productName = orderLine.getProduct().getPrimaryName();
        if (stock == null) {
          return new ResponseEntity<>(
                  "Error: There is no " + productName + " in the stock inventory.",
                  HttpStatus.BAD_REQUEST
          );
        }
        //Checking if there is sufficient quanitity of the ordered products.
        if (stock.getStoredQuantity() < orderLine.getOrderedQuantity()) {
          return new ResponseEntity<>(
                  "Error: There is insufficient quantity of " + productName
                          + " in the stock inventory.",
                  HttpStatus.BAD_REQUEST
          );
        }
      }

      logger.debug("Finalizing the order");

      /*Once finalized has been selected all commodities are subtracted
        from inventory at the warehouse*/
      for (OrderLine orderLine : order.getOrderLines()) {
        Stock stock = stockRepository.findByStockInventoryAndProduct(
                order.getSupplyingFacility().getStockInventory(),
                orderLine.getProduct()
        );
        stock.setStoredQuantity(
                stock.getStoredQuantity() - orderLine.getOrderedQuantity()
        );
        stockRepository.save(stock);
      }

      order.setStatus(OrderStatus.SHIPPED);
      orderRepository.save(order);

      return new ResponseEntity<>(HttpStatus.OK);
    }
  }

  /**
   * Return list of orders filled by user's home facility, filtered according to params.
   *
   * @param userId UUID of user whose home facility's orders we want to get
   * @param programId UUID of program we filter by
   * @param requestingFacilityId UUID of requesting facility we filter by
   * @param periodId UUID of period we filter by
   * @param scheduleId UUID of schedule we filter by
   * @param startDate LocalDate of start point we filter by
   * @param endDate LocalDate of end point we filter by
   * @param request HttpServletRequest object
   * @return result Iterable object with filtered orders
   */
  @RequestMapping(value = "/orders", method = RequestMethod.GET)
  //@PreAuthorize("@permissionEvaluator.hasPermission(principal, 'VIEW_ORDER')")
  @ResponseBody
  public Iterable<Order> getOrderList(
          @RequestParam(value = "user") UUID userId,
          @RequestParam(value = "program", required = false) UUID programId,
          @RequestParam(value = "facility", required = false) UUID requestingFacilityId,
          @RequestParam(value = "period", required = false) UUID periodId,
          @RequestParam(value = "schedule", required = false) UUID scheduleId,
          @RequestParam(value = "startYear", required = false) LocalDate startDate,
          @RequestParam(value = "endYear", required = false) LocalDate endDate,
          HttpServletRequest request) {
    User user = userRepository.findOne(userId);
    Program program = null;
    Facility requestingFacility = null;
    Period period = null;
    Schedule schedule = null;
    if (user == null) {
      return null;
    }
    if (programId != null) {
      program = programRepository.findOne(programId);
    }
    if (requestingFacilityId != null) {
      requestingFacility = facilityRepository.findOne(requestingFacilityId);
    }
    if (periodId != null) {
      period = periodRepository.findOne(periodId);
    }
    if (scheduleId != null) {
      schedule = scheduleRepository.findOne(scheduleId);
    }
    //Iterable<Order> orderList = orderRepository.findBySupplyingFacility(user.getHomeFacility());
    Iterable<Order> orderList = orderService.searchOrders(user.getHomeFacility(),
                                                requestingFacility,
                                                program,
                                                period,
                                                schedule,
                                                startDate,
                                                endDate);
    return orderList;
  }

  /**
   * Returns csv or pdf of defined object in response.
   *
   * @param orderId UUID of order to print
   * @param format String describing return format (pdf or csv)
   * @param response HttpServletResponse object
   */
  @RequestMapping(value = "/orders/{id}/print", method = RequestMethod.GET)
  @ResponseBody
  public void printOrder(@PathVariable("id") UUID orderId,
                         @RequestParam("format") String format,
                         HttpServletResponse response) {
    Order order = orderRepository.findOne(orderId);
    if (order == null) {
      try {
        response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Order does not exist.");
      } catch (IOException ex) {
        logger.info("Error sending error message to client.", ex);
      }
    }
    if (format.equals("pdf")) {
      printPdfOrder(order, response);
    } else {
      printCsvOrder(order, response);
    }
  }

  private void printCsvOrder(Order order, HttpServletResponse response) {
    response.setContentType("text/csv");
    response.addHeader("Content-Disposition",
            "attachment; filename=order" + order.getOrderCode() + ".csv");
    List<Map<String, Object>> csvRows = new ArrayList<>();
    for(OrderLine o : order.getOrderLines()) {
      Map<String, Object> row = new HashMap<>();
      row.put("Product", o.getProduct().getPrimaryName());
      row.put("Filled Quantity", o.getFilledQuantity());
      row.put("Ordered Quantity", o.getOrderedQuantity());
      csvRows.add(row);
    }
    List<String> header = new ArrayList<>();
    header.add("Product");
    header.add("Filled Quantity");
    header.add("Ordered Quantity");
    CsvGenerator csvGenerator = new CsvGenerator();
    String csvContent = csvGenerator.toCsv(csvRows, header.toArray(new String[0]));
    try {
      InputStream input = new ByteArrayInputStream(csvContent.getBytes(StandardCharsets.UTF_8));
      IOUtils.copy(input, response.getOutputStream());
      response.flushBuffer();
    } catch (IOException ex) {
      logger.info("Error writing csv file to output stream.", ex);
    }
  }

  private void printPdfOrder(Order order, HttpServletResponse response) {
    try {
      response.setContentType("application/pdf");
      response.addHeader("Content-Disposition",
              "attachment; filename=order-" + order.getOrderCode() + ".pdf");
      Document document = new Document();
      PdfWriter.getInstance(document, response.getOutputStream());
      document.open();
      PdfPTable headerTable = new PdfPTable(1);
      headerTable.setWidthPercentage(100);
      PdfPCell cell = new PdfPCell(new Phrase("Order - " + order.getOrderCode()));
      cell.setPadding(0);
      cell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
      cell.setBorder(PdfPCell.NO_BORDER);
      headerTable.addCell(cell);
      headerTable.setSpacingAfter(10f);
      document.add(headerTable);
      PdfPTable table = new PdfPTable(3);
      cell = new PdfPCell(new Phrase("Product"));
      cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
      table.addCell(cell);
      cell = new PdfPCell(new Phrase("Filled Quantity"));
      cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
      table.addCell(cell);
      cell = new PdfPCell(new Phrase("Ordered Quantity"));
      cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
      table.addCell(cell);
      for (OrderLine o : order.getOrderLines()) {
        table.addCell(o.getProduct().getPrimaryName());
        table.addCell(o.getFilledQuantity().toString());
        table.addCell(o.getOrderedQuantity().toString());
      }
      document.add(table);
      document.close();
      response.flushBuffer();
    } catch (IOException ex) {
      logger.info("Error writing pdf file to output stream.", ex);
    } catch (DocumentException ex) {
      logger.info("Error writing pdf file to output stream.", ex);
    }
  }
}
