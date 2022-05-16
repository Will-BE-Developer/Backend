package com.team7.project.likes.service;

import com.team7.project.advice.RestException;
import com.team7.project.interview.model.Interview;
import com.team7.project.interview.repository.InterviewRepository;
import com.team7.project.likes.dto.LikesResponseDto;
import com.team7.project.likes.model.Intervals;
import com.team7.project.likes.model.Likes;
//import com.team7.project.likes.model.LikesData;
import com.team7.project.likes.repository.LikesRepository;
import com.team7.project.user.dto.UserInfoResponseDto;
import com.team7.project.user.model.User;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class LikesService {

    private final LikesRepository likesRepository;
    private final InterviewRepository interviewRepository;


    public LikesResponseDto addLike(Long videoId, User user, String time){

        Likes likes;
        Interview interview = interviewRepository.findById(videoId).orElseThrow(
                ()-> new RestException(HttpStatus.BAD_REQUEST,"해당 인터뷰를 찾을 수 없습니다.")
        );


        likes = likesRepository.findByInterviewId(videoId);
        Map<Integer,Integer> map = new HashMap<>();


        if(likes ==null) {
            likes = likesRepository.save(Likes.builder()
                    .interview(interview)
                    .likesData(map)
                    .build());
        }else{
            map = likes.getLikesData();
        }

        int timeSec = (Integer.parseInt(time.split(":")[0])*60+Integer.parseInt(time.split(":")[1]))/10;
        log.info("time convet to sec is : {}",timeSec);
        log.info("what is value in {} : {} ",timeSec,map.getOrDefault(timeSec,0));
        map.put(timeSec,map.getOrDefault(timeSec,0)+1);
        likes.setLikesData(map);

        log.info("*************GET TOP THREE**************");
        List<Integer> findTopThree = map.entrySet().stream().sorted(Map.Entry.<Integer,Integer>comparingByValue()
                .reversed()).limit(3).map(Map.Entry::getKey).collect(Collectors.toList());

        if(findTopThree.size() ==2){
            findTopThree.add(-1);
        }else if(findTopThree.size() ==1){
            findTopThree.add(-1);
            findTopThree.add(-1);
        }

        UserInfoResponseDto userInfoResponseDto = UserInfoResponseDto.builder()
                .user(UserInfoResponseDto.UserBody.builder()
                        .introduce(user.getIntroduce())
                        .profileImageUrl(user.getProfileImageUrl())
                        .nickname(user.getNickname())
                        .id(user.getId())
                        .githubLink(user.getGithubLink())
                        .build())
                .build();
        LikesResponseDto likesResponseDto = new LikesResponseDto(likes.getLikesData(), Long.valueOf(findTopThree.get(0))
                , Long.valueOf(findTopThree.get(1)), Long.valueOf(findTopThree.get(2)),userInfoResponseDto);
       return likesResponseDto;
    }

}
