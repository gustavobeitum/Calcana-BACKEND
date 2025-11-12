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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;

@Service
public class ReportService {

    @Autowired
    private AnaliseService analiseService;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

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
}