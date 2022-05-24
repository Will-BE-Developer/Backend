package com.sparta.willbe.user.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.sparta.willbe.advice.ErrorMessage;
import com.sparta.willbe.advice.RestException;
import com.sparta.willbe.advice.Success;
import com.sparta.willbe.interview.service.InterviewGeneralService;
import com.sparta.willbe.mail.Service.MailService;
import com.sparta.willbe.security.jwt.TokenResponseDto;
import com.sparta.willbe.user.dto.UserInfoResponseDto;
import com.sparta.willbe.user.dto.request.LoginRequestDto;
import com.sparta.willbe.user.dto.request.RegisterRequestDto;
import com.sparta.willbe.user.model.User;
import com.sparta.willbe.user.service.registerService.KakaoUserService;
import com.sparta.willbe.user.service.registerService.UserProfileService;
import com.sparta.willbe.user.service.registerService.UserRegistryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import static com.sparta.willbe.advice.ErrorMessage.*;

@Slf4j
@RequiredArgsConstructor
@Controller
public class UserController {

    private final UserRegistryService userRegistryService;
    private final UserProfileService userProfileService;
    private final KakaoUserService kakaoUserService;
    private final MailService mailService;
    private final InterviewGeneralService interviewGeneralService;

    @PostMapping("/signin")
    public ResponseEntity<UserInfoResponseDto> Signin(@RequestBody LoginRequestDto requestDto,
                                                      @AuthenticationPrincipal User users,
                                                      HttpServletResponse response, Errors errors) {
        //이미 로그인되어 있다면 사용자는 로그인 할 수 없다.
        if(users !=null){
            throw ErrorMessage.USER_AlREADY_FOUND.throwError();
        }

        //requestbody 에서 들어온 에러를 처리한다
        if (errors.hasErrors()) {
            for (FieldError error : errors.getFieldErrors()) {
                throw new RestException(HttpStatus.BAD_REQUEST, error.getDefaultMessage());
            }
        }
        log.info("SIGN_IN() >> 로그인 되지 않은 사용자 입니다. 로그인을 시행하겠습니다");
        //이미 로그인 되어있지 않는다면 로그인을 시행한다.

        User loginUser = userProfileService.login(requestDto);
        log.info("SIGN_IN() >> 로그인이 처리되었으므로 토큰을 보내겠습니다.");

        //로그인이 오류없이 처리 되었다면 Autorization 토큰을 헤더에 실어 보내준다.
        TokenResponseDto token = userProfileService.giveToken(loginUser.getEmail());
        response.setHeader("Authorization", token.getAuthorization());

        return new ResponseEntity<UserInfoResponseDto>(UserInfoResponseDto.builder()
                .user(UserInfoResponseDto.UserBody.builder()
                        .nickname(loginUser.getNickname())
                        .githubLink(loginUser.getGithubLink())
                        .introduce(loginUser.getIntroduce())
                        .id(loginUser.getId())
                        .profileImageUrl(interviewGeneralService.generateProfileImageUrl(loginUser.getProfileImageUrl()))
                        .build())
                .token(loginUser.getToken())
                .build(), HttpStatus.OK);
    }

    @PostMapping("/signup")
    public ResponseEntity<UserInfoResponseDto> userSignup(@AuthenticationPrincipal User users, @Valid @RequestBody RegisterRequestDto requestDto, Errors errors) {
        //이미 사용자가 로그인 되어있을 경우 회원가입을 할 수 없다.
        if(users !=null){
            throw ErrorMessage.USER_AlREADY_FOUND.throwError();
        }
        log.info("SIGN_UP() >> 로그인 되지 않은 사용자 입니다. 회원가입을 시행하겠습니다");
        //Request body에서 에러가 나면 에러를 보내준다
        if (errors.hasErrors()) {
            for (FieldError error : errors.getFieldErrors()) {
                throw new RestException(HttpStatus.BAD_REQUEST, error.getDefaultMessage());
            }
        }
        log.info("SIGN_UP() >> 이메일 중복검사 시행 중...");
        //중복된 이메일이 존재 한다
        if(userRegistryService.isEmailExist(requestDto.getEmail())){
            throw ErrorMessage.CONFLICT_USER_EMAIL.throwError();
        }
        log.info("SIGN_UP() >> 비밀번호와 비밀번호가 일치하는지 확인중...");
        //비밀번호와 비밀번호 확인이 일치
        if (!requestDto.getPassword().equals( requestDto.getPasswordCheck())) {
            throw ErrorMessage.PASSWORD_MISMATCHED.throwError();
        } else {
            log.info("SIGN_UP() >> 회원가입 진행중.. ");
            //모든 조건이 충족될경우에 회원가입을 진행한다.
            User register = userRegistryService.registerUser(requestDto);
            log.info("SIGN_UP() >> 회원가입 완료!");
            mailService.sendEmail(register.getEmail(),register.getToken(),register.getNickname());
            return new ResponseEntity<UserInfoResponseDto>(UserInfoResponseDto.builder()
                    .user(UserInfoResponseDto.UserBody.builder()
                                    .nickname(register.getNickname())
                                    .githubLink(register.getGithubLink())
                                    .introduce(register.getIntroduce())
                                    .id(register.getId())
                                    .profileImageUrl(interviewGeneralService.generateProfileImageUrl(register.getProfileImageUrl()))
                                    .build())
                    .token(register.getToken())
                    .build(), HttpStatus.OK);
        }
    }
    @GetMapping("/signup/{email}")
    public ResponseEntity<Success> idCheck(@PathVariable String email){
        //유저네임이 등록되어있지 않은경우 사용가능 하다
        if(!userRegistryService.isEmailExist(email)){
            return new ResponseEntity<Success>(new Success(true, "사용 가능한 이메일 입니다."), HttpStatus.OK);
        }
        //유저네임이 등록되어있는경우 사용불가능 하다.
        return new ResponseEntity<Success>(new Success(false, "동일한 이메일 주소가 존재합니다."), HttpStatus.CONFLICT);
    }

    @PostMapping("/signout")
    public ResponseEntity<UserInfoResponseDto> logout(HttpServletRequest request ,@AuthenticationPrincipal User user) {
        String nickname = user.getNickname();
        String gitHubLink = user.getGithubLink();
        String introduce = user.getIntroduce();
        Long id = user.getId();
        String profileImg = user.getProfileImageUrl();
        String token = user.getToken();
        String provider = user.getProvider();

        //로그아웃시에 Conttext holder에 있는 사용자 정보 컨텐츠 값을 지줘준다.
        userProfileService.logout(request);

        if(provider =="kakao"){
            log.info("CONTORLLER >> LOGOUT >> 카카오 로그아웃시작 ");
            kakaoUserService.kakaoLogout(token);
        }

        return new ResponseEntity<UserInfoResponseDto>(UserInfoResponseDto.builder()
                .user(UserInfoResponseDto.UserBody.builder()
                        .nickname(nickname)
                        .githubLink(gitHubLink)
                        .introduce(introduce)
                        .id(id)
                        .profileImageUrl(profileImg)
                        .build())
                .token(token)
                .build(), HttpStatus.OK);
    }


    @GetMapping("api/users/me")
    public ResponseEntity<UserInfoResponseDto> getUserInfo(@AuthenticationPrincipal User user){
        //유저가 로그인 되어있지 않는 경우에는 유저정보를 반환하지 않는다
        log.info("GET_USER_INFO >> 유저 정보를 조회하는 중입니다.");
        if(user ==null){
            throw UNAUTHORIZED_USER.throwError();
        }
        log.info("GET_USER_INFO >> {}의 유저 정보를 반환 합니다 ",user.getNickname());
        //로그인 된 사용자의 이름과 닉네임을 반환한다.

        return new ResponseEntity<UserInfoResponseDto>(UserInfoResponseDto.builder()
                .user(UserInfoResponseDto.UserBody.builder()
                        .nickname(user.getNickname())
                        .githubLink(user.getGithubLink())
                        .introduce(user.getIntroduce())
                        .id(user.getId())
                        .profileImageUrl(interviewGeneralService.generateProfileImageUrl(user.getProfileImageUrl()))
                        .build())
                .token(user.getToken())
                .build(), HttpStatus.OK);
    }

    @DeleteMapping("api/users/me")
    public ResponseEntity<Success> deleteUser(@AuthenticationPrincipal User user){
        if(user ==null){
            throw UNAUTHORIZED_USER.throwError();
        }
        log.info("DELETE_USER >> {} 의 유저정보 삭제를 요청합니다. ", user.getNickname());
        User deleting = userProfileService.deleteUser(user);
        log.info("DELETE_USER >> {} 의 유저정보 삭제 처리를 완료 했습니다. 현제 isDeleted: {} ", deleting.getNickname(),deleting.getIsDeleted());
        return new ResponseEntity<>(new Success(true,"회원삭제 성공"),HttpStatus.OK);
    }

    @GetMapping("/user/kakao/callback")
    public ResponseEntity<UserInfoResponseDto> kakaoLogin(@AuthenticationPrincipal User users, @RequestParam String code, HttpServletResponse response) throws JsonProcessingException {
        //로그인 되는게 확인 될 경우에 에러를 반환한다.
        if(users != null){
            throw ErrorMessage.USER_AlREADY_FOUND.throwError();
        }
        User user =  kakaoUserService.kakaoLogin(code);

        //로그인이 오류없이 처리 되었다면 Autorization 토큰을 헤더에 실어 보내준다.
        TokenResponseDto token = userProfileService.giveToken(user.getEmail());
        response.setHeader("Authorization", token.getAuthorization());

        return new ResponseEntity<>(UserInfoResponseDto.builder()
                .user(UserInfoResponseDto.UserBody.builder()
                        .nickname(user.getNickname())
                        .githubLink(user.getGithubLink())
                        .introduce(user.getIntroduce())
                        .id(user.getId())
                        .profileImageUrl(interviewGeneralService.generateProfileImageUrl(user.getProfileImageUrl()))
                        .build())
                .token(user.getToken())
                .build(), HttpStatus.OK);
    }

    @GetMapping("/signin/validation")
    public ResponseEntity<UserInfoResponseDto> emailValidationandLogin(@RequestParam String token,
                                                                       @RequestParam String email,
                                                                       HttpServletRequest request,
                                                                       @AuthenticationPrincipal User users,
                                                                       HttpServletResponse response){
        //logout 되어있지 않다면 일단 로그아웃 시킨다.
        if(users !=null){
            log.info("USER CONTROLLER >> logging out current user");
            userProfileService.logout(request);
        }
        //isvalid 값을 바꿔준다.

        User user = userProfileService.validateUser(email,token);
        log.info("USER CONTROLLER >>change validation : from {} : ", user.getIsValid());
        //성공했으면 로그인 시켜준다.

        log.info("USER CONTROLLER >> 로그인 시켜주는 중 >>  토큰 발급 중 ... ");
        TokenResponseDto accessToken = userProfileService.giveToken(email);
        response.setHeader("Authorization", accessToken.getAuthorization());

        return new ResponseEntity<>(UserInfoResponseDto.builder()
                .user(UserInfoResponseDto.UserBody.builder()
                        .nickname(user.getNickname())
                        .githubLink(user.getGithubLink())
                        .introduce(user.getIntroduce())
                        .id(user.getId())
                        .profileImageUrl(interviewGeneralService.generateProfileImageUrl(user.getProfileImageUrl()))
                        .build())
                .token(user.getToken())
                .build(), HttpStatus.OK);
    }
  

}
