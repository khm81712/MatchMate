package com.example.backend.controller;

import com.example.backend.common.response.CommonApiResponse;
import com.example.backend.common.response.PageApiResponse;
import com.example.backend.domain.User;
import com.example.backend.dto.request.people.HotSearchDto;
import com.example.backend.dto.request.people.PeopleSearchDto;
import com.example.backend.dto.response.people.PeopleDetailResponseDto;
import com.example.backend.dto.response.people.PeopleResponseDto;
import com.example.backend.repository.people.PeopleRepository;
import com.example.backend.repository.project.ProjectRepository;
import com.example.backend.service.JwtService;
import com.example.backend.service.KakaoMessageService;
import com.example.backend.service.PeopleService;
import com.example.backend.service.ProposalService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.HttpStatus.*;

@RestController
@RequiredArgsConstructor
public class PeopleController {

    private final PeopleRepository peopleRepository;
    private final ProjectRepository projectRepository;
    private final KakaoMessageService kakaoMessageService;
    private final ProposalService proposalService;
    private final JwtService jwtService;
    private final PeopleService peopleService;

    @GetMapping("/peoples")
    public PageApiResponse<?> getPeoples(@ModelAttribute PeopleSearchDto dto) {
        //return peopleService.findPeoples(dto);

        return peopleRepository.findPeoples(dto);
    }

    @GetMapping("/peoples/{peopleId}")
    public CommonApiResponse<PeopleDetailResponseDto> getPeopleDetail(@PathVariable("peopleId") Long peopleId) throws Exception {

        User user = peopleRepository.findById(peopleId)
                .orElseThrow(() -> new RuntimeException("해당 피플을 찾을 수 없습니다."));

        PeopleDetailResponseDto dto = new PeopleDetailResponseDto(user);

        return new CommonApiResponse<>(OK, dto);
    }

    @GetMapping("/peoples/hot")
    public CommonApiResponse<List<PeopleResponseDto>> getHotPeoples(@ModelAttribute HotSearchDto dto) {
        return new CommonApiResponse<>(OK, peopleRepository.findHotPeoples(dto));
    }

    @GetMapping("/users/favorite")
    public PageApiResponse<List<PeopleResponseDto>> getFavoritePeoples(Authentication authentication,
                                                                       @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.ASC) Pageable pageable) {

        Long userId = jwtService.getUserIdFromAuthentication(authentication);
        return peopleRepository.findFavoritePeoples(userId, pageable);
    }

    @PostMapping("/proposal/send/{peopleId}")
    public CommonApiResponse<?> sendProposal(@PathVariable("peopleId") Long receiver,
                                             Authentication authentication) throws Exception {

        Long proposer = jwtService.getUserIdFromAuthentication(authentication); //보내는 사람

        proposalService.sendProposal(receiver, proposer);
        return new CommonApiResponse<>(OK, "제안 신청이 성공하였습니다.");
    }

}
