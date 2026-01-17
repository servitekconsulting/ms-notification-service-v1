package com.notification.service.config.utils;

import com.notification.service.dto.RequestDTO;
import org.springframework.mail.SimpleMailMessage;

import java.util.List;

public class RequestDTOMock {

  public static RequestDTO buildRequest() {
    RequestDTO dto = new RequestDTO();
    dto.setTo(List.of("destino1@example.com", "destino2@example.com"));
    dto.setCc(List.of("copia@example.com"));
    dto.setSubject("Asunto de prueba");
    dto.setBody("Cuerpo del mensaje");
    return dto;
  }


  public static RequestDTO buildRequest(List<String> to, List<String> cc, String subject, String body) {
    RequestDTO dto = new RequestDTO();
    dto.setTo(to);
    dto.setCc(cc);
    dto.setSubject(subject);
    dto.setBody(body);
    return dto;
  }


  public static SimpleMailMessage buildMessage() {
    SimpleMailMessage msg = new SimpleMailMessage();
    msg.setFrom("notificaciones@test-eqvygm09vv5l0p7w.mlsender.net");
    msg.setTo("destino1@example.com", "destino2@example.com");
    msg.setCc("copia@example.com");
    msg.setSubject("Test subject");
    msg.setText("Test body");
    return msg;
  }
}
