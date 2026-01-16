package com.notification.service.dto;

import lombok.Data;


import java.util.List;


@Data
public class RequestDTO {

  private List<String> to;
  private List<String> cc;
  private String subject;
  private String body;

}
