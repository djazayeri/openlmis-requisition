package org.openlmis.fulfillment.utils;

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
import org.openlmis.requisition.domain.Requisition;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;



public class BuildProofOfDeliveryPdf extends PdfViewGenerator {

    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("dd MM yyyy");

    @Override
    protected void buildPdfDocument(Map<String, Object> model, Document document, PdfWriter writer,
                                    HttpServletRequest request, HttpServletResponse response)
                                    throws Exception {

        Map<Requisition, OrderProofOfDelivery> orderProofOfDeliveries =
                (Map<Requisition, OrderProofOfDelivery>) model.get("orderProofOfDeliveries");
        OrderProofOfDelivery orderProofOfDelivery = orderProofOfDeliveries.values().iterator().next();
        Requisition requisition = orderProofOfDeliveries.keySet().iterator().next();
        addHeading(document, orderProofOfDelivery);
        addFirstSeparator(document);
        new AddOrderData(document, orderProofOfDelivery, requisition);
        addBreakLine(document);
        new AddProductsData(document, orderProofOfDelivery);
        addBreakLine(document);
        addSummary(document, orderProofOfDelivery);
        document.close();
    }

    private void addHeading(Document document, OrderProofOfDelivery orderProofOfDelivery) {
        Table headingTable = new Table(2);
        headingTable.setWidthPercent(100);

        String programName = "";
        if(orderProofOfDelivery.getOrder().getProgram().getName() != null) {
            programName = orderProofOfDelivery.getOrder().getProgram().getName();
        }
        Paragraph program = new Paragraph("Proof of Delivery for " +
                programName + "\t").setTextAlignment(TextAlignment.LEFT);
        headingTable.addCell(new Cell().add(program).setBorder(Border.NO_BORDER));

        Paragraph actualDate = new Paragraph(DATE_FORMAT.format(
                new Date())).setTextAlignment(TextAlignment.RIGHT);
        headingTable.addCell(new Cell().add(actualDate).setBorder(Border.NO_BORDER));

        document.add(headingTable);
    }

    private void addFirstSeparator(Document document) {
        SolidLine l = new SolidLine();
        document.add(new LineSeparator(l));
        document.add(new Paragraph("\n"));
    }

    private void addBreakLine(Document document) {
        document.add(new Paragraph("\n"));
    }

    private void addSummary(Document document, OrderProofOfDelivery orderProofOfDelivery) {
        Paragraph summary = new Paragraph(
                "Summary").setTextAlignment(TextAlignment.LEFT).setFontSize(16);
        document.add(summary);
        Long totalShipped = new Long(0);
        Long totalReceived = new Long(0);
        Long totalReturned = new Long(0);
        for (OrderProofOfDeliveryLine currentItem :
                orderProofOfDelivery.getProfOfDeliveryLineItems())
        {
            if(currentItem.getQuantityShipped() != null) {
                totalShipped += currentItem.getQuantityShipped();
            }
            if(currentItem.getQuantityReceived() != null) {
                totalReceived += currentItem.getQuantityReceived();
            }
            if(currentItem.getQuantityReturned() != null) {
                totalReturned += currentItem.getQuantityReturned();
            }
        }
        Paragraph totalShippedPacks = new Paragraph("Total Shipped Packs: " + totalShipped);
        Paragraph totalReceivedPacks = new Paragraph("Total Received Packs: " + totalReceived);
        Paragraph totalReturnedPacks = new Paragraph("Total Returned Packs: " + totalReturned);
        String deliveredBy = "";
        if(orderProofOfDelivery.getDeliveredBy() != null) {
            deliveredBy = orderProofOfDelivery.getDeliveredBy();
        }
        Paragraph deliveredByParagraph = new Paragraph("Delivered By: " + deliveredBy);
        String receivedBy = "";
        if(orderProofOfDelivery.getReceivedBy() != null) {
            receivedBy = orderProofOfDelivery.getReceivedBy();
        }
        String receivedDate = "";
        if(orderProofOfDelivery.getReceivedDate() != null) {
            receivedDate = DATE_FORMAT.format(orderProofOfDelivery.getReceivedDate());
        }

        Paragraph receivedByParagraph = new Paragraph("Received By: " + receivedBy);
        Paragraph receivedDateParagraph = new Paragraph("Received Date: " + receivedDate);

        Table summaryTable = new Table(2);
        summaryTable.setWidthPercent(100);
        summaryTable.addCell(new Cell().add(totalShippedPacks).setBorder(Border.NO_BORDER));
        summaryTable.addCell(new Cell().add(deliveredByParagraph).setBorder(Border.NO_BORDER));
        summaryTable.addCell(new Cell().add(totalReceivedPacks).setBorder(Border.NO_BORDER));
        summaryTable.addCell(new Cell().add(receivedByParagraph).setBorder(Border.NO_BORDER));
        summaryTable.addCell(new Cell().add(totalReturnedPacks).setBorder(Border.NO_BORDER));
        summaryTable.addCell(new Cell().add(receivedDateParagraph).setBorder(Border.NO_BORDER));
        document.add(summaryTable);
    }

}
