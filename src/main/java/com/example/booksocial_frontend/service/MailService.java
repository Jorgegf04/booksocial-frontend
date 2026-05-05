package com.example.booksocial_frontend.service;

import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.example.booksocial_frontend.dto.OrderLineResponseDTO;
import com.example.booksocial_frontend.dto.OrderResponseDTO;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class MailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username:}")
    private String fromEmail;

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    // ── Confirmación de pedido ─────────────────────────────────────────────────

    public void sendOrderConfirmation(String toEmail, String username, OrderResponseDTO order) {
        if (toEmail == null || toEmail.isBlank()) return;
        try {
            String subject = "¡Pedido confirmado! #" + order.getId() + " — BookSocial";
            String body = buildOrderConfirmationHtml(username, order);
            send(toEmail, subject, body);
        } catch (Exception e) {
            log.warn("No se pudo enviar confirmación de pedido a {}: {}", toEmail, e.getMessage());
        }
    }

    private String buildOrderConfirmationHtml(String username, OrderResponseDTO order) {
        StringBuilder sb = new StringBuilder();
        sb.append(header("¡Tu pedido ha sido confirmado!"));
        sb.append("<p style='margin:0 0 12px;'>Hola <strong>").append(escape(username)).append("</strong>,</p>");
        sb.append("<p style='margin:0 0 20px;'>Hemos recibido tu pedido correctamente. Aquí tienes el resumen:</p>");

        sb.append("<table style='width:100%;border-collapse:collapse;margin-bottom:20px;'>");
        sb.append("<tr style='background:#f5f0e8;'>");
        sb.append("<th style='text-align:left;padding:8px 12px;font-size:13px;color:#5a4a3a;'>Producto</th>");
        sb.append("<th style='text-align:center;padding:8px 12px;font-size:13px;color:#5a4a3a;'>Cant.</th>");
        sb.append("<th style='text-align:right;padding:8px 12px;font-size:13px;color:#5a4a3a;'>Subtotal</th>");
        sb.append("</tr>");

        List<OrderLineResponseDTO> lines = order.getOrderLines();
        if (lines != null) {
            for (OrderLineResponseDTO line : lines) {
                sb.append("<tr style='border-bottom:1px solid #e8e0d0;'>");
                sb.append("<td style='padding:8px 12px;font-size:13px;'>").append(escape(line.getTitle() != null ? line.getTitle() : "—")).append("</td>");
                sb.append("<td style='padding:8px 12px;font-size:13px;text-align:center;'>").append(line.getQuantity() != null ? line.getQuantity() : 1).append("</td>");
                sb.append("<td style='padding:8px 12px;font-size:13px;text-align:right;'>€").append(line.getSubtotal() != null ? String.format("%.2f", line.getSubtotal()) : "—").append("</td>");
                sb.append("</tr>");
            }
        }
        sb.append("</table>");

        sb.append("<p style='text-align:right;font-size:16px;font-weight:700;color:#2d7a4f;margin-bottom:24px;'>Total: €")
          .append(order.getTotal() != null ? String.format("%.2f", order.getTotal()) : "—")
          .append("</p>");

        if (order.getDate() != null) {
            sb.append("<p style='font-size:12px;color:#8a7a6a;margin-bottom:20px;'>Pedido realizado el ")
              .append(order.getDate().format(FMT)).append("</p>");
        }

        sb.append("<p style='margin:0;'>Recibirás una notificación cuando cambie el estado de tu pedido.</p>");
        sb.append(footer());
        return sb.toString();
    }

    // ── Nueva obra de un autor seguido ─────────────────────────────────────────

    public void sendNewWorkNotification(String toEmail, String username, String workTitle, String authorName) {
        if (toEmail == null || toEmail.isBlank()) return;
        try {
            String subject = "Nueva obra de " + authorName + " — BookSocial";
            String body = buildNewWorkHtml(username, workTitle, authorName);
            send(toEmail, subject, body);
        } catch (Exception e) {
            log.warn("No se pudo enviar notificación de obra a {}: {}", toEmail, e.getMessage());
        }
    }

    private String buildNewWorkHtml(String username, String workTitle, String authorName) {
        StringBuilder sb = new StringBuilder();
        sb.append(header("Nueva obra disponible"));
        sb.append("<p style='margin:0 0 12px;'>Hola <strong>").append(escape(username)).append("</strong>,</p>");
        sb.append("<p style='margin:0 0 20px;'>Un autor que sigues acaba de publicar una nueva obra en BookSocial:</p>");

        sb.append("<div style='background:#f5f0e8;border-left:4px solid #2d7a4f;padding:16px 20px;border-radius:4px;margin-bottom:24px;'>");
        sb.append("<p style='margin:0 0 6px;font-size:16px;font-weight:700;color:#1a1a1a;'>").append(escape(workTitle)).append("</p>");
        sb.append("<p style='margin:0;font-size:13px;color:#5a4a3a;'>Autor: <strong>").append(escape(authorName)).append("</strong></p>");
        sb.append("</div>");

        sb.append("<p style='margin:0;'>Entra en BookSocial para explorarla y añadirla a tu biblioteca.</p>");
        sb.append(footer());
        return sb.toString();
    }

    // ── Actualización de estado de pedido ──────────────────────────────────────

    public void sendOrderStatusUpdate(String toEmail, String username, Long orderId, String statusLabel) {
        if (toEmail == null || toEmail.isBlank()) return;
        try {
            String subject = "Tu pedido #" + orderId + " ha sido actualizado — BookSocial";
            String body = buildOrderStatusHtml(username, orderId, statusLabel);
            send(toEmail, subject, body);
        } catch (Exception e) {
            log.warn("No se pudo enviar actualización de estado a {}: {}", toEmail, e.getMessage());
        }
    }

    private String buildOrderStatusHtml(String username, Long orderId, String statusLabel) {
        StringBuilder sb = new StringBuilder();
        sb.append(header("Actualización de tu pedido"));
        sb.append("<p style='margin:0 0 12px;'>Hola <strong>").append(escape(username)).append("</strong>,</p>");
        sb.append("<p style='margin:0 0 20px;'>El estado de tu pedido <strong>#").append(orderId).append("</strong> ha sido actualizado:</p>");

        sb.append("<div style='background:#f5f0e8;border-left:4px solid #2d7a4f;padding:16px 20px;border-radius:4px;margin-bottom:24px;'>");
        sb.append("<p style='margin:0;font-size:18px;font-weight:700;color:#2d7a4f;'>").append(escape(statusLabel)).append("</p>");
        sb.append("</div>");

        sb.append("<p style='margin:0;'>Puedes consultar el detalle de tu pedido desde tu historial en BookSocial.</p>");
        sb.append(footer());
        return sb.toString();
    }

    // ── Utilidades HTML ────────────────────────────────────────────────────────

    private String header(String title) {
        return """
            <div style='font-family:Georgia,serif;max-width:600px;margin:0 auto;'>
            <div style='background:#1a1a1a;padding:24px 32px;border-radius:8px 8px 0 0;'>
              <h1 style='margin:0;color:#f5f0e8;font-size:22px;letter-spacing:1px;'>📚 BookSocial</h1>
            </div>
            <div style='background:#ffffff;padding:32px;border:1px solid #e8e0d0;border-top:none;'>
            <h2 style='margin:0 0 20px;color:#1a1a1a;font-size:20px;'>""" + escape(title) + "</h2>";
    }

    private String footer() {
        return """
            </div>
            <div style='background:#f5f0e8;padding:16px 32px;border-radius:0 0 8px 8px;border:1px solid #e8e0d0;border-top:none;'>
              <p style='margin:0;font-size:11px;color:#8a7a6a;text-align:center;'>
                Este mensaje ha sido generado automáticamente por BookSocial. Por favor no respondas a este email.
              </p>
            </div>
            </div>""";
    }

    private String escape(String s) {
        if (s == null) return "";
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;");
    }

    private void send(String to, String subject, String htmlBody) throws Exception {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        helper.setFrom(fromEmail);
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(htmlBody, true);
        mailSender.send(message);
        log.info("Email enviado a {} — {}", to, subject);
    }
}
