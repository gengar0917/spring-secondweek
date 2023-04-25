package com.sparta.hanghaememo.controller;

import com.sparta.hanghaememo.dto.UserRequestDto;
import com.sparta.hanghaememo.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import com.sparta.hanghaememo.dto.*;

@Controller //Json 형태로 반환할 게 아니어서 @RestController는 필요 없음
@RequiredArgsConstructor // UserService를 초기화 할 때 사용됨
@RequestMapping("/api/user") //공통적인 url 표시
public class UserController {

    private final UserService userService; //UserService 포함관계로 가져오기

    //회원가입
    @PostMapping("/signup") //Post 메서드로 url 추가로 적음
    public StatusResponseDto signup(@Valid @RequestBody UserRequestDto userRequestDto) { //@Valid로 @RequestBody를 통해 받는 매개변수를 제한함
        return userService.signup(userRequestDto); //Service 계층으로 넘어감
    }

    //로그인
    @PostMapping("/login")
    public StatusResponseDto login(@RequestBody UserRequestDto userRequestDto, HttpServletResponse response) { //@RequestBody를 이용해 Json 형태로 매개변수를 받음
        return userService.login(userRequestDto, response); //Service 계층으로 넘어감
    }
}