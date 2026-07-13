package com.my.company.model;

import java.util.UUID;
import lombok.Builder;

@Builder
public record PictureSubmission(UUID id, String fileName, String email) {}
