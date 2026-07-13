package com.my.company.repository.model;

import jakarta.persistence.*;
import java.util.UUID;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Entity
@Table(name = "picture_submission")
public class JPictureSubmission {
  @Id @GeneratedValue private UUID id;

  @Column(nullable = false)
  private String fileName;

  @Column(nullable = false)
  private String email;
}
