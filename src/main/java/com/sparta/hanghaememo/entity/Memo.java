package com.sparta.hanghaememo.entity;

import com.sparta.hanghaememo.dto.MemoRequestDto;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@NoArgsConstructor
public class Memo extends Timestamped {
    @Id //JPA가 User Memo에 관련된 쿼리를 작성할때 이 필드를 이용하게 함, PK
    @GeneratedValue(strategy = GenerationType.AUTO) //자동 생성 전략을 지정하는 어노테이션, Auto로 설정했기 때문에 전략 중 하나를 자동으로 선택함
    private Long id;

    @Column(nullable = false) //null 불가
    private String contents;

    @Column(nullable = false) //null 불가
    private String title;

    @ManyToOne(fetch = FetchType.LAZY) //다대일 연관관계, 지연로딩 이용
    @JoinColumn(name = "username") //Memo 테이블에 username이라는 컬럼명 정의
    private User user;

    public Memo(MemoRequestDto requestDto) {
        this.contents = requestDto.getContents();
        this.title = requestDto.getTitle();
    }

    public void update(MemoRequestDto requestDto) {
//        this.username = requestDto.getUsername();
        this.contents = requestDto.getContents();
        this.title = requestDto.getTitle();
    }
}