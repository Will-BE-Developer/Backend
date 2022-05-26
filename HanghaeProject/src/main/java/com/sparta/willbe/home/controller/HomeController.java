package com.sparta.willbe.home.controller;

import com.sparta.willbe.home.dto.HomeResponseDto;
import com.sparta.willbe.home.service.HomeService;
import com.sparta.willbe.security.jwt.JwtTokenProvider;
import com.sparta.willbe.user.model.User;
import com.sparta.willbe.user.repository.UserRepository;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Slf4j
@RequiredArgsConstructor
@Controller
public class HomeController {

    private final HomeService homeService;
    private final JwtTokenProvider jwtTokenProvider;
    @GetMapping("/api/home")
    @ApiOperation(value = "메인페이지용 api")
    @ApiImplicitParam(name = "Authorization", value = "token", dataTypeClass = String.class, paramType = "header", example = "Bearer access_token")
    public ResponseEntity<HomeResponseDto> home(@AuthenticationPrincipal User users) {

        return new ResponseEntity<HomeResponseDto>(new HomeResponseDto(homeService.getLatestInterview(users),homeService.getWeeklyInterview(users),
                homeService.getTodayQuestion(),homeService.getTopCatetories()), HttpStatus.OK);
    }


}
