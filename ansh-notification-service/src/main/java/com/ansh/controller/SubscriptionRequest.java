package com.ansh.controller;

import lombok.Data;

@Data
public class SubscriptionRequest {
  private String email;
  private String approver;
}
