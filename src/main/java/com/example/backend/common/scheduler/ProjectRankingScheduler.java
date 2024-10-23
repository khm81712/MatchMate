package com.example.backend.common.scheduler;

import com.example.backend.common.util.RedisUtil;
import com.example.backend.domain.ProjectRankingBackup;
import com.example.backend.dto.response.project.ProjectResponseDto;
import com.example.backend.repository.project.ProjectRankingRepository;
import com.example.backend.repository.project.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.LocalDateTime;
import java.util.List;

@EnableScheduling
@RequiredArgsConstructor
public class ProjectRankingScheduler {

    private RedisUtil redisUtil;

    private ProjectRepository projectRepository;

    private ProjectRankingRepository projectRankingRepository;

    @Scheduled(cron = "0 0 0 ? * MON", zone = "Asia/Seoul")
    public void projectRankingScheduler() {
        StringBuilder stringBuilder = new StringBuilder();

        // 상위 10개 projectId 값 가져오기
        List<Long> rankingIdList = redisUtil.getProjectRankingIdList();

        // DB에 랭킹 리스트 백업
        for(Long id : rankingIdList) stringBuilder.append(id).append(", ");
        String rankingCsv = stringBuilder.substring(0, stringBuilder.length() - 2);
        projectRankingRepository.save(ProjectRankingBackup.builder()
                .createdAt(LocalDateTime.now())
                .rankingCsv(rankingCsv)
                .build());

        // redis에 저장
        List<ProjectResponseDto> projectRankingList = projectRepository.findResponseDtoAllById(rankingIdList);
        redisUtil.projectCacheSave(projectRankingList);

        // redis에서 zset으로 저장했던 값들 삭제(초기화)
        redisUtil.deleteZSetData("project:ranking");
    }

}
