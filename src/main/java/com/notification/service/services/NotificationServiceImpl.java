package com.notification.service.services;

import com.notification.service.domain.dao.NotificationDaoImpl;
import com.notification.service.dto.RequestDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Slf4j
@RequiredArgsConstructor
@Service
public class NotificationServiceImpl {

  @Autowired
  private final NotificationDaoImpl notificationDao;

  @Value("${app.mail.from}")
  private String defaultFrom;

  public void execute(RequestDTO requestDTO){
    log.error("Start execute serviceImpl");
    notificationDao.sendNotificationMessage(sendMessage(requestDTO));
  }

  private SimpleMailMessage sendMessage(RequestDTO emailDetail){

    List<String> emailListTo = Objects.requireNonNullElse(emailDetail.getTo(), List.of());
    List<String> emailListCc = Objects.requireNonNullElse(emailDetail.getCc(), List.of());

    if (emailListTo.isEmpty()) {
      throw new IllegalArgumentException("La lista 'to' no puede estar vacía");
    }

    SimpleMailMessage msg = new SimpleMailMessage();
    msg.setFrom(defaultFrom);
    msg.setTo(emailListTo.toArray(new String[0]));
    if (!emailListCc.isEmpty()) {
      msg.setCc(emailListCc.toArray(new String[0]));
    }
    msg.setSubject(emailDetail.getSubject());
    msg.setText(emailDetail.getBody());

    return msg;

  }
}
