package com.sparta.hanghaememo.entity;

import com.sparta.hanghaememo.dto.UserRequestDto;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity(name = "users") //user은 예약어라 users로 이름 명명
@Getter
@NoArgsConstructor
public class User {

    @Id //JPA가 User Entity에 관련된 쿼리를 작성할때 이 필드를 이용하게 함, PK
    @Column(nullable = false, unique = true) //null 안 받고 유일하다는 조건
    private String username;

    @Column(nullable = false) //null 불가
    private String password;

    public User(UserRequestDto userRequestDto) {
        this.username = userRequestDto.getUsername();
        this.password = userRequestDto.getPassword();
    }
}
