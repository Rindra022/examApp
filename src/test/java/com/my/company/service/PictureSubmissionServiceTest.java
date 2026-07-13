package com.my.company.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.my.company.endpoint.event.EventProducer;
import com.my.company.endpoint.event.model.PictureSubmitted;
import com.my.company.file.bucket.BucketComponent;
import com.my.company.mapper.PictureSubmissionMapper;
import com.my.company.model.PictureSubmission;
import com.my.company.repository.PictureSubmissionRepository;
import com.my.company.repository.model.JPictureSubmission;
import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

class PictureSubmissionServiceTest {

  private final PictureSubmissionRepository repository = mock(PictureSubmissionRepository.class);
  private final PictureSubmissionMapper mapper = mock(PictureSubmissionMapper.class);
  private final BucketComponent bucketComponent = mock(BucketComponent.class);

  @SuppressWarnings("unchecked")
  private final EventProducer<PictureSubmitted> eventProducer = mock(EventProducer.class);

  private final PictureSubmissionService service =
      new PictureSubmissionService(repository, mapper, bucketComponent, eventProducer);

  @Test
  void submit_should_persist_metadata_and_produce_event_for_a_valid_jpeg() {
    var file =
        new MockMultipartFile("file", "cat.jpg", "image/jpeg", "fake-image-bytes".getBytes());
    var jEntity = new JPictureSubmission();
    var model =
        PictureSubmission.builder()
            .id(UUID.randomUUID())
            .fileName("cat.jpg")
            .email("jdoe@example.com")
            .createdAt(Instant.now())
            .build();

    when(mapper.toEntity("cat.jpg", "jdoe@example.com")).thenReturn(jEntity);
    when(repository.save(jEntity)).thenReturn(jEntity);
    when(mapper.toModel(jEntity)).thenReturn(model);

    var result = service.submit(file, "jdoe@example.com");

    assertThat(result).isEqualTo(model);
    verify(repository).save(jEntity);
    verify(bucketComponent).upload(any(), eq("originals/" + model.id() + ".jpg"));
    verify(eventProducer).accept(anyList());
  }

  @Test
  void submit_should_reject_unsupported_format() {
    var file = new MockMultipartFile("file", "cat.gif", "image/gif", "fake-bytes".getBytes());

    assertThatThrownBy(() -> service.submit(file, "jdoe@example.com"))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Only JPEG and PNG images are accepted");

    verifyNoInteractions(repository, bucketComponent, eventProducer);
  }

  @Test
  void findAll_should_return_all_submissions_mapped_to_domain_models() {
    var jEntity = new JPictureSubmission();
    var model = PictureSubmission.builder().id(UUID.randomUUID()).fileName("cat.jpg").build();

    when(repository.findAll()).thenReturn(java.util.List.of(jEntity));
    when(mapper.toModel(jEntity)).thenReturn(model);

    var result = service.findAll();

    assertThat(result).containsExactly(model);
  }
}
