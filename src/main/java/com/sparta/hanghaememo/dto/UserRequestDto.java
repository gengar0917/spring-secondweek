package com.sparta.hanghaememo.dto;

import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserRequestDto {

    //유저네임에 제한 걸기
    @Pattern(regexp = "^[0-9a-z]{4,10}$", message = "4 ~ 10자 사이의 알파벳 소문자와 숫자만 가능합니다.") //@Pattern과 정규식을 이용해 유저네임에 제약 걸기
    private String username;

    //패스워드에 제한 걸기
    @Pattern(regexp = "^[0-9a-zA-Z]{8,15}$", message = "8 ~ 15자 사이의 알파벳 대소문자와 숫자만 가능합니다.") //@Pattern과 정규식을 이용해 패스워드에 제약 걸기
    private String password;

}
