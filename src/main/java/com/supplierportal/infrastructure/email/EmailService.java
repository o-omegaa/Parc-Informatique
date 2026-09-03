package com.supplierportal.infrastructure.email;

import jakarta.mail.*;
import jakarta.mail.internet.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.Properties;

/**
 * Email service using direct Jakarta Mail API.
 * Uses CID (Content-ID) inline image attachment so logos display in Gmail.
 */
@Slf4j
@Service
public class EmailService {

    @Value("${spring.mail.host:smtp.gmail.com}")
    private String smtpHost;

    @Value("${spring.mail.port:587}")
    private int smtpPort;

    @Value("${spring.mail.username:}")
    private String smtpUsername;

    @Value("${spring.mail.password:}")
    private String smtpPassword;

    @Value("${spring.mail.properties.mail.smtp.ssl.trust:smtp.gmail.com}")
    private String smtpTrust;

    // Path to logo image on server filesystem
    private static final String LOGO_PATH =
            System.getProperty("user.dir") + "/src/main/resources/static/images/login_illustration.jpg";

    // ─── Helper : build SMTP session ─────────────────────────────────────────
    private Session buildSession() {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", smtpHost);
        props.put("mail.smtp.port", String.valueOf(smtpPort));
        props.put("mail.smtp.ssl.trust", smtpTrust);
        props.put("mail.smtp.ssl.protocols", "TLSv1.2");
        return Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(smtpUsername, smtpPassword);
            }
        });
    }

    // ─── Helper : attach logo as CID inline part ─────────────────────────────
    private MimeBodyPart buildLogoPart() throws Exception {
        MimeBodyPart logoPart = new MimeBodyPart();
        File logoFile = new File(LOGO_PATH);
        if (logoFile.exists()) {
            logoPart.attachFile(logoFile);
            logoPart.setContentID("<portalLogo>");
            logoPart.setDisposition(MimeBodyPart.INLINE);
        }
        return logoPart;
    }

    // ─── Helper : build multipart/related message with inline image ──────────
    private void setMultipartContent(MimeMessage message, String htmlBody) throws Exception {
        MimeBodyPart htmlPart = new MimeBodyPart();
        htmlPart.setContent(htmlBody, "text/html; charset=UTF-8");

        MimeBodyPart logoPart = buildLogoPart();

        MimeMultipart related = new MimeMultipart("related");
        related.addBodyPart(htmlPart);
        related.addBodyPart(logoPart);

        message.setContent(related);
    }

    /**
     * Sends a temporary password to a newly registered supplier.
     */
    @Async
    public void sendTemporaryPassword(String toEmail, String username, String temporaryPassword) {
        if (smtpUsername == null || smtpUsername.isBlank() ||
            smtpPassword == null || smtpPassword.isBlank()) {
            log.warn("📧 EMAIL SIMULÉ — Mot de passe pour {} : {}", username, temporaryPassword);
            return;
        }
        try {
            MimeMessage message = new MimeMessage(buildSession());
            message.setFrom(new InternetAddress(smtpUsername, "Portail Fournisseur"));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
            message.setSubject("Portail Fournisseur — Votre mot de passe temporaire", "UTF-8");
            message.setContent(buildEmailHtml(username, temporaryPassword), "text/html; charset=UTF-8");
            Transport.send(message);
            log.info("✅ Mot de passe temporaire envoyé à : {}", toEmail);
        } catch (Exception e) {
            log.error("❌ Échec envoi email à {} : {}", toEmail, e.getMessage());
            log.warn("  ⚠ Mot de passe temporaire pour {} : {}", username, temporaryPassword);
        }
    }

    /**
     * Sends a password reset link email.
     */
    @Async
    public void sendPasswordResetLink(String toEmail, String username, String resetLink, int expiryMinutes) {
        if (smtpUsername == null || smtpUsername.isBlank() ||
            smtpPassword == null || smtpPassword.isBlank()) {
            log.warn("📧 RESET LINK SIMULÉ pour {} : {}", toEmail, resetLink);
            return;
        }
        try {
            MimeMessage message = new MimeMessage(buildSession());
            message.setFrom(new InternetAddress(smtpUsername, "Portail Fournisseur"));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
            message.setSubject("Portail Fournisseur — Réinitialisation de mot de passe", "UTF-8");
            message.setContent(buildResetEmailHtml(username, resetLink, expiryMinutes), "text/html; charset=UTF-8");
            Transport.send(message);
            log.info("✅ Email de réinitialisation envoyé à : {}", toEmail);
        } catch (Exception e) {
            log.error("❌ Échec envoi reset email à {} : {}", toEmail, e.getMessage());
        }
    }

    // ─── Email HTML: Inscription ──────────────────────────────────────────────
    private String buildEmailHtml(String username, String temporaryPassword) {
        return """
            <!DOCTYPE html>
            <html lang="fr">
            <head><meta charset="UTF-8"></head>
            <body style="font-family:Arial,sans-serif;background:#fef3e2;margin:0;padding:20px;">
              <div style="max-width:560px;margin:0 auto;background:#ffffff;border-radius:16px;overflow:hidden;box-shadow:0 4px 20px rgba(0,0,0,0.08);">
                <div style="background:linear-gradient(135deg,#f97316,#ea580c);padding:36px 32px;text-align:center;">
                  <h1 style="color:white;margin:0;font-size:22px;font-weight:800;letter-spacing:0.5px;">Portail Fournisseur</h1>
                  <p style="color:rgba(255,255,255,0.85);margin:6px 0 0;font-size:13px;">Inscription confirmée</p>
                </div>
                <div style="padding:32px;">
                  <p style="font-size:15px;color:#1c1917;">Bonjour <strong>%s</strong>,</p>
                  <p style="font-size:14px;color:#57534e;line-height:1.6;">
                    Votre demande d'inscription sur le <strong>Portail Fournisseur</strong> a bien été reçue.<br>
                    Votre compte est en cours de <strong>validation par l'administrateur</strong>.
                  </p>
                  <div style="background:#fff7ed;border:1.5px solid #fed7aa;border-radius:12px;padding:20px 24px;margin:20px 0;">
                    <div style="margin-bottom:12px;">
                      <span style="font-size:11px;font-weight:700;color:#9a3412;text-transform:uppercase;">Nom d'utilisateur</span>
                      <div style="font-size:16px;font-weight:700;color:#1c1917;margin-top:4px;">%s</div>
                    </div>
                    <div>
                      <span style="font-size:11px;font-weight:700;color:#9a3412;text-transform:uppercase;">Mot de passe temporaire</span>
                      <div style="font-size:20px;font-weight:800;color:#ea580c;letter-spacing:2px;margin-top:4px;font-family:monospace;">%s</div>
                    </div>
                  </div>
                  <div style="background:#fef2f2;border:1px solid #fecaca;border-radius:10px;padding:14px 18px;margin-bottom:24px;">
                    <p style="color:#b91c1c;font-size:13px;font-weight:600;margin:0 0 4px;">⚠️ Important</p>
                    <p style="color:#7f1d1d;font-size:13px;margin:0;">Ce mot de passe est <strong>temporaire</strong>. Vous serez invité(e) à le modifier lors de votre première connexion.</p>
                  </div>
                  <a href="http://192.168.1.37:8080/index.html" style="display:block;text-align:center;background:linear-gradient(135deg,#f97316,#ea580c);color:white;text-decoration:none;padding:14px 28px;border-radius:10px;font-size:15px;font-weight:700;">
                    Se connecter au portail →
                  </a>
                </div>
                <div style="background:#fef9f4;padding:18px 32px;text-align:center;border-top:1px solid #e7d5bf;">
                  <p style="font-size:11px;color:#a8a29e;margin:0;">Email automatique — Ne pas répondre.</p>
                </div>
              </div>
            </body>
            </html>
            """.formatted(username, username, temporaryPassword);
    }

    // ─── Email HTML: Réinitialisation ─────────────────────────────────────────
    private String buildResetEmailHtml(String username, String resetLink, int expiryMinutes) {
        return """
            <!DOCTYPE html>
            <html lang="fr">
            <head><meta charset="UTF-8"></head>
            <body style="font-family:Arial,sans-serif;background:#fef3e2;margin:0;padding:20px;">
              <div style="max-width:560px;margin:0 auto;background:#ffffff;border-radius:16px;overflow:hidden;box-shadow:0 4px 20px rgba(0,0,0,0.08);">
                <div style="background:linear-gradient(135deg,#f97316,#ea580c);padding:36px 32px;text-align:center;">
                  <h1 style="color:white;margin:0;font-size:22px;font-weight:800;letter-spacing:0.5px;">Portail Fournisseur</h1>
                  <p style="color:rgba(255,255,255,0.85);margin:6px 0 0;font-size:13px;">Réinitialisation de mot de passe</p>
                </div>
                <div style="padding:32px;">
                  <p style="font-size:15px;color:#1c1917;">Bonjour <strong>%s</strong>,</p>
                  <p style="font-size:14px;color:#57534e;line-height:1.6;">
                    Vous avez demandé la réinitialisation de votre mot de passe.<br>
                    Cliquez sur le bouton ci-dessous pour définir un nouveau mot de passe.
                  </p>
                  <div style="text-align:center;margin:28px 0;">
                    <a href="%s" style="display:inline-block;background:linear-gradient(135deg,#f97316,#ea580c);color:white;text-decoration:none;padding:14px 36px;border-radius:10px;font-size:15px;font-weight:700;">
                      🔐 Réinitialiser mon mot de passe
                    </a>
                  </div>
                  <div style="background:#fef2f2;border:1px solid #fecaca;border-radius:10px;padding:14px 18px;margin-bottom:24px;">
                    <p style="color:#b91c1c;font-size:13px;font-weight:600;margin:0 0 4px;">⚠️ Ce lien expire dans %d minutes</p>
                    <p style="color:#7f1d1d;font-size:13px;margin:0;">Si vous n'avez pas demandé cette réinitialisation, ignorez cet email.</p>
                  </div>
                </div>
                <div style="background:#fef9f4;padding:18px 32px;text-align:center;border-top:1px solid #e7d5bf;">
                  <p style="font-size:11px;color:#a8a29e;margin:0;">Email automatique — Ne pas répondre.</p>
                </div>
              </div>
            </body>
            </html>
            """.formatted(username, resetLink, expiryMinutes);
    }
}
