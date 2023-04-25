package com.sparta.hanghaememo.service;

import com.sparta.hanghaememo.dto.StatusResponseDto;
import com.sparta.hanghaememo.dto.UserRequestDto;
import com.sparta.hanghaememo.entity.User;
import com.sparta.hanghaememo.jwt.JwtUtil;
import com.sparta.hanghaememo.repository.UserRepository;
import jakarta.servlet.http.HttpServletResponse;
import jdk.jshell.Snippet;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service //서비스 계층
@RequiredArgsConstructor //포함관계 인스턴스들 초기화
public class UserService {

    private final UserRepository userRepository; //UserRepository 포함 관계로 가져오기
    private final JwtUtil jwtUtil; //JwtUtio 포함 관계로 가져오기


    //회원가입
    @Transactional //@Transactional - 이용해 모든 작업들이 성공해야만 최종적으로 데이터베이스에 반영
    public StatusResponseDto signup(UserRequestDto userRequestDto) {

        // 회원 중복 확인
        Optional<User> found = userRepository.findByUsername(userRequestDto.getUsername()); //NPE 방지를 위해 Optional wrapper 클래스로 변수 생성
        if (found.isPresent()) { //found가 null이 아닌지 확인하는 메서드
            return new StatusResponseDto("이미 회원가입이 된 사용자입니다.", HttpStatus.ALREADY_REPORTED);
            //매개변수로 받은 requestDto에 있는 유저의 이름이 userRepository에 있다면 이미 회원가입이 된 사용자라는 뜻이라 반환 처리 함
        }

        //회원 중복이 아닐 시
        User user = new User(userRequestDto); //dto를 이용해 user entity 생성
        userRepository.save(user); //생성한 user를 repository에 저장
        return new StatusResponseDto("회원가입을 성공적으로 마쳤습니다.", HttpStatus.OK); //완료 메세지와 상태코드 반환
    }

    //로그인
    @Transactional //@Transactional - 이용해 모든 작업들이 성공해야만 최종적으로 데이터베이스에 반영
    public StatusResponseDto login(UserRequestDto userRequestDto, HttpServletResponse response) {

        String username = userRequestDto.getUsername(); //받은 requestDto에서 username을 빼서 저장함
        String password = userRequestDto.getPassword(); //받은 requestDto에서 password를 빼서 저장함

        // 사용자 확인
        User user = userRepository.findByUsername(username).orElseThrow( //저장한 username을 이용해 repository에서 user를 찾기
                () -> new IllegalArgumentException("등록된 사용자가 없습니다.") //못 찾을 시 회원가입이 안 된 사용자
        );

        // 비밀번호 확인
        if(!user.getPassword().equals(password)){ //저장한 password와 username을 이용해 찾은 user의 password가 일치하는지 확인
            throw  new IllegalArgumentException("비밀번호가 일치하지 않습니다."); //불일치 할 시 비밀번호가 틀림
        }

        //http header에 jwt 토큰 발급
        response.addHeader(JwtUtil.AUTHORIZATION_HEADER, jwtUtil.createToken(user.getUsername())); //addHeader 메서드를 이용해 header에 토큰 저장

        return new StatusResponseDto("로그인이 완료되었습니다.", HttpStatus.OK); //완료 메세지와 상태코드 반환
    }
}