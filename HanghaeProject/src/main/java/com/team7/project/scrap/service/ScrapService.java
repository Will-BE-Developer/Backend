package com.team7.project.scrap.service;


import com.team7.project.advice.RestException;
import com.team7.project.interview.model.Interview;
import com.team7.project.interview.repository.InterviewRepository;
import com.team7.project.scrap.dto.ScrapInfoResponseDto;
import com.team7.project.scrap.model.Scrap;
import com.team7.project.scrap.repository.ScrapRepository;
import com.team7.project.user.model.User;
import com.team7.project.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.hibernate.Hibernate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Objects;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class ScrapService {
    private final InterviewRepository interviewRepository;
    private final UserRepository userRepository;
    private final ScrapRepository scrapRepository;

    @Transactional
    public ScrapInfoResponseDto addScrap(User user, Long interviewId) {

        User loginUser = userRepository.findById(user.getId())
                .orElseThrow(
                        () -> new RestException(HttpStatus.BAD_REQUEST, "해당 유저가 존재하지 않습니다.")
                );

        for (Scrap scrap : loginUser.getScraps()) {
            if (Objects.equals(scrap.getInterview().getId(), interviewId)) {
                throw new RestException(HttpStatus.CONFLICT, "이미 스크랩한 게시글 입니다.");
            }
        }

        Interview interview = interviewRepository.findById(interviewId)
                .orElseThrow(
                        () -> new RestException(HttpStatus.BAD_REQUEST, "해당 인터뷰가 존재하지 않습니다.")
                );
        Long scrapCount = interview.getScraps().stream().count();

        Scrap scrap = scrapRepository.save(new Scrap(loginUser, interview));
        loginUser.getScraps().add(scrap);
        interview.getScraps().add(scrap);

        return new ScrapInfoResponseDto(new ScrapInfoResponseDto.Data(interviewId, true, scrapCount + 1));

    }

    @Transactional
    public ScrapInfoResponseDto removeScrap(User user, Long interviewId) {

        Scrap scrap = scrapRepository.findByUser_IdAndInterview_Id(user.getId(), interviewId)
                .orElseThrow(
                        () -> new RestException(HttpStatus.BAD_REQUEST, "해당 스크랩 정보가 존재하지 않습니다.")
                );

        scrapRepository.deleteById(scrap.getId());

        return new ScrapInfoResponseDto(new ScrapInfoResponseDto.Data(interviewId, false, 999L));
    }

    public Long getScrapCount(Long interviewId){
        Interview interview = interviewRepository.findById(interviewId)
                .orElseThrow(
                        () -> new RestException(HttpStatus.BAD_REQUEST, "해당 인터뷰가 존재하지 않습니다.")
                );
        return interview.getScraps().stream().count();
    }

}
