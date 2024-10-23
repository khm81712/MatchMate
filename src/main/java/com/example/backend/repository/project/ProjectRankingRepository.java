package com.example.backend.repository.project;

import com.example.backend.domain.ProjectRankingBackup;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;

public interface ProjectRankingRepository extends JpaRepository<ProjectRankingBackup, LocalDate> {

    ProjectRankingBackup findTopByOrderByProjectRankingBackupIdDesc();

}
