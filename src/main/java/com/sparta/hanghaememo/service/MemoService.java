package com.sparta.hanghaememo.service;

import com.sparta.hanghaememo.dto.SuperTypeDto;
import com.sparta.hanghaememo.dto.MemoRequestDto;
import com.sparta.hanghaememo.dto.MemoResponseDto;
import com.sparta.hanghaememo.dto.StatusResponseDto;
import com.sparta.hanghaememo.entity.Memo;
import com.sparta.hanghaememo.entity.User;
import com.sparta.hanghaememo.jwt.JwtUtil;
import com.sparta.hanghaememo.repository.MemoRepository;
import com.sparta.hanghaememo.repository.UserRepository;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service //서비스 계층
@RequiredArgsConstructor //포함관계로 가져온 클래스의 인스턴스들 초기화
public class MemoService {

    private final MemoRepository memoRepository; //MemoRepository 포함관계로 가져오기
    private final UserRepository userRepository; //UserRepository 포함관계로 가져오기
    private final JwtUtil jwtUtil; //JwtUtil 포함관계로 가져오기

    //메모 생성
    @Transactional //@Transactional - 이용해 모든 작업들이 성공해야만 최종적으로 데이터베이스에 반영
    public SuperTypeDto createMemo(MemoRequestDto requestDto, HttpServletRequest request) {
        String token = jwtUtil.resolveToken(request); //jwtUtil의 resolveToken 메서드를 이용해 토큰을 가져옴
//        Claims claims = jwtUtil.getUserInfoFromToken(token);

        User user = getUserByToken(token); //getUserByToken() 메서드를 이용해 user를 생성함

        if(user != null){ //생성한 유저가 null이 아닐 때
            Memo memo = new Memo(requestDto); //요청받은 내용으로 메모를 저장하고
            memo.setUser(user); //해당 메모의 유저를 저장함
            memoRepository.save(memo); //저장한 메모를 repository에 저장하고
            return new MemoResponseDto(memo); //메모를 return 함
        }else{
            return new StatusResponseDto("사용할 수 없는 토큰입니다.", HttpStatus.BAD_REQUEST); //생성한 유저가 null일 시 오류 메세지와 상태 코드 출력
        }

    }


    //메모 전체 조회
    @Transactional(readOnly = true)
    public List<MemoResponseDto> getMemos() {
        List<Memo> lists = memoRepository.findAllByOrderByModifiedAtDesc();

        List<MemoResponseDto> memos = new ArrayList();

        for(Memo memo : lists){
            memos.add(new MemoResponseDto(memo));
        }

        return memos;
    }


    //메모 수정
    @Transactional //@Transactional - 이용해 모든 작업들이 성공해야만 최종적으로 데이터베이스에 반영
    public SuperTypeDto update(Long id, MemoRequestDto requestDto, HttpServletRequest request) {
        String token = jwtUtil.resolveToken(request); //jwtUtil의 resolveToken 메서드를 이용해 토큰을 가져옴

        //ID 확인
        Memo memo = memoRepository.findById(id).orElseThrow( //매개변수의 id로 repository에서 메모를 찾아 저장함
                () -> new IllegalArgumentException("아이디가 존재하지 않습니다.") //에러가 나면 잘못된 아이디
        );

        User user = getUserByToken(token); //getUserByToken() 메서드를 이용해 user를 생성함

        //(메모의 작성자 == 수정을 요청한 요청자) 확인
        if(memo.getUser().getUsername().equals(user.getUsername())){ //id를 이용해 저장한 메모에서 꺼낸 유저의 유저네임과 토큰을 이용해 저장한 유저의 유저네임이 같은지 비교
            memo.update(requestDto); //같을 시 메모 업데이트
        }else{
            return new StatusResponseDto("해당 메모의 작성자만 수정이 가능합니다.", HttpStatus.BAD_REQUEST); //다를 시 에러 메세지와 상태코드 반환
        }
        return new MemoResponseDto(memo); //정상적으로 작동이 끝날 시 MemoResponseDto 반환
    }


    //메모 삭제
    @Transactional //@Transactional - 이용해 모든 작업들이 성공해야만 최종적으로 데이터베이스에 반영
    public StatusResponseDto deleteMemo(Long id, HttpServletRequest request) {
        String token = jwtUtil.resolveToken(request); //jwtUtil의 resolveToken 메서드를 이용해 토큰을 가져옴
        User user = getUserByToken(token); //getUserByToken() 메서드를 이용해 user를 생성함

        //ID 확인
        Memo memo = memoRepository.findById(id).orElseThrow( //매개변수의 id로 repository에서 메모를 찾아 저장함
                () -> new IllegalArgumentException("아이디가 존재하지 않습니다.") //에러가 나면 잘못된 아이디
        );

        //(메모의 작성자 == 수정을 요청한 요청자) 확인
        if(user.getUsername().equals(memo.getUser().getUsername())){ //id를 이용해 저장한 메모에서 꺼낸 유저의 유저네임과 토큰을 이용해 저장한 유저의 유저네임이 같은지 비교
            memoRepository.deleteById(memo.getId()); //같을 시 repository에서 memo 삭제
        }else{
            return new StatusResponseDto("해당 메모의 작성자만 메모를 삭제할 수 있습니다.", HttpStatus.BAD_REQUEST); //다를 시 오류 메세지와 상태코드 반환
        }
        return new StatusResponseDto("삭제를 성공적으로 마쳤습니다.", HttpStatus.OK); //정상적으로 작동 시 완료 메세지와 상태코드 반환
    }


    //메모 상세 조회
    public MemoResponseDto getMemo(Long id) {
        List<Memo> lists = memoRepository.findAllByOrderByModifiedAtDesc();

        List<MemoResponseDto> memos = new ArrayList();
        MemoResponseDto selectedMemo = null;

        for (Memo memo : lists) {
            if (memo.getId() == id) {
                selectedMemo = new MemoResponseDto(memo);
            }

            if(selectedMemo == null){
                MessageService.getMemo();
            }

        }
        return selectedMemo;
    }


    //해당 메서드 사용해서 예외처리를 이용하면 서버 에러로 나옴 (500, Server Error) 나중에 수정하기
    //토큰을 받아 유저를 출력하는 메서드
    public User getUserByToken(String token){
        Claims claims; //정보를 저장할 claims 미리 저장

        if(token != null){ //입력받은 토큰이 null이 아닐 시
            if(jwtUtil.validateToken(token)){ //jwtUtil의 ValidateToken 메서드를 이용해 해당 토큰이 유효한지 검사
                claims = jwtUtil.getUserInfoFromToken(token); //getUserInfoFromToken 메서드를 이용해 유저 정보를 가져옴
            }else{
                throw new IllegalArgumentException("허가되지 않은 토큰입니다."); //예외 처리
            }

            User user = userRepository.findByUsername(claims.getSubject()).orElseThrow( //claims의 getSubject()를 이용해 유저네임으로 사용하여 repository에서 유저를 찾아 저장
                    () -> new IllegalArgumentException("존재하지 않는 사용자입니다.") //예외 처리
            );
            return user; //찾은 유저를 반환
        }
        return null;
    }
}
