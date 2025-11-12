package br.com.calcana.calcana_api.controllers;

import br.com.calcana.calcana_api.services.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayInputStream;
import java.io.IOException;

@RestController
@RequestMapping("/relatorios")
public class ReportController {

    @Autowired
    private ReportService reportService;

    @GetMapping("/analise/{id}/pdf")
    @PreAuthorize("hasAnyRole('GESTOR', 'OPERADOR')")
    public ResponseEntity<InputStreamResource> gerarBoletimPdf(@PathVariable("id") Long idAnalise) {

        try {
            ByteArrayInputStream pdfStream = reportService.gerarBoletimPdf(idAnalise);

            HttpHeaders headers = new HttpHeaders();

            String filename = "boletim_analise_" + idAnalise + ".pdf";

            headers.add(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=" + filename);

            return ResponseEntity
                    .ok()
                    .headers(headers)
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(new InputStreamResource(pdfStream));

        } catch (IOException e) {
            return ResponseEntity.internalServerError().build();
        } catch (Exception e) {
            throw e;
        }
    }
}