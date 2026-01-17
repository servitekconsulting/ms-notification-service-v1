
package com.notification.service.domain.dao;

import com.notification.service.exception.NotificationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.util.ReflectionTestUtils;

import static com.notification.service.config.constans.Constants.EMAIL_EXCEPTION_MESSAGE;
import static com.notification.service.config.utils.RequestDTOMock.buildMessage;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationDaoImplTest {

  private JavaMailSender mailSender;
  private NotificationDaoImpl dao;

  @BeforeEach
  void setUp() {
    mailSender = Mockito.mock(JavaMailSender.class);
    dao = new NotificationDaoImpl();
    ReflectionTestUtils.setField(dao, "mailSender", mailSender);
  }


  @Test
  @DisplayName("Should call JavaMailSender.send once without throwing")
  void shouldCallJavaMailSenderOnce() {
    SimpleMailMessage message = buildMessage();
    dao.sendNotificationMessage(message);
    verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
    verifyNoMoreInteractions(mailSender);
    assertThat(message.getFrom()).isEqualTo("notificaciones@test-eqvygm09vv5l0p7w.mlsender.net");
    assertThat(message.getTo()).containsExactly("destino1@example.com", "destino2@example.com");
    assertThat(message.getCc()).containsExactly("copia@example.com");
    assertThat(message.getSubject()).isEqualTo("Test subject");
    assertThat(message.getText()).isEqualTo("Test body");
  }


  @Test
  @DisplayName("Should wrap underlying exception into NotificationException and preserve cause")
  void shouldWrapExceptionAndPreserveCause() {
    SimpleMailMessage message = buildMessage();
    RuntimeException providerFailure = new RuntimeException("SMTP provider down");

    doThrow(providerFailure).when(mailSender).send(any(SimpleMailMessage.class));
    NotificationException thrown = assertThrows(
            NotificationException.class,
            () -> dao.sendNotificationMessage(message)
    );

    assertThat(thrown)
            .hasMessage(EMAIL_EXCEPTION_MESSAGE)
            .hasCauseReference(providerFailure); // preserve exact cause

    verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
    verifyNoMoreInteractions(mailSender);
  }

}
