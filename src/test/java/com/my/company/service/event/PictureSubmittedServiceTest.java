package com.my.company.service.event;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.my.company.endpoint.event.model.PictureSubmitted;
import com.my.company.file.bucket.BucketComponent;
import com.my.company.mail.Email;
import com.my.company.mail.Mailer;
import com.my.company.model.PictureSubmission;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URL;
import java.time.Duration;
import java.util.UUID;
import javax.imageio.ImageIO;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

class PictureSubmittedServiceTest {

  private final BucketComponent bucketComponent = mock(BucketComponent.class);
  private final Mailer mailer = mock(Mailer.class);
  private final PictureSubmittedService service =
      new PictureSubmittedService(bucketComponent, mailer);

  @Test
  @SneakyThrows
  void accept_should_convert_picture_to_black_and_white_and_send_email() {
    var submission =
        PictureSubmission.builder()
            .id(UUID.randomUUID())
            .fileName("cat.jpg")
            .email("jdoe@example.com")
            .build();

    var colorImage = createTinyColorImageFile();
    var expectedUrl =
        new URL("https://bucket.s3.amazonaws.com/black-and-white/" + submission.id() + ".png");

    when(bucketComponent.download("originals/" + submission.id() + ".jpg")).thenReturn(colorImage);
    when(bucketComponent.presign(
            eq("black-and-white/" + submission.id() + ".png"), any(Duration.class)))
        .thenReturn(expectedUrl);

    service.accept(new PictureSubmitted(submission, "originals/" + submission.id() + ".jpg"));

    var fileCaptor = ArgumentCaptor.forClass(File.class);
    verify(bucketComponent)
        .upload(fileCaptor.capture(), eq("black-and-white/" + submission.id() + ".png"));

    var uploadedFile = fileCaptor.getValue();
    var uploadedImage = ImageIO.read(uploadedFile);
    assertThat(uploadedImage.getType()).isEqualTo(BufferedImage.TYPE_BYTE_GRAY);

    verify(mailer).accept(any(Email.class));
  }

  @SneakyThrows
  private File createTinyColorImageFile() {
    var image = new BufferedImage(10, 10, BufferedImage.TYPE_INT_RGB);
    image.setRGB(5, 5, 0xFF0000); // un pixel rouge, pour être sûr que ce n'est pas déjà gris
    var file = File.createTempFile("original-test", ".jpg");
    ImageIO.write(image, "jpg", file);
    return file;
  }
}
