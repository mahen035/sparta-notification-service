package com.training.notificationservice.service.impl;

import com.training.notificationservice.dto.request.NotificationTemplateRequestDto;
import com.training.notificationservice.dto.response.NotificationTemplateResponseDto;
import com.training.notificationservice.entity.NotificationTemplate;
import com.training.notificationservice.enums.NotificationChannel;
import com.training.notificationservice.exception.DuplicateTemplateNameException;
import com.training.notificationservice.exception.NotificationTemplateNotFoundException;
import com.training.notificationservice.repository.NotificationTemplateRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NotificationTemplateServiceImplTest {

    @Mock
    private NotificationTemplateRepository templateRepository;

    @InjectMocks
    private NotificationTemplateServiceImpl templateService;

    private NotificationTemplateRequestDto validRequest() {
        NotificationTemplateRequestDto request = new NotificationTemplateRequestDto();
        request.setName("order-confirmed-email");
        request.setChannel(NotificationChannel.EMAIL);
        request.setSubjectTemplate("Your order is confirmed");
        request.setBodyTemplate("Hi {{name}}, your order #{{orderId}} is confirmed.");
        return request;
    }

    @Test
    void create_withUniqueName_persistsAndReturnsDto() {
        NotificationTemplateRequestDto request = validRequest();
        when(templateRepository.existsByName(request.getName())).thenReturn(false);
        when(templateRepository.save(any(NotificationTemplate.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        NotificationTemplateResponseDto result = templateService.create(request);

        assertThat(result.getName()).isEqualTo(request.getName());
        assertThat(result.getChannel()).isEqualTo(NotificationChannel.EMAIL);
        assertThat(result.getBodyTemplate()).isEqualTo(request.getBodyTemplate());
        verify(templateRepository).save(any(NotificationTemplate.class));
    }

    @Test
    void create_withDuplicateName_throwsConflict() {
        NotificationTemplateRequestDto request = validRequest();
        when(templateRepository.existsByName(request.getName())).thenReturn(true);

        assertThatThrownBy(() -> templateService.create(request))
                .isInstanceOf(DuplicateTemplateNameException.class);
    }

    @Test
    void getById_whenFound_returnsMappedDto() {
        UUID id = UUID.randomUUID();
        NotificationTemplate entity = new NotificationTemplate();
        entity.setId(id);
        entity.setName("order-confirmed-email");
        entity.setChannel(NotificationChannel.EMAIL);
        entity.setBodyTemplate("Hi there");
        when(templateRepository.findById(id)).thenReturn(Optional.of(entity));

        NotificationTemplateResponseDto result = templateService.getById(id);

        assertThat(result.getId()).isEqualTo(id);
        assertThat(result.getName()).isEqualTo("order-confirmed-email");
    }

    @Test
    void getById_whenMissing_throwsNotFound() {
        UUID id = UUID.randomUUID();
        when(templateRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> templateService.getById(id))
                .isInstanceOf(NotificationTemplateNotFoundException.class)
                .hasMessageContaining(id.toString());
    }

    @Test
    void update_withUnchangedName_savesSuccessfully() {
        UUID id = UUID.randomUUID();
        NotificationTemplate existing = new NotificationTemplate();
        existing.setId(id);
        existing.setName("order-confirmed-email");
        existing.setChannel(NotificationChannel.EMAIL);
        existing.setBodyTemplate("old body");
        when(templateRepository.findById(id)).thenReturn(Optional.of(existing));
        when(templateRepository.findByName("order-confirmed-email")).thenReturn(Optional.of(existing));
        when(templateRepository.save(any(NotificationTemplate.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        NotificationTemplateResponseDto result = templateService.update(id, validRequest());

        assertThat(result.getBodyTemplate()).isEqualTo(validRequest().getBodyTemplate());
    }

    @Test
    void update_whenRenamingToAnotherTemplatesName_throwsConflict() {
        UUID id = UUID.randomUUID();
        NotificationTemplate existing = new NotificationTemplate();
        existing.setId(id);
        existing.setName("old-name");
        existing.setChannel(NotificationChannel.EMAIL);
        existing.setBodyTemplate("old body");
        when(templateRepository.findById(id)).thenReturn(Optional.of(existing));

        NotificationTemplate other = new NotificationTemplate();
        other.setId(UUID.randomUUID());
        other.setName("taken-name");
        when(templateRepository.findByName("taken-name")).thenReturn(Optional.of(other));

        NotificationTemplateRequestDto request = validRequest();
        request.setName("taken-name");

        assertThatThrownBy(() -> templateService.update(id, request))
                .isInstanceOf(DuplicateTemplateNameException.class);
    }

    @Test
    void update_whenMissing_throwsNotFound() {
        UUID id = UUID.randomUUID();
        when(templateRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> templateService.update(id, validRequest()))
                .isInstanceOf(NotificationTemplateNotFoundException.class);
    }

    @Test
    void delete_whenFound_removesTemplate() {
        UUID id = UUID.randomUUID();
        NotificationTemplate entity = new NotificationTemplate();
        entity.setId(id);
        when(templateRepository.findById(id)).thenReturn(Optional.of(entity));

        templateService.delete(id);

        verify(templateRepository).delete(entity);
    }

    @Test
    void delete_whenMissing_throwsNotFound() {
        UUID id = UUID.randomUUID();
        when(templateRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> templateService.delete(id))
                .isInstanceOf(NotificationTemplateNotFoundException.class);
    }
}
