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

import br.com.calcana.calcana_api.model.Analises;
import br.com.calcana.calcana_api.services.AnaliseService;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.format.annotation.DateTimeFormat;

import org.springframework.web.bind.annotation.PostMapping;
import java.time.format.DateTimeFormatter;
import org.springframework.http.HttpStatus;
import br.com.calcana.calcana_api.model.Fornecedor;
import br.com.calcana.calcana_api.services.EmailService;
import org.apache.commons.io.IOUtils;

import java.time.LocalDate;
import java.util.List;

import java.io.ByteArrayInputStream;
import java.io.IOException;

@RestController
@RequestMapping("/relatorios")
public class ReportController {

    @Autowired
    private ReportService reportService;

    @Autowired
    private AnaliseService analiseService;

    @Autowired
    private EmailService emailService;

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


    @GetMapping("/analises/excel")
    @PreAuthorize("hasAnyRole('GESTOR', 'OPERADOR')")
    public ResponseEntity<InputStreamResource> gerarRelatorioExcel(
            @RequestParam(required = false, defaultValue = "default") String layout,

            @RequestParam(required = false) Long fornecedorId,
            @RequestParam(required = false) Long propriedadeId,
            @RequestParam(required = false) String talhao,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim
    ) {
        try {
            List<Analises> analises = analiseService.listarTodasFiltradas(
                    fornecedorId, propriedadeId, talhao, dataInicio, dataFim
            );

            ByteArrayInputStream excelStream = reportService.gerarRelatorioExcel(analises, layout);

            HttpHeaders headers = new HttpHeaders();
            String filename = "relatorio_analises_" + layout + ".xlsx";

            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename);

            return ResponseEntity
                    .ok()
                    .headers(headers)
                    .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                    .body(new InputStreamResource(excelStream));

        } catch (IOException e) {
            return ResponseEntity.internalServerError().build();
        } catch (Exception e) {
            throw e;
        }
    }


    @PostMapping("/analise/{id}/enviar")
    @PreAuthorize("hasRole('OPERADOR')")
    public ResponseEntity<Void> enviarBoletimPorEmail(@PathVariable("id") Long idAnalise) {

        try {
            Analises analise = analiseService.buscarPorId(idAnalise);
            Fornecedor fornecedor = analise.getPropriedade().getFornecedor();

            String emailDestinatario = fornecedor.getEmail();
            if (emailDestinatario == null || emailDestinatario.isEmpty()) {
                throw new RuntimeException("Fornecedor não possui e-mail cadastrado.");
            }

            ByteArrayInputStream pdfStream = reportService.gerarBoletimPdf(idAnalise);
            byte[] pdfBytes = IOUtils.toByteArray(pdfStream);
            String nomeArquivo = "boletim_analise_" + idAnalise + ".pdf";

            String assunto = "Boletim de Análise de ATR - Amostra " + analise.getNumeroAmostra();
            String corpo = String.format(
                    "Olá, %s!\n\n" +
                            "Segue em anexo o boletim da análise de ATR referente à amostra nº %d, " +
                            "realizada em %s na propriedade %s (Talhão: %s).\n\n" +
                            "Atenciosamente,\nEquipe Calcana (Assocana)",
                    fornecedor.getNome(),
                    analise.getNumeroAmostra(),
                    analise.getDataAnalise().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                    analise.getPropriedade().getNome(),
                    analise.getTalhao()
            );

            emailService.enviarEmailComAnexo(
                    emailDestinatario,
                    assunto,
                    corpo,
                    pdfBytes,
                    nomeArquivo
            );

            analiseService.registrarEnvioEmail(idAnalise);

            return ResponseEntity.ok().build();

        } catch (IOException e) {
            return ResponseEntity.internalServerError().build();
        } catch (RuntimeException e) {
            throw e;
        }
    }
}