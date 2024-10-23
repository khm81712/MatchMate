package com.example.backend.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "project_ranking_backup")
public class ProjectRankingBackup {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "project_ranking_backup_id")
    private Long projectRankingBackupId;

    private String rankingCsv;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

}
