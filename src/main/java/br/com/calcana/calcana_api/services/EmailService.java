package br.com.calcana.calcana_api.services;

import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${calcana.email.remetente}")
    private String remetente;

    public void enviarEmailComAnexo(String destinatario, String assunto, String corpo, byte[] anexoBytes, String nomeAnexo) {

        try {
            MimeMessage message = mailSender.createMimeMessage();

            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(remetente);
            helper.setTo(destinatario);
            helper.setSubject(assunto);
            helper.setText(corpo, false);

            ByteArrayResource pdfResource = new ByteArrayResource(anexoBytes);

            helper.addAttachment(nomeAnexo, pdfResource, "application/pdf");

            mailSender.send(message);

            System.out.println("E-mail enviado com sucesso para: " + destinatario);

        } catch (Exception e) {
            System.err.println("Erro ao enviar e-mail: " + e.getMessage());
            throw new RuntimeException("Falha ao enviar o e-mail com anexo.", e);
        }
    }
}