package com.sparta.willbe.scrap.service;


import com.sparta.willbe.advice.ErrorMessage;
import com.sparta.willbe.interview.model.Interview;
import com.sparta.willbe.interview.repository.InterviewRepository;
import com.sparta.willbe.scrap.dto.ScrapInfoResponseDto;
import com.sparta.willbe.scrap.model.Scrap;
import com.sparta.willbe.scrap.repository.ScrapRepository;
import com.sparta.willbe.user.model.User;
import com.sparta.willbe.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
                .orElseThrow(ErrorMessage.NOT_FOUND_LOGIN_USER::throwError);

        for (Scrap scrap : loginUser.getScraps()) {
            if (Objects.equals(scrap.getInterview().getId(), interviewId)) {
                throw ErrorMessage.CONFLICT_SCRAP_POST.throwError();
            }
        }

        Interview interview = interviewRepository.findById(interviewId)
                .orElseThrow(ErrorMessage.NOT_FOUND_INTERVIEW::throwError);

        Long scrapCount = interview.getScraps().stream().count();

        Scrap scrap = scrapRepository.save(new Scrap(loginUser, interview));
        loginUser.getScraps().add(scrap);
        interview.getScraps().add(scrap);

        return new ScrapInfoResponseDto(new ScrapInfoResponseDto.Data(interviewId, true, scrapCount + 1));

    }

    @Transactional
    public ScrapInfoResponseDto removeScrap(User user, Long interviewId) {

        Scrap scrap = scrapRepository.findByUser_IdAndInterview_Id(user.getId(), interviewId)
                .orElseThrow(ErrorMessage.CONFLICT_SCRAP_DELETE::throwError);

        scrapRepository.deleteById(scrap.getId());

        return new ScrapInfoResponseDto(new ScrapInfoResponseDto.Data(interviewId, false, 999L));
    }

    public Long getScrapCount(Long interviewId) {
        Interview interview = interviewRepository.findById(interviewId)
                .orElseThrow(ErrorMessage.NOT_FOUND_INTERVIEW::throwError);

        return interview.getScraps().stream().count();
    }

}
