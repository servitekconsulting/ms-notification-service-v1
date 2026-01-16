package com.notification.service.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ResponseDTO {

  private String to;
  private String status;
}
