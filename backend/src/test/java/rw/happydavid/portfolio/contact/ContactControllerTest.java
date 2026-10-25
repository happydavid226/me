package rw.happydavid.portfolio.contact;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.mail.MailSendException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@SpringBootTest
@AutoConfigureMockMvc
class ContactControllerTest {
    @Autowired MockMvc mvc;
    @MockitoBean JavaMailSender mailSender;

    @Test
    void validRequestSendsPlainTextMailWithReplyTo() throws Exception {
        mvc.perform(post("/api/contact").header("X-Real-IP", "192.0.2.11")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"  Ada Lovelace  \",\"email\":\"ada@example.com\",\"message\":\"  Hello, I would like to collaborate.  \",\"website\":\"\"}"))
                .andExpect(status().isAccepted()).andExpect(jsonPath("$.message").value("Your message was accepted by the mail server."));
        org.mockito.ArgumentCaptor<SimpleMailMessage> captor = org.mockito.ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender).send(captor.capture());
        SimpleMailMessage sent = captor.getValue();
        org.junit.jupiter.api.Assertions.assertEquals("owner@example.test", sent.getTo()[0]);
        org.junit.jupiter.api.Assertions.assertEquals("ada@example.com", sent.getReplyTo());
        org.junit.jupiter.api.Assertions.assertTrue(sent.getText().contains("Ada Lovelace"));
        org.junit.jupiter.api.Assertions.assertTrue(sent.getText().contains("Hello, I would like to collaborate."));
        org.junit.jupiter.api.Assertions.assertFalse(sent.getText().contains("<html>"));
    }

    @Test
    void rejectsInvalidEmailAndBlankMessage() throws Exception {
        mvc.perform(post("/api/contact").header("X-Real-IP", "192.0.2.12").contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"Visitor\",\"email\":\"bad\",\"message\":\"          \",\"website\":\"\"}"))
                .andExpect(status().isBadRequest()).andExpect(jsonPath("$.message").exists());
        mvc.perform(post("/api/contact").header("X-Real-IP", "192.0.2.17").contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"Visitor\",\"email\":\"v@example.com\",\"message\":\"           x           \",\"website\":\"\"}"))
                .andExpect(status().isBadRequest());
        verify(mailSender, never()).send(any(SimpleMailMessage.class));
    }

    @Test
    void honeypotAcceptsSilentlyWithoutSendingMail() throws Exception {
        mvc.perform(post("/api/contact").header("X-Real-IP", "192.0.2.13").contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"Automated visitor\",\"email\":\"bot@example.com\",\"message\":\"This message is long enough.\",\"website\":\"filled\"}"))
                .andExpect(status().isAccepted());
        verify(mailSender, never()).send(any(SimpleMailMessage.class));
    }

    @Test
    void mailFailureReturnsSafeUnavailableResponse() throws Exception {
        doThrow(new MailSendException("private smtp diagnostic")).when(mailSender).send(any(SimpleMailMessage.class));
        mvc.perform(post("/api/contact").header("X-Real-IP", "192.0.2.14").contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"Visitor\",\"email\":\"v@example.com\",\"message\":\"This is a sufficiently long note.\",\"website\":\"\"}"))
                .andExpect(status().isServiceUnavailable()).andExpect(jsonPath("$.message").value("The mail service is temporarily unavailable. Please try again later."));
    }

    @Test
    void rateLimitsExcessMessagesFromOneAddress() throws Exception {
        String payload = "{\"name\":\"Visitor\",\"email\":\"v@example.com\",\"message\":\"This is a sufficiently long note.\",\"website\":\"\"}";
        for (int i = 0; i < 5; i++) mvc.perform(post("/api/contact").header("X-Real-IP", "192.0.2.15").contentType(MediaType.APPLICATION_JSON).content(payload)).andExpect(status().isAccepted());
        mvc.perform(post("/api/contact").header("X-Real-IP", "192.0.2.15").contentType(MediaType.APPLICATION_JSON).content(payload))
                .andExpect(status().isTooManyRequests());
    }

    @Test
    void rejectsOversizedRequest() throws Exception {
        String huge = "x".repeat(13 * 1024);
        mvc.perform(post("/api/contact").header("X-Real-IP", "192.0.2.16").contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"" + huge + "\"}"))
                .andExpect(status().isPayloadTooLarge());
    }
}
