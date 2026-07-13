package com.my.company.mapper;

import com.my.company.model.PictureSubmission;
import com.my.company.repository.model.JPictureSubmission;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class PictureSubmissionMapper {
  public PictureSubmission toModel(JPictureSubmission entity) {
    return PictureSubmission.builder()
        .id(entity.getId())
        .fileName(entity.getFileName())
        .email(entity.getEmail())
        .createdAt(entity.getCreatedAt())
        .build();
  }

  public JPictureSubmission toEntity(String fileName, String email) {
    return JPictureSubmission.builder()
        .id(UUID.randomUUID())
        .fileName(fileName)
        .email(email)
        .build();
  }
}
