package com.training.notificationservice.controller;

import com.training.notificationservice.dto.response.InAppNotificationResponseDto;
import com.training.notificationservice.enums.NotificationChannel;
import com.training.notificationservice.enums.NotificationStatus;
import com.training.notificationservice.exception.GlobalExceptionHandler;
import com.training.notificationservice.exception.NotificationNotFoundException;
import com.training.notificationservice.service.InAppNotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Tests for the in-app endpoints. Create/mark-as-read go through standalone
 * MockMvc (with the shared {@link GlobalExceptionHandler} wired in, mirroring
 * the Email/SMS controller tests) so status-code and JSON mapping are exercised.
 * The paginated list endpoint is verified by a direct method call, matching
 * {@code NotificationHistoryControllerTest} - a {@code Page} is not serialized
 * as-is over HTTP, so asserting on the returned {@link ResponseEntity} keeps the
 * test focused on the controller's own delegation logic.
 */
@ExtendWith(MockitoExtension.class)
class InAppNotificationControllerTest {

    @Mock
    private InAppNotificationService service;

    private InAppNotificationController controller;
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        controller = new InAppNotificationController(service);
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    private InAppNotificationResponseDto inAppResponse(boolean isRead, NotificationStatus status) {
        return new InAppNotificationResponseDto(
                UUID.randomUUID(),
                "user-42",
                NotificationChannel.IN_APP,
                "Order update",
                "Your order has shipped",
                status,
                isRead,
                LocalDateTime.now(),
                LocalDateTime.now());
    }

    @Test
    void createInAppNotificationReturns201WithUnreadBody() throws Exception {
        when(service.create(any())).thenReturn(inAppResponse(false, NotificationStatus.SENT));

        String body = """
                {
                  "recipient": "user-42",
                  "subject": "Order update",
                  "message": "Your order has shipped"
                }
                """;

        mockMvc.perform(post("/api/v1/notifications/in-app")
                        .contentType("application/json")
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.channel").value("IN_APP"))
                .andExpect(jsonPath("$.isRead").value(false));
    }

    @Test
    void createInAppNotificationWithBlankRecipientReturns400() throws Exception {
        String body = """
                {
                  "recipient": "",
                  "message": "Your order has shipped"
                }
                """;

        mockMvc.perform(post("/api/v1/notifications/in-app")
                        .contentType("application/json")
                        .content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createInAppNotificationWithBlankMessageReturns400() throws Exception {
        String body = """
                {
                  "recipient": "user-42",
                  "message": ""
                }
                """;

        mockMvc.perform(post("/api/v1/notifications/in-app")
                        .contentType("application/json")
                        .content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    void listForUserDelegatesToServiceAndReturnsPage() {
        Pageable pageable = PageRequest.of(0, 20);
        Page<InAppNotificationResponseDto> page =
                new PageImpl<>(List.of(inAppResponse(false, NotificationStatus.SENT)));
        when(service.listForUser("user-42", pageable)).thenReturn(page);

        ResponseEntity<Page<InAppNotificationResponseDto>> response =
                controller.listForUser("user-42", pageable);

        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getBody()).isEqualTo(page);
        assertThat(response.getBody().getContent().get(0).recipient()).isEqualTo("user-42");
        verify(service).listForUser("user-42", pageable);
    }

    @Test
    void markAsReadReturns200WithReadBody() throws Exception {
        UUID id = UUID.randomUUID();
        when(service.markAsRead(id)).thenReturn(inAppResponse(true, NotificationStatus.SENT));

        mockMvc.perform(patch("/api/v1/notifications/in-app/{id}/read", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isRead").value(true));
    }

    @Test
    void markAsReadOnMissingIdReturns404() throws Exception {
        UUID id = UUID.randomUUID();
        when(service.markAsRead(id))
                .thenThrow(new NotificationNotFoundException("In-app notification not found with id: " + id));

        mockMvc.perform(patch("/api/v1/notifications/in-app/{id}/read", id))
                .andExpect(status().isNotFound());
    }
}
