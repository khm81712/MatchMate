package com.example.backend.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@NoArgsConstructor
@Getter
@AllArgsConstructor
@Builder
@Data
@Table(name = "user")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;

    @Column(name = "social_id")
    private String socialId;

    @Column(name = "social_type")
    private String socialType;

    private String nickname;

    private String email;
    private String position;

    @Column(name = "file_url")
    private String userFileUrl;

    @Column(name = "employment_status")
    private boolean employmentStatus;

    @Column(name = "tech_stack")
    private String techStack;

    @Column(name = "soft_skill")
    private String softSkill;

    private String year;

    private String links;

    @Column(name = "view_count")
    private int viewCount;

    @Column(name = "favorite_count")
    private int favoriteCount;

    @Lob
    private String content;

    @Column(name = "alarm_status")
    private boolean alarmStatus;

    @Column(name = "last_modified_at")
    private LocalDateTime lastModifiedAt;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Builder.Default
    @ElementCollection
    @CollectionTable(name = "user_favorite", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "favorite_id")
    private Set<Long> userLike = new HashSet<>();

    @Builder.Default
    @OneToMany(mappedBy = "user")
    private List<Project> projects = new ArrayList<>();

    public User(String nickname, String position, String userFileUrl, String techStack,
                int viewCount, int favoriteCount, LocalDateTime createdAt) {
        this.nickname = nickname;
        this.position = position;
        this.userFileUrl = userFileUrl;
        this.techStack = techStack;
        this.viewCount = viewCount;
        this.favoriteCount = favoriteCount;
        this.createdAt = createdAt;
    }

    public void updateFavoriteCount(int count) {
        this.favoriteCount += count;
    }

    public void addUserLike(Long userId) {
        this.userLike.add(userId);
    }

    public void updateUser(String nickname, String position, String userFileUrl, boolean employmentStatus, String techStack,
                String softSkill, String year, String links, String content, boolean alarmStatus, String email) {
        this.nickname = nickname;
        this.position = position;
        this.userFileUrl = userFileUrl;
        this.employmentStatus = employmentStatus;
        this.techStack = techStack;
        this.softSkill = softSkill;
        this.year = year;
        this.links = links;
        this.content = content;
        this.alarmStatus = alarmStatus;
        this.email = email;
    }
}
