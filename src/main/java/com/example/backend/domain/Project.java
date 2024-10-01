package com.example.backend.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static jakarta.persistence.CascadeType.*;
import static jakarta.persistence.FetchType.*;

@Entity
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "project")
public class Project {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "project_id")
    private Long projectId;

    private String title;

    @Column(name = "file_url")
    private String projectFileUrl;

    private LocalDate deadline;

    private String recruitment;

    private String position;

    @Column(name = "tech_stack")
    private String techStack;

    @Column(name = "soft_skill")
    private String softSkill;

    @Column(name = "important_question")
    private String importantQuestion;

    @Column(name = "view_count")
    private int viewCount;

    @Column(name = "favorite_count")
    private int favoriteCount;

    @Lob
    private String description;

    @Column(name = "last_modified_at")
    private LocalDateTime lastModifiedAt;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Builder.Default
    @ElementCollection
    @CollectionTable(name = "project_favorite", joinColumns = @JoinColumn(name = "project_id"))
    @Column(name = "user_id")
    private Set<Long> projectLike = new HashSet<>();

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "created_id")
    private User user;

    @Builder.Default
    @OneToMany(mappedBy = "project", cascade = ALL, orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "project", cascade = ALL, orphanRemoval = true)
    private List<Recruit> recruits = new ArrayList<>();

    public void update(String title, String projectFileUrl, LocalDate deadline,
                       String importantQuestion, String softSkill, String techStack,
                       String description, List<Recruit> recruits, String position,
                       LocalDateTime lastModifiedAt) {
        this.title = title;
        this.projectFileUrl = projectFileUrl;
        this.deadline = deadline;
        this.importantQuestion = importantQuestion;
        this.softSkill = softSkill;
        this.techStack = techStack;
        this.description = description;
        this.recruits = recruits;
        this.position = position;
        this.lastModifiedAt = lastModifiedAt;
    }

    public void updateRecruit(List<Recruit> recruits) {this.recruits = recruits;}

    public void updateFavoriteCount(int count) {this.favoriteCount += count;}

    public void updatePosition(String position) {this.position = position;}

    public void addProjectLike(Long userId) {this.projectLike.add(userId);}

}
