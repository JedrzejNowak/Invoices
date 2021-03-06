package pl.coderstrust.controller;

import com.itextpdf.text.DocumentException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pl.coderstrust.database.DatabaseOperationException;
import pl.coderstrust.model.Invoice;
import pl.coderstrust.service.InvoiceService;
import pl.coderstrust.service.PdfService;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Collection;
import javax.servlet.http.HttpServletResponse;


@RestController
@RequestMapping("/invoices")
@Api(value = "/invoices", description = "Operations related to invoices in Invoices App")
public class InvoiceController {

  private InvoiceService invoiceService;
  private PdfService pdfService;

  @Autowired
  public InvoiceController(InvoiceService invoiceService, PdfService pdfService) {
    this.invoiceService = invoiceService;
    this.pdfService = pdfService;
  }

  @PostMapping
  @ApiOperation(
      value = "Add/update an invoice",
      notes = "Add new invoice with next id to the database. "
          + "If id exists in the database invoice wil be updated",
      response = Invoice.class,
      responseContainer = "List")
  public Invoice save(
      @ApiParam(value = "The details of invoice to be added/updated in the database",
          required = true) @RequestBody Invoice invoice) throws DatabaseOperationException {
    return invoiceService.save(invoice);
  }

  @DeleteMapping("/{id}")
  @ApiOperation(
      value = "Remove an invoice",
      notes = "Remove invoice with given ID from the database")
  public void delete(
      @ApiParam(value = "The invoice ID to be deleted", required = true) @PathVariable Long id)
      throws DatabaseOperationException {
    invoiceService.delete(id);
  }

  @GetMapping
  @ApiOperation(
      value = "Find all invoices",
      notes = "Return all invoices from the database",
      response = Invoice.class,
      responseContainer = "List")
  Collection<Invoice> findAll() throws DatabaseOperationException {
    return invoiceService.findAll();
  }

  @GetMapping("/buyer/{companyId}")
  @ApiOperation(
      value = "Find invoices by buyer",
      notes = "Return all invoices by the buyer ID from the database",
      response = Invoice.class,
      responseContainer = "List")
  Collection<Invoice> findByBuyer(
      @ApiParam(value = "The ID of buyer based on which the list of invoices will be returned",
          required = true) @PathVariable Long companyId) throws DatabaseOperationException {
    return invoiceService.findByBuyer(companyId);
  }

  @GetMapping("/seller/{companyId}")
  @ApiOperation(
      value = "Find invoices by seller",
      notes = "Return all invoices by the seller ID from the database",
      response = Invoice.class,
      responseContainer = "List")
  Collection<Invoice> findBySeller(
      @ApiParam(value = "The ID of seller based on which the list of invoices will be returned",
          required = true) @PathVariable Long companyId) throws DatabaseOperationException {
    return invoiceService.findBySeller(companyId);
  }

  @GetMapping("/byDate")
  @ApiOperation(
      value = "Find invoices by range of date",
      notes = "Return all invoices from the database in terms of given range of date",
      response = Invoice.class,
      responseContainer = "List")
  Collection<Invoice> findByDate(
      @ApiParam(value = "Starting date", required = true) @DateTimeFormat(iso = ISO.DATE)
      @RequestParam LocalDate fromDate, @ApiParam(value = "Ending date", required = true)
  @DateTimeFormat(iso = ISO.DATE) @RequestParam LocalDate toDate)
      throws DatabaseOperationException {
    return invoiceService.findByDate(fromDate, toDate);
  }

  @GetMapping("/{id}")
  @ApiOperation(
      value = "Find invoice by id",
      notes = "Return invoice with given ID from the database",
      response = Invoice.class,
      responseContainer = "List")
  Invoice findOne(
      @ApiParam(value = "The invoice ID to be found", required = true) @PathVariable Long id)
      throws DatabaseOperationException {
    return invoiceService.findOne(id);
  }

  @RequestMapping("/pdf/{id}")
  public void downloadPdfFile(HttpServletResponse response, @PathVariable Long id)
      throws IOException, DatabaseOperationException, DocumentException {
    Invoice invoice = invoiceService.findOne(id);
    pdfService.downloadPdfFile(response, invoice);
  }
}
