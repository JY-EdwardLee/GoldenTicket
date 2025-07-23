package com.ssafy.ticket_backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthCheckController {
  @Autowired private JdbcTemplate jdbcTemplate;

  @GetMapping("/health-check")
  public ResponseEntity<String> healthCheck() {
    try {
      jdbcTemplate.queryForObject("SELECT 1", Integer.class);

      return ResponseEntity.ok("Database connection is successful!");
    } catch (Exception e) {
      return ResponseEntity.status(500).body("Database connection failed: " + e.getMessage());
    }
  }
}
