package com.notification.service.web.controller;

import com.notification.service.dto.RequestDTO;
import com.notification.service.dto.ResponseDTO;
import com.notification.service.services.NotificationServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.notification.service.config.constans.Constants.ALL_DESCRIPTION;
import static com.notification.service.config.constans.Constants.STATUS_SUCCESS;

@RestController
@Slf4j
@RequestMapping(value = "/api/v1/notification", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class NotificationController {

  private final NotificationServiceImpl notificationService;

  @PostMapping("/email")
  public ResponseEntity<ResponseDTO> notification(@RequestBody RequestDTO requestDTO) {

    notificationService.execute(requestDTO);

    return ResponseEntity.ok(
            ResponseDTO.builder().status(STATUS_SUCCESS).to(ALL_DESCRIPTION).build()
    );
  }

}
