package com.my.company.repository.model;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Entity
@Table(name = "picture_submission")
public class JPictureSubmission {
  @Id @GeneratedValue private UUID id;

  @CreationTimestamp private Instant createdAt;

  @Column(nullable = false)
  private String fileName;

  @Column(nullable = false)
  private String email;
}
