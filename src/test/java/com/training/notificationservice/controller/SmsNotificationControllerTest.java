package com.training.notificationservice.controller;

import com.training.notificationservice.dto.response.NotificationResponseDto;
import com.training.notificationservice.enums.NotificationChannel;
import com.training.notificationservice.enums.NotificationStatus;
import com.training.notificationservice.exception.GlobalExceptionHandler;
import com.training.notificationservice.service.NotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class SmsNotificationControllerTest {

    @Mock
    private NotificationService notificationService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        SmsNotificationController controller = new SmsNotificationController(notificationService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    private NotificationResponseDto smsResponse(NotificationStatus status) {
        NotificationResponseDto dto = new NotificationResponseDto();
        dto.setId(UUID.randomUUID());
        dto.setRecipient("+15551234567");
        dto.setChannel(NotificationChannel.SMS);
        dto.setMessage("Your order has shipped");
        dto.setStatus(status);
        dto.setRetryCount(0);
        dto.setCreatedAt(LocalDateTime.now());
        dto.setUpdatedAt(LocalDateTime.now());
        return dto;
    }

    @Test
    void createSmsNotificationReturns201WithSentBody() throws Exception {
        when(notificationService.createNotification(any())).thenReturn(smsResponse(NotificationStatus.SENT));

        String body = """
                {
                  "recipient": "+15551234567",
                  "message": "Your order has shipped"
                }
                """;

        mockMvc.perform(post("/api/v1/notifications/sms")
                        .contentType("application/json")
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.channel").value("SMS"))
                .andExpect(jsonPath("$.status").value("SENT"));
    }

    @Test
    void createSmsNotificationWithInvalidPhoneReturns400() throws Exception {
        String body = """
                {
                  "recipient": "not-a-phone-number",
                  "message": "Your order has shipped"
                }
                """;

        mockMvc.perform(post("/api/v1/notifications/sms")
                        .contentType("application/json")
                        .content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createSmsNotificationWithBlankMessageReturns400() throws Exception {
        String body = """
                {
                  "recipient": "+15551234567",
                  "message": ""
                }
                """;

        mockMvc.perform(post("/api/v1/notifications/sms")
                        .contentType("application/json")
                        .content(body))
                .andExpect(status().isBadRequest());
    }
}
