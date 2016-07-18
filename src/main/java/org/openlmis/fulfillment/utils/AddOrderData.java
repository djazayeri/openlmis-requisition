package org.openlmis.fulfillment.utils;

import com.itextpdf.kernel.color.Color;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.property.TextAlignment;
import org.openlmis.fulfillment.domain.OrderProofOfDelivery;
import org.openlmis.requisition.domain.Requisition;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


public class AddOrderData {

    private static final DateTimeFormatter DATE_TIME_FORMAT =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("dd MM yyyy");

    public AddOrderData(Document document, OrderProofOfDelivery orderProofOfDelivery,
                              Requisition requisition) {
        Paragraph order = new Paragraph(
                "Order").setTextAlignment(TextAlignment.LEFT).setFontSize(16);
        document.add(order);

        Table orderTable = new Table(6);
        orderTable.setWidthPercent(100);
        addHeaderCellsToOrderTable(orderTable);

        addCellsToOrderTable(orderProofOfDelivery, requisition, orderTable);
        document.add(orderTable);
    }

    private void addHeaderCellsToOrderTable(Table orderTable) {
        orderTable.addHeaderCell(new Cell().add(new Paragraph(
                "Order No.")).setBackgroundColor(Color.LIGHT_GRAY));
        orderTable.addHeaderCell(new Cell().add(new Paragraph(
                "Reporting\n Period")).setBackgroundColor(Color.LIGHT_GRAY));
        orderTable.addHeaderCell(new Cell().add(new Paragraph(
                "Facility")).setBackgroundColor(Color.LIGHT_GRAY));
        orderTable.addHeaderCell(new Cell().add(new Paragraph(
                "Supplying\n Depot")).setBackgroundColor(Color.LIGHT_GRAY));
        orderTable.addHeaderCell(new Cell().add(new Paragraph(
                "Order date")).setBackgroundColor(Color.LIGHT_GRAY));
        orderTable.addHeaderCell(new Cell().add(new Paragraph(
                "Type")).setBackgroundColor(Color.LIGHT_GRAY));
    }

    private void addCellsToOrderTable(OrderProofOfDelivery orderProofOfDelivery,
                                             Requisition requisition, Table orderTable) {
        String orderNo = orderNo = orderProofOfDelivery.getOrder().getOrderCode();
        String facility = "";
        if (orderProofOfDelivery.getOrder().getReceivingFacility().getName() != null) {
            facility = orderProofOfDelivery.getOrder().getReceivingFacility().getName();
        }
        String supplyingDepot = "";
        if (orderProofOfDelivery.getOrder().getSupplyingFacility().getName() != null) {
            supplyingDepot = orderProofOfDelivery.getOrder().getSupplyingFacility().getName();
        }
        String orderDate = "";
        if (orderProofOfDelivery.getOrder().getCreatedDate() != null) {
            LocalDateTime localDateTime = orderProofOfDelivery.getOrder().getCreatedDate();
            orderDate = localDateTime.format(DATE_TIME_FORMAT);
        }
        String orderType = "";
        String reportingPeriod = "";
        if (requisition != null) {
            orderType = getOrderType(requisition, orderType);
            reportingPeriod = getReportingPeriod(requisition, reportingPeriod);
        }
        orderTable.addCell(new Cell().add(new Paragraph(orderNo)));
        orderTable.addCell(new Cell().add(new Paragraph(reportingPeriod)));
        orderTable.addCell(new Cell().add(new Paragraph(facility)));
        orderTable.addCell(new Cell().add(new Paragraph(supplyingDepot)));
        orderTable.addCell(new Cell().add(new Paragraph(orderDate)));
        orderTable.addCell(new Cell().add(new Paragraph(orderType)));
    }

    private String getReportingPeriod(Requisition requisition, String reportingPeriod) {
        if (requisition.getProcessingPeriod().getStartDate() != null &&
                requisition.getProcessingPeriod().getEndDate() != null) {
            reportingPeriod = DATE_FORMAT.format(
                    requisition.getProcessingPeriod().getStartDate())
                    + " " + DATE_FORMAT.format(
                    requisition.getProcessingPeriod().getEndDate());
        }
        return reportingPeriod;
    }

    private String getOrderType(Requisition requisition, String orderType) {
        if (requisition.getEmergency() != null) {
            if (requisition.getEmergency() == Boolean.TRUE) {
                orderType = "Emergency";
            } else {
                orderType = "Regular";
            }
        }
        return orderType;
    }
}
