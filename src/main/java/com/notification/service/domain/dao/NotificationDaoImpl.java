package com.notification.service.domain.dao;

import com.notification.service.exception.NotificationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import java.util.Arrays;

import static com.notification.service.config.constans.Constants.EMAIL_EXCEPTION_MESSAGE;

@Component
@Slf4j
@RequiredArgsConstructor
public class NotificationDaoImpl {

  @Autowired
  private JavaMailSender mailSender;

  public void sendNotificationMessage(SimpleMailMessage simpleMailMessage){

    try {
      mailSender.send(simpleMailMessage);
      log.info("Correo enviado a {}", Arrays.toString(simpleMailMessage.getTo()));
    } catch (Exception ex) {
      log.error("Client error calling email service exception: {}", ex.toString());
      throw new NotificationException(EMAIL_EXCEPTION_MESSAGE, ex);
    }
  }

}
