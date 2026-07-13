package com.my.company.service;

import static java.util.List.of;

import com.my.company.endpoint.event.EventProducer;
import com.my.company.endpoint.event.model.PictureSubmitted;
import com.my.company.file.bucket.BucketComponent;
import com.my.company.mapper.PictureSubmissionMapper;
import com.my.company.model.PictureSubmission;
import com.my.company.repository.PictureSubmissionRepository;
import java.io.File;
import java.util.List;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@AllArgsConstructor
public class PictureSubmissionService {
  private static final Set<String> ALLOWED_CONTENT_TYPES = Set.of("image/jpeg", "image/png");

  private final PictureSubmissionRepository repository;
  private final PictureSubmissionMapper mapper;
  private final BucketComponent bucketComponent;
  private final EventProducer<PictureSubmitted> eventProducer;

  @SneakyThrows
  public PictureSubmission submit(MultipartFile file, String email) {
    if (!ALLOWED_CONTENT_TYPES.contains(file.getContentType())) {
      throw new IllegalArgumentException("Only JPEG and PNG images are accepted");
    }

    var entity = mapper.toEntity(file.getOriginalFilename(), email);
    var saved = mapper.toModel(repository.save(entity));

    var tempFile = File.createTempFile("original-" + saved.id(), getExtension(file));
    file.transferTo(tempFile);
    var originalKey = "originals/" + saved.id() + getExtension(file);
    bucketComponent.upload(tempFile, originalKey);

    eventProducer.accept(of(new PictureSubmitted(saved, originalKey)));

    return saved;
  }

  public List<PictureSubmission> findAll() {
    return repository.findAll().stream().map(mapper::toModel).toList();
  }

  private String getExtension(MultipartFile file) {
    return "image/png".equals(file.getContentType()) ? ".png" : ".jpg";
  }
}
