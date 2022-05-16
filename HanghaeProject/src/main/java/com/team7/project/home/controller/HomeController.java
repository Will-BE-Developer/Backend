package com.team7.project.home.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.team7.project.home.dto.HomeResponseDto;
import com.team7.project.home.service.HomeService;
import com.team7.project.user.model.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletResponse;

@Slf4j
@RequiredArgsConstructor
@Controller
public class HomeController {

    private final HomeService homeService;
    @GetMapping("/api/home")
    public ResponseEntity<HomeResponseDto> home(@AuthenticationPrincipal User users) {
        return new ResponseEntity<HomeResponseDto>(new HomeResponseDto(homeService.getLatestInterview(users),homeService.getWeeklyInterview(users),
                homeService.getTodayQuestion(),homeService.getTopCatetories()), HttpStatus.OK);
    }
}
