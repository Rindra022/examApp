package com.my.company.repository;

import com.my.company.repository.model.JPictureSubmission;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PictureSubmissionRepository extends JpaRepository<JPictureSubmission, UUID> {}
