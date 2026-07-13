package com.my.company.service.event;

import com.my.company.endpoint.event.model.PictureSubmitted;
import com.my.company.file.bucket.BucketComponent;
import com.my.company.mail.Email;
import com.my.company.mail.Mailer;
import jakarta.mail.internet.InternetAddress;
import java.awt.image.BufferedImage;
import java.io.File;
import java.time.Duration;
import java.util.List;
import java.util.function.Consumer;
import javax.imageio.ImageIO;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class PictureSubmittedService implements Consumer<PictureSubmitted> {
  private final BucketComponent bucketComponent;
  private final Mailer mailer;

  @Override
  @SneakyThrows
  public void accept(PictureSubmitted event) {
    var submission = event.getSubmission();

    var originalFile = bucketComponent.download(event.getOriginalBucketKey());

    var bwFile = convertToBlackAndWhite(originalFile, submission.id());

    var bwKey = "black-and-white/" + submission.id() + ".png";
    bucketComponent.upload(bwFile, bwKey);

    var link = bucketComponent.presign(bwKey, Duration.ofHours(24)).toString();
    var email =
        new Email(
            new InternetAddress(submission.email()),
            List.of(),
            List.of(),
            "Your black and white picture is ready",
            "<p>Here is your picture in black and white: <a href=\"%s\">%s</a></p>"
                .formatted(link, link),
            List.of());
    mailer.accept(email);
  }

  private File convertToBlackAndWhite(File original, java.util.UUID id) throws Exception {
    var image = ImageIO.read(original);
    var bwImage =
        new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
    var graphics = bwImage.getGraphics();
    graphics.drawImage(image, 0, 0, null);
    graphics.dispose();

    var outputFile = File.createTempFile("bw-" + id, ".png");
    ImageIO.write(bwImage, "png", outputFile);
    return outputFile;
  }
}
