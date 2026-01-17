package com.notification.service.web.controller;

import com.notification.service.dto.RequestDTO;
import com.notification.service.dto.ResponseDTO;
import com.notification.service.services.NotificationServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;


import static com.notification.service.config.utils.RequestDTOMock.buildRequest;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.doThrow;

@ExtendWith(MockitoExtension.class)
class NotificationControllerTest {

  @Mock
  private NotificationServiceImpl notificationService;

  @InjectMocks
  private NotificationController controller;



  @Test
  @DisplayName("Should return ok when call service is correct")
  void shouldReturnOkWhenCallServiceIsCorrect() {
    RequestDTO request = buildRequest();
    ResponseEntity<ResponseDTO> response = controller.notification(request);

    assertThat(response.getStatusCode().value()).isEqualTo(200);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().getStatus()).isEqualTo("EMAIL SENT");
    assertThat(response.getBody().getTo()).isEqualTo("ALL");

    ArgumentCaptor<RequestDTO> captor = ArgumentCaptor.forClass(RequestDTO.class);
    verify(notificationService, times(1)).execute(captor.capture());
    verifyNoMoreInteractions(notificationService);

    RequestDTO captured = captor.getValue();
    assertThat(captured.getTo()).containsExactly("destino1@example.com", "destino2@example.com");
    assertThat(captured.getCc()).containsExactly("copia@example.com");
    assertThat(captured.getSubject()).isEqualTo("Asunto de prueba");
    assertThat(captured.getBody()).isEqualTo("Cuerpo del mensaje");
  }

  @Test
  @DisplayName("Should throw ExceptionWhenService Fails")
  void shouldThrowExceptionWhenServiceFail() {
    RequestDTO request = buildRequest();
    doThrow(new RuntimeException("Falla simulada"))
            .when(notificationService).execute(any(RequestDTO.class));
    RuntimeException thrown = assertThrows(RuntimeException.class, () -> controller.notification(request));
    assertThat(thrown).hasMessageContaining("Falla simulada");

    verify(notificationService, times(1)).execute(any(RequestDTO.class));
    verifyNoMoreInteractions(notificationService);
  }
}
