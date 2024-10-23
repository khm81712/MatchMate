package com.example.backend.repository.project;

import com.example.backend.dto.request.project.ProjectSearchDto;
import com.example.backend.dto.request.project.RecruitRequestDto;
import com.example.backend.dto.response.project.ProjectDetailResponseDto;
import com.example.backend.dto.response.project.ProjectResponseDto;
import com.example.backend.dto.response.project.QProjectResponseDto;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.example.backend.domain.QProject.project;
import static com.example.backend.domain.QRecruit.recruit;
import static com.querydsl.core.group.GroupBy.groupBy;
import static com.querydsl.core.group.GroupBy.list;

@RequiredArgsConstructor
public class ProjectRepositoryImpl implements ProjectRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    // 프로젝트 목록 가져오기
    @Override
    public Page<ProjectResponseDto> findProjects(Pageable pageable, ProjectSearchDto searchDto) {

        OrderSpecifier<?> orderCondition = searchDto.getSort() == null || searchDto.getSort().equalsIgnoreCase("POPULAR")
                ? project.favoriteCount.add(project.viewCount).desc()
                : project.createdAt.desc();

        List<ProjectResponseDto> content = queryFactory
                .select(new QProjectResponseDto(
                        project.projectId,
                        project.user.nickname,
                        project.user.userFileUrl,
                        project.title,
                        project.techStack,
                        project.position,
                        project.deadline,
                        project.viewCount,
                        project.favoriteCount,
                        project.createdAt))
                .from(project)
                .where(eqTechStack(searchDto.getTechStack()),
                        eqPosition(searchDto.getPosition()),
                        eqKeyword(searchDto.getKeyword()))
                .orderBy(orderCondition)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long total = queryFactory
                .select(project.count())
                .from(project)
                .where(eqTechStack(searchDto.getTechStack()),
                        eqPosition(searchDto.getPosition()),
                        eqKeyword(searchDto.getKeyword()))
                .fetchOne();

        return new PageImpl<>(content, pageable, total);

    }

    // 핫 프로젝트 목록 가져오기
    @Override
    public List<ProjectResponseDto> findHotProjects(int size) {

        return queryFactory
                .select(new QProjectResponseDto(
                        project.projectId,
                        project.user.nickname,
                        project.user.userFileUrl,
                        project.title,
                        project.techStack,
                        project.position,
                        project.deadline,
                        project.viewCount,
                        project.favoriteCount,
                        project.createdAt))
                .from(project)
                .orderBy(project.favoriteCount.add(project.viewCount).desc())//TODO 인기 순위 조건 생각하기
                .limit(size)
                .fetch();

    }

    @Override
    public List<ProjectResponseDto> findResponseDtoAllById(List<Long> projectIds) {

        List<ProjectResponseDto> result = queryFactory
                .select(new QProjectResponseDto(
                        project.projectId,
                        project.user.nickname,
                        project.user.userFileUrl,
                        project.title,
                        project.techStack,
                        project.position,
                        project.deadline,
                        project.createdAt))
                .from(project)
                .where(project.projectId.in(projectIds))
                .fetch();

        Map<Long, ProjectResponseDto> resultMap = result.stream()
                .collect(Collectors.toMap(ProjectResponseDto::getProjectId, dto -> dto));

        return projectIds.stream()
                .map(resultMap::get)
                .collect(Collectors.toList());

    }

    // 내가 찜한 프로젝트 목록 가져오기
    @Override
    public Page<ProjectResponseDto> findFavoriteProjects(Long userId, Pageable pageable) {

        List<ProjectResponseDto> content = queryFactory
                .select(new QProjectResponseDto(
                        project.projectId,
                        project.user.nickname,
                        project.user.userFileUrl,
                        project.title,
                        project.techStack,
                        project.position,
                        project.deadline,
                        project.viewCount,
                        project.favoriteCount,
                        project.createdAt))
                .from(project)
                .where(project.projectLike.contains(userId))
                .orderBy(project.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long total = queryFactory
                .select(project.count())
                .from(project)
                .where(project.projectLike.contains(userId))
                .fetchOne();

        return new PageImpl<>(content, pageable, total);

    }

    // 프로젝트 상세 정보 가져오기
    @Override
    public List<ProjectDetailResponseDto> findDetailByProjectId(Long projectId) {

        return queryFactory
                .selectFrom(project)
                .leftJoin(recruit).on(project.projectId.eq(recruit.project.projectId))
                .where(project.projectId.eq(projectId))
                .transform(groupBy(project.projectId).list(Projections.constructor(ProjectDetailResponseDto.class,
                        project.projectId,
                        project.user.userId,
                        project.user.nickname,
                        project.user.userFileUrl,
                        project.projectFileUrl,
                        project.title,
                        project.techStack,
                        project.softSkill,
                        project.importantQuestion,
                        project.deadline,
                        project.recruitment,
                        project.user.employmentStatus,
                        project.viewCount,
                        project.favoriteCount,
                        project.description,
                        project.createdAt,
                        project.lastModifiedAt,
                        list(Projections.constructor(RecruitRequestDto.class,
                                recruit.position,
                                recruit.currentCount,
                                recruit.targetCount)))));

    }

    // 내가 작성한 프로젝트 가져오기
    @Override
    public Page<ProjectResponseDto> findMyProjects(Long userId, Pageable pageable) {

        List<ProjectResponseDto> content = queryFactory
                .select(new QProjectResponseDto(
                        project.projectId,
                        project.user.nickname,
                        project.user.userFileUrl,
                        project.title,
                        project.techStack,
                        project.position,
                        project.deadline,
                        project.viewCount,
                        project.favoriteCount,
                        project.createdAt))
                .from(project)
                .where(project.user.userId.eq(userId))
                .orderBy(project.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long total = queryFactory
                .select(project.count())
                .from(project)
                .where(project.user.userId.eq(userId))
                .fetchOne();

        return new PageImpl<>(content, pageable, total);

    }

    // 기술 스택 동적 쿼리
    private BooleanExpression eqTechStack(String techStackCsv) {
        if (techStackCsv == null) return null;
        String[] split = techStackCsv.split(", ");
        BooleanExpression condition = null;
        for (String stack : split) {
            BooleanExpression stackCondition = project.techStack.contains(stack);
            condition = (condition == null) ? stackCondition : condition.and(stackCondition);
        }
        return condition;
    }

    // 포지션 스택 동적 쿼리
    private BooleanExpression eqPosition(String positionCsv) {
        if (positionCsv == null) return null;
        String[] split = positionCsv.split(", ");
        BooleanExpression condition = null;
        for (String position : split) {
            BooleanExpression positionCondition = project.position.contains(position);
            condition = (condition == null) ? positionCondition : condition.and(positionCondition);
        }
        return condition;
    }

    // 검색어 동적 쿼리
    private BooleanExpression eqKeyword(String keyword) {
        if (keyword == null) return null;
        return project.title.contains(keyword)
                .or(project.description.contains(keyword));
    }

}
