package com.notification.service.impl;

import com.notification.service.domain.dao.NotificationDaoImpl;
import com.notification.service.dto.RequestDTO;
import com.notification.service.services.NotificationServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static com.notification.service.config.utils.RequestDTOMock.buildRequest;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationServiceImplTest {

  @Mock
  private NotificationDaoImpl notificationDao;

  @InjectMocks
  private NotificationServiceImpl service;

  private static final String DEFAULT_FROM = "notificaciones@test-eqvygm09vv5l0p7w.mlsender.net";

  @BeforeEach
  void setUp() {
    ReflectionTestUtils.setField(service, "defaultFrom", DEFAULT_FROM);
  }

  @Nested
  @DisplayName("execute(requestDTO) - Happy path")
  class HappyPath {

    @Test
    @DisplayName("Construye el SimpleMailMessage correctamente y llama al DAO una sola vez")
    void shouldBuildMessageAndCallDaoOnce() {
      RequestDTO request = buildRequest(
              List.of("destino1@example.com", "destino2@example.com"),
              List.of("copia@example.com"),
              "Asunto de prueba",
              "Cuerpo del mensaje"
      );

      ArgumentCaptor<SimpleMailMessage> msgCaptor = ArgumentCaptor.forClass(SimpleMailMessage.class);

      service.execute(request);

      verify(notificationDao, times(1)).sendNotificationMessage(msgCaptor.capture());
      verifyNoMoreInteractions(notificationDao);

      SimpleMailMessage msg = msgCaptor.getValue();
      assertThat(msg.getFrom()).isEqualTo(DEFAULT_FROM);
      assertThat(msg.getTo()).containsExactly("destino1@example.com", "destino2@example.com");
      assertThat(msg.getCc()).containsExactly("copia@example.com");
      assertThat(msg.getSubject()).isEqualTo("Asunto de prueba");
      assertThat(msg.getText()).isEqualTo("Cuerpo del mensaje");
    }
  }

  @Nested
  @DisplayName("execute(requestDTO) - Validaciones de listas")
  class ListValidations {

    @Test
    @DisplayName("'to' vacío ⇒ IllegalArgumentException y NO se llama al DAO")
    void emptyTo_shouldThrowAndNotCallDao() {
      RequestDTO request = buildRequest(
              List.of(),                      // vacío
              List.of("copia@example.com"),
              "Asunto",
              "Body"
      );

      IllegalArgumentException ex = assertThrows(
              IllegalArgumentException.class,
              () -> service.execute(request)
      );
      assertThat(ex).hasMessageContaining("La lista 'to' no puede estar vacía");

      verifyNoInteractions(notificationDao);
    }

    @Test
    @DisplayName("'to' nulo ⇒ IllegalArgumentException (se convierte en lista vacía por requireNonNullElse)")
    void nullTo_shouldThrowAndNotCallDao() {
      RequestDTO request = buildRequest(
              null,                           // nulo
              List.of("copia@example.com"),
              "Asunto",
              "Body"
      );
      IllegalArgumentException ex = assertThrows(
              IllegalArgumentException.class,
              () -> service.execute(request)
      );
      assertThat(ex).hasMessageContaining("La lista 'to' no puede estar vacía");

      verifyNoInteractions(notificationDao);
    }

    @Test
    @DisplayName("'cc' vacío ⇒ no se setea CC; aún así envía correctamente")
    void emptyCc_shouldNotSetCcAndStillSend() {
      RequestDTO request = buildRequest(
              List.of("destino1@example.com"),
              List.of(),                      // cc vacío
              "Asunto",
              "Body"
      );

      ArgumentCaptor<SimpleMailMessage> msgCaptor = ArgumentCaptor.forClass(SimpleMailMessage.class);

      service.execute(request);

      verify(notificationDao, times(1)).sendNotificationMessage(msgCaptor.capture());
      verifyNoMoreInteractions(notificationDao);

      SimpleMailMessage msg = msgCaptor.getValue();
      assertThat(msg.getFrom()).isEqualTo(DEFAULT_FROM);
      assertThat(msg.getTo()).containsExactly("destino1@example.com");
      assertThat(msg.getCc()).isNull();
      assertThat(msg.getSubject()).isEqualTo("Asunto");
      assertThat(msg.getText()).isEqualTo("Body");
    }
  }

  @Nested
  @DisplayName("execute(requestDTO) - Mapeo de campos")
  class FieldMapping {

    @Test
    @DisplayName("Asigna correctamente subject y body al mensaje")
    void subjectAndBody_areMapped() {
      RequestDTO request = buildRequest(
              List.of("a@b.com"),
              null, // cc nulo
              "Sujeto X",
              "Contenido Y"
      );

      ArgumentCaptor<SimpleMailMessage> msgCaptor = ArgumentCaptor.forClass(SimpleMailMessage.class);
      service.execute(request);

      verify(notificationDao, times(1)).sendNotificationMessage(msgCaptor.capture());
      SimpleMailMessage msg = msgCaptor.getValue();

      assertThat(msg.getSubject()).isEqualTo("Sujeto X");
      assertThat(msg.getText()).isEqualTo("Contenido Y");
    }
  }
}