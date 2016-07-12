package org.openlmis.fulfillment.utils;

import com.itextpdf.kernel.color.Color;
import com.itextpdf.kernel.color.DeviceCmyk;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.draw.SolidLine;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.border.Border;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.LineSeparator;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.property.TextAlignment;
import org.openlmis.fulfillment.domain.OrderProofOfDelivery;
import org.openlmis.fulfillment.domain.OrderProofOfDeliveryLine;
import org.openlmis.product.domain.Product;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class BuildProofOfDeliveryPdf extends PdfViewGenerator {

    private static final DateFormat dateFormat = new SimpleDateFormat("dd MM yyyy");

    private static final DateTimeFormatter formatter =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    @Override
    protected void buildPdfDocument(Map<String, Object> model, Document document, PdfWriter writer,
                                    HttpServletRequest request, HttpServletResponse response)
                                    throws Exception {

        List<OrderProofOfDelivery> orderProofOfDeliveries =
                (List<OrderProofOfDelivery>) model.get("orderProofOfDeliveries");
        int alwaysFirstElementIsNeeded = 0;
        OrderProofOfDelivery orderProofOfDelivery =
                orderProofOfDeliveries.get(alwaysFirstElementIsNeeded);
        addHeading(document, orderProofOfDelivery);
        addFirstSeparator(document);
        addOrderData(document, orderProofOfDelivery);
        addBreakLine(document);
        addProducts(document, orderProofOfDelivery);
        addBreakLine(document);
        addSummary(document, orderProofOfDelivery);
        document.close();
    }

    private void addHeading(Document document, OrderProofOfDelivery orderProofOfDelivery) {
        Table headingTable = new Table(2);
        headingTable.setWidthPercent(100);

        String programName = orderProofOfDelivery.getOrder().getProgram().getName();
        Paragraph program = new Paragraph("Proof of Delivery for " +
                programName + "\t").setTextAlignment(TextAlignment.LEFT);
        headingTable.addCell(new Cell().add(program).setBorder(Border.NO_BORDER));

        Paragraph actualDate = new Paragraph(dateFormat.format(
                new Date())).setTextAlignment(TextAlignment.RIGHT);
        headingTable.addCell(new Cell().add(actualDate).setBorder(Border.NO_BORDER));

        document.add(headingTable);
    }

    private void addFirstSeparator(Document document) {
        SolidLine l = new SolidLine();
        document.add(new LineSeparator(l));
        document.add(new Paragraph("\n"));
    }

    private void addBreakLine(Document document)
    {
        document.add(new Paragraph("\n"));
    }

    private void addOrderData(Document document, OrderProofOfDelivery orderProofOfDelivery) {
        Paragraph order = new Paragraph(
                "Order").setTextAlignment(TextAlignment.LEFT).setFontSize(16);
        document.add(order);

        Table orderTable = new Table(6);
        orderTable.setWidthPercent(100);
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

        String orderNo = orderProofOfDelivery.getOrder().getOrderCode();
        //String reportingPeriod = TODO
        String facility = orderProofOfDelivery.getOrder().getReceivingFacility().getName();
        String supplyingDepot = orderProofOfDelivery.getOrder().getSupplyingFacility().getName();
        LocalDateTime localDateTime = orderProofOfDelivery.getOrder().getCreatedDate();
        String orderDate = localDateTime.format(formatter);
        //String orderType = orderProofOfDelivery.getOrder().getStatus().name(); TODO

        orderTable.addCell(new Cell().add(new Paragraph(orderNo)));
        orderTable.addCell(new Cell().add(new Paragraph("-")));
        orderTable.addCell(new Cell().add(new Paragraph(facility)));
        orderTable.addCell(new Cell().add(new Paragraph(supplyingDepot)));
        orderTable.addCell(new Cell().add(new Paragraph(orderDate)));
        orderTable.addCell(new Cell().add(new Paragraph("-")));
        document.add(orderTable);
    }

    private void addProducts(Document document, OrderProofOfDelivery orderProofOfDelivery) {
        Paragraph products = new Paragraph(
                "Product(s)").setTextAlignment(TextAlignment.LEFT).setFontSize(16);
        document.add(products);

        List<OrderProofOfDeliveryLine> orderProofOfDeliveryLines =
                orderProofOfDelivery.getProfOfDeliveryLineItems();
        float[] cellProportionsInTable2 = new float[]
                {2, 2, 4, 1.5f, 1.5f, 1.5f, 1.5f, 1.5f, 1.5f, 5};
        Table table2 = new Table(cellProportionsInTable2);
        table2.setWidthPercent(100);

        Paragraph categoryParagraph = new Paragraph("Category");
        Cell categoryCell = new Cell(1,10).add(categoryParagraph);
        Color blueColor = new DeviceCmyk(0.445f, 0.0546f, 0, 0.0667f);
        categoryCell.setBackgroundColor(blueColor);
        table2.addCell(categoryCell);

        addHeaderCellsToProductsTable(table2);

        for (OrderProofOfDeliveryLine item :orderProofOfDeliveryLines)
        {
            addCellsToProductsTable(table2, item);
        }
        document.add(table2);
    }

    private void addCellsToProductsTable(Table table2, OrderProofOfDeliveryLine item) {
        Product currentProduct = item.getOrderLine().getProduct();
        table2.addCell(new Cell().add(new Paragraph(
                currentProduct.getFullSupply().toString())));
        table2.addCell(new Cell().add(new Paragraph(
                currentProduct.getCode())));
        table2.addCell(new Cell().add(new Paragraph(
                currentProduct.getPrimaryName())));
        table2.addCell(new Cell().add(new Paragraph(
                currentProduct.getDispensingUnit())));
        table2.addCell(new Cell().add(new Paragraph(
                item.getPackToShip().toString())));
        table2.addCell(new Cell().add(new Paragraph(
                item.getQuantityShipped().toString())));
        table2.addCell(new Cell().add(new Paragraph(
                item.getQuantityReceived().toString())));
        table2.addCell(new Cell().add(new Paragraph(
                item.getQuantityReturned().toString())));
        table2.addCell(new Cell().add(new Paragraph(item.getReplacedProductCode())));
        table2.addCell(new Cell().add(new Paragraph(item.getNotes())));
    }

    private void addHeaderCellsToProductsTable(Table table2) {
        table2.addHeaderCell(new Cell().add(new Paragraph(
                "Full\n Supply")).setBackgroundColor(Color.LIGHT_GRAY));
        table2.addHeaderCell(new Cell().add(new Paragraph(
                "Product\n Code")).setBackgroundColor(Color.LIGHT_GRAY));
        table2.addHeaderCell(new Cell().add(new Paragraph(
                "Product\n Name")).setBackgroundColor(Color.LIGHT_GRAY));
        table2.addHeaderCell(new Cell().add(new Paragraph(
                "Unit\n of\n Issue")).setBackgroundColor(Color.LIGHT_GRAY));
        table2.addHeaderCell(new Cell().add(new Paragraph(
                "Packs\n to\n Ship")).setBackgroundColor(Color.LIGHT_GRAY));
        table2.addHeaderCell(new Cell().add(new Paragraph(
                "Quantity\n Shipped")).setBackgroundColor(Color.LIGHT_GRAY));
        table2.addHeaderCell(new Cell().add(new Paragraph(
                "Quantity\n Received")).setBackgroundColor(Color.LIGHT_GRAY));
        table2.addHeaderCell(new Cell().add(new Paragraph(
                "Quantity\n Returned")).setBackgroundColor(Color.LIGHT_GRAY));
        table2.addHeaderCell(new Cell().add(new Paragraph(
                "Replaced\n Product\n Code")).setBackgroundColor(Color.LIGHT_GRAY));
        table2.addHeaderCell(new Cell().add(new Paragraph(
                "Notes")).setBackgroundColor(Color.LIGHT_GRAY));
    }

    private void addSummary(Document document, OrderProofOfDelivery orderProofOfDelivery) {
        Paragraph summary = new Paragraph(
                "Summary").setTextAlignment(TextAlignment.LEFT).setFontSize(16);
        document.add(summary);

        Paragraph totalShippedPacks = new Paragraph("Total Shipped Packs");
        Paragraph totalReceivedPacks = new Paragraph("Total Received Packs");
        Paragraph totalReturnedPacks = new Paragraph("Total Returned Packs");

        Paragraph deliveredBy = new Paragraph("Delivered By: "
                + orderProofOfDelivery.getDeliveredBy());
        Paragraph receivedBy = new Paragraph("Received By: "
                + orderProofOfDelivery.getReceivedBy());
        Paragraph receivedDate = new Paragraph("Received Date: " +
                dateFormat.format(orderProofOfDelivery.getReceivedDate()));

        Table tableSummary = new Table(2);
        tableSummary.setWidthPercent(100);

        tableSummary.addCell(new Cell().add(totalShippedPacks).setBorder(Border.NO_BORDER));
        tableSummary.addCell(new Cell().add(deliveredBy).setBorder(Border.NO_BORDER));
        tableSummary.addCell(new Cell().add(totalReceivedPacks).setBorder(Border.NO_BORDER));
        tableSummary.addCell(new Cell().add(receivedBy).setBorder(Border.NO_BORDER));
        tableSummary.addCell(new Cell().add(totalReturnedPacks).setBorder(Border.NO_BORDER));
        tableSummary.addCell(new Cell().add(receivedDate).setBorder(Border.NO_BORDER));
        document.add(tableSummary);
    }
}
