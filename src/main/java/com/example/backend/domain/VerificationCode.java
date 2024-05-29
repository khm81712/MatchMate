package com.example.backend.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity(name = "code")
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class VerificationCode {

    @Id
    private String email;

    @Column(name = "verification_code")
    private String verificationCode;

}
