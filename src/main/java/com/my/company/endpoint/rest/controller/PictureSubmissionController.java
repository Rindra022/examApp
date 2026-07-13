package com.my.company.endpoint.rest.controller;

import com.my.company.model.PictureSubmission;
import com.my.company.service.PictureSubmissionService;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/pictures")
@AllArgsConstructor
public class PictureSubmissionController {
  private final PictureSubmissionService service;

  @PostMapping
  public PictureSubmission submit(
      @RequestParam("file") MultipartFile file, @RequestParam String email) {
    return service.submit(file, email);
  }

  @GetMapping
  public List<PictureSubmission> findAll() {
    return service.findAll();
  }
}
