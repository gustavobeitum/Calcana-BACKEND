package br.com.calcana.calcana_api.services;

import br.com.calcana.calcana_api.model.Analises;
import br.com.calcana.calcana_api.model.Fornecedor;
import br.com.calcana.calcana_api.model.Propriedade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.kernel.colors.DeviceGray;
import com.itextpdf.layout.properties.HorizontalAlignment;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;

import java.util.List;
import java.math.BigDecimal;

@Service
public class ReportService {

    @Autowired
    private AnaliseService analiseService;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    //PDF
    public ByteArrayInputStream gerarBoletimPdf(Long idAnalise) throws IOException {

        Analises analise = analiseService.buscarPorId(idAnalise);
        Propriedade propriedade = analise.getPropriedade();
        Fornecedor fornecedor = propriedade.getFornecedor();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(baos);
        PdfDocument pdfDoc = new PdfDocument(writer);
        Document document = new Document(pdfDoc, PageSize.A4);
        document.setMargins(30, 30, 30, 30);

        Paragraph titulo = new Paragraph("Boletim de Análise de ATR")
                .setTextAlignment(TextAlignment.CENTER)
                .setBold()
                .setFontSize(18)
                .setMarginBottom(20);
        document.add(titulo);

        Table tabelaInfo = new Table(UnitValue.createPercentArray(new float[]{1, 2, 1, 2}));
        tabelaInfo.setWidth(UnitValue.createPercentValue(100)).setMarginBottom(15);

        tabelaInfo.addCell(criarCelulaCabecalho("Fornecedor:"));
        tabelaInfo.addCell(criarCelulaSimples(fornecedor.getNome()));
        tabelaInfo.addCell(criarCelulaCabecalho("Propriedade:"));
        tabelaInfo.addCell(criarCelulaSimples(propriedade.getNome()));

        tabelaInfo.addCell(criarCelulaCabecalho("Data Análise:"));
        tabelaInfo.addCell(criarCelulaSimples(analise.getDataAnalise().format(DATE_FORMATTER)));
        tabelaInfo.addCell(criarCelulaCabecalho("Talhão:"));
        tabelaInfo.addCell(criarCelulaSimples(analise.getTalhao() != null ? analise.getTalhao() : "N/A"));

        tabelaInfo.addCell(criarCelulaCabecalho("Amostra Nº:"));
        tabelaInfo.addCell(criarCelulaSimples(analise.getNumeroAmostra().toString()));
        tabelaInfo.addCell(criarCelulaCabecalho("Corte:"));
        tabelaInfo.addCell(criarCelulaSimples(analise.getCorte() != null ? analise.getCorte().toString() : "N/A"));

        document.add(tabelaInfo);

        Paragraph tituloResultados = new Paragraph("Resultados Calculados (Consecana)")
                .setBold()
                .setFontSize(14)
                .setMarginTop(10)
                .setMarginBottom(10);
        document.add(tituloResultados);

        Table tabelaResultados = new Table(UnitValue.createPercentArray(new float[]{1, 1, 1, 1}));
        tabelaResultados.setWidth(UnitValue.createPercentValue(100));

        tabelaResultados.addCell(criarCelulaCabecalho("PBU"));
        tabelaResultados.addCell(criarCelulaCabecalho("BRIX"));
        tabelaResultados.addCell(criarCelulaCabecalho("L. Sacarimétrica"));
        tabelaResultados.addCell(criarCelulaCabecalho("POL do Caldo"));

        tabelaResultados.addCell(criarCelulaSimples(analise.getPbu().toString()));
        tabelaResultados.addCell(criarCelulaSimples(analise.getBrix().toString()));
        tabelaResultados.addCell(criarCelulaSimples(analise.getLeituraSacarimetrica().toString()));
        tabelaResultados.addCell(criarCelulaSimples(analise.getPolCaldo() != null ? analise.getPolCaldo().toString() : "-"));

        tabelaResultados.addCell(criarCelulaCabecalho("Fibra"));
        tabelaResultados.addCell(criarCelulaCabecalho("Pureza"));
        tabelaResultados.addCell(criarCelulaCabecalho("AR da Cana"));
        tabelaResultados.addCell(criarCelulaCabecalho("POL da Cana"));

        tabelaResultados.addCell(criarCelulaSimples(analise.getFibra() != null ? analise.getFibra().toString() : "-"));
        tabelaResultados.addCell(criarCelulaSimples(analise.getPureza() != null ? analise.getPureza().toString() : "-"));
        tabelaResultados.addCell(criarCelulaSimples(analise.getArCana() != null ? analise.getArCana().toString() : "-"));
        tabelaResultados.addCell(criarCelulaSimples(analise.getPolCana() != null ? analise.getPolCana().toString() : "-"));

        document.add(tabelaResultados);

        Paragraph tituloATR = new Paragraph("Resultado Final - ATR")
                .setBold()
                .setFontSize(16)
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginTop(20);
        document.add(tituloATR);

        Table tabelaATR = new Table(UnitValue.createPercentArray(new float[]{1}));
        tabelaATR.setWidth(UnitValue.createPercentValue(50));

        Cell celulaATR = new Cell().add(new Paragraph(analise.getAtr() != null ? analise.getAtr().toString() : "N/A"))
                .setFontSize(22)
                .setBold()
                .setTextAlignment(TextAlignment.CENTER)
                .setPadding(10);
        tabelaATR.addCell(celulaATR);

        tabelaATR.setHorizontalAlignment(HorizontalAlignment.CENTER);

        document.add(tabelaATR);

        document.close();

        return new ByteArrayInputStream(baos.toByteArray());
    }

private Cell criarCelulaCabecalho(String texto) {
        return new Cell().add(new Paragraph(texto))
                .setBold()
                .setFontSize(9)
                .setBackgroundColor(DeviceGray.GRAY)
                .setTextAlignment(TextAlignment.LEFT)
                .setPadding(5);
    }

    private Cell criarCelulaSimples(String texto) {
        return new Cell().add(new Paragraph(texto))
                .setFontSize(10)
                .setTextAlignment(TextAlignment.LEFT)
                .setPadding(5);
    }

    //EXCEL
    public ByteArrayInputStream gerarRelatorioExcel(List<Analises> analises, String layout) throws IOException {

        try (XSSFWorkbook workbook = new XSSFWorkbook(); ByteArrayOutputStream baos = new ByteArrayOutputStream()) {

            XSSFSheet sheet = workbook.createSheet("Relatório de Análises");

            CellStyle headerStyle = workbook.createCellStyle();
            Font font = workbook.createFont();
            font.setBold(true);
            headerStyle.setFont(font);

            Row headerRow = sheet.createRow(0);

            String[] colunas;
            switch (layout.toLowerCase()) {
                case "nova_america":
                    colunas = new String[]{"Sequência", "Código", "Zona", "Talhão"};
                    break;
                case "agroterenas":
                    colunas = new String[]{"Sequência", "Zona", "Talhão"};
                    break;
                default:
                    colunas = new String[]{"ID Análise", "Data", "Fornecedor", "Propriedade", "Talhão", "Zona", "Corte", "ATR"};
                    break;
            }

            for (int i = 0; i < colunas.length; i++) {
                org.apache.poi.ss.usermodel.Cell cell = headerRow.createCell(i);
                cell.setCellValue(colunas[i]);
                cell.setCellStyle(headerStyle);
            }

            int rowNum = 1;
            for (Analises analise : analises) {
                Row row = sheet.createRow(rowNum++);
                preencherLinhaExcel(row, analise, layout);
            }

            for (int i = 0; i < colunas.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(baos);
            return new ByteArrayInputStream(baos.toByteArray());

        } catch (IOException e) {
            System.err.println("Erro ao gerar arquivo Excel: " + e.getMessage());
            throw new IOException("Erro ao gerar relatório Excel", e);
        }
    }

    private void preencherLinhaExcel(Row row, Analises analise, String layout) {

        switch (layout.toLowerCase()) {
            case "nova_america":
                criarCelulaExcel(row, 0, analise.getIdAnalise());
                criarCelulaExcel(row, 1, analise.getNumeroAmostra());
                criarCelulaExcel(row, 2, analise.getZona());
                criarCelulaExcel(row, 3, analise.getTalhao());
                break;

            case "agroterenas":
                criarCelulaExcel(row, 0, analise.getIdAnalise());
                criarCelulaExcel(row, 1, analise.getZona());
                criarCelulaExcel(row, 2, analise.getTalhao());
                break;

            default:
                criarCelulaExcel(row, 0, analise.getIdAnalise());
                criarCelulaExcel(row, 1, analise.getDataAnalise().format(DATE_FORMATTER));
                criarCelulaExcel(row, 2, analise.getPropriedade().getFornecedor().getNome());
                criarCelulaExcel(row, 3, analise.getPropriedade().getNome());
                criarCelulaExcel(row, 4, analise.getTalhao());
                criarCelulaExcel(row, 5, analise.getZona());
                criarCelulaExcel(row, 6, analise.getCorte());
                criarCelulaExcel(row, 7, analise.getAtr());
                break;
        }
    }

    private void criarCelulaExcel(Row row, int colNum, Object valor) {
        org.apache.poi.ss.usermodel.Cell cell = row.createCell(colNum);
        if (valor == null) {
            cell.setCellValue("-");
        } else if (valor instanceof String) {
            cell.setCellValue((String) valor);
        } else if (valor instanceof Long) {
            cell.setCellValue((Long) valor);
        } else if (valor instanceof Integer) {
            cell.setCellValue((Integer) valor);
        } else if (valor instanceof BigDecimal) {
            cell.setCellValue(((BigDecimal) valor).doubleValue());
        } else {
            cell.setCellValue(valor.toString());
        }
    }
}