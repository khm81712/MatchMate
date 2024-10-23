package com.example.backend.service;

import com.amazonaws.services.s3.AmazonS3;
import com.example.backend.common.util.RedisUtil;
import com.example.backend.domain.User;
import com.example.backend.dto.request.people.UpdateUserRequestDto;
import com.example.backend.repository.people.PeopleRepository;
import com.example.backend.repository.people.VerificationCodeRepository;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Random;

@Service
@RequiredArgsConstructor
@Transactional
public class PeopleService {

    private final PeopleRepository peopleRepository;
    private final VerificationCodeRepository codeRepository;
    private final AmazonS3 amazonS3;

    private final AwsS3Service awsS3Service;
    private final JavaMailSender mailSender;

    private final RedisUtil redisUtil;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    public String update(Long userId,
                         @RequestPart UpdateUserRequestDto dto,
                         @RequestPart(required = false) MultipartFile file) throws IOException {
        User user = peopleRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("해당 사용자는 존재하지 않습니다."));

        String fileUrl;
        if (file != null && !file.isEmpty()) {
            // 기존 파일 삭제
            awsS3Service.deleteFileFromS3(user.getUserFileUrl());
            // 새 파일 업로드
            fileUrl = awsS3Service.upload(file);
        } else {
            // 기존 파일 URL 유지
            fileUrl = user.getUserFileUrl();
        }

        user.updateUser(dto.getNickname(),
                dto.getPosition(),
                fileUrl,
                dto.isEmploymentStatus(),
                dto.getTechStack(),
                dto.getSoftSkill(),
                dto.getYear(),
                dto.getLinks(),
                dto.getContent(),
                dto.isAlarmStatus(),
                dto.getEmail());

        peopleRepository.save(user);

        return "수정이 완료되었습니다.";
    }

    public String sendVerificationCode(String email) {

        Random r = new Random();
        StringBuilder verificationCodeBuilder = new StringBuilder();

        for(int i = 0; i < 6; i++) {
            verificationCodeBuilder = verificationCodeBuilder.append(Integer.toString(r.nextInt(10)));
        }

        String verificationCode = verificationCodeBuilder.toString();

        String from = "matchmate25@gmail.com";
        String title = "[매치메이트] 인증 메일이 도착했습니다.";
        String content = """
        메치메이트에 방문해주셔서 감사합니다.
        <br><br>
        인증 번호 : %s
        <br><br>
        인증 번호를 제대로 입력해주세요
        """.formatted(verificationCode);

        redisUtil.setData(email, verificationCode, 5 * 60 * 1000L);

        return mailSend(from, email, title, content);

    }

    private String mailSend(String setFrom, String toMail, String title, String content) {
        MimeMessage message = mailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message,true,"utf-8");
            helper.setFrom(setFrom);
            helper.setTo(toMail);
            helper.setSubject(title);
            helper.setText(content,true);
            mailSender.send(message);
        } catch (Exception e) {
            e.printStackTrace();
            return "fail";
        }
        return "success";
    }

    public Boolean checkVerificationCode(String email, String code) {

        if (redisUtil.getData(email) == null || !redisUtil.getData(email).equals(code)) return false;

        redisUtil.deleteData(email);

        return true;

    }

    public Boolean checkNickname(String nickname) {

        if(peopleRepository.findByNickname(nickname) == null) return true;

        return false;

    }
}
