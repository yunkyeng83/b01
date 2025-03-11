package org.zerock.b01.domain;

import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import java.time.LocalDateTime;

@MappedSuperclass // entity를 상속해 주기 위해서 사용하는 어노테이션
@EntityListeners(value = {AuditingEntityListener.class}) // Auditing된 거에 대해서만 listener로 설정
@Getter
public class BaseEntity {
    @CreatedDate  // insert할 경우에 기본값으로 생성시간을 주입하는 어노테이션
    @Column(name = "regdate", updatable = false)  // 실제 테이블의 컬럼 이름을 regdate이고 update문에는 컬럼이 들어가지 않도록 설정
    private LocalDateTime regDate;

    @LastModifiedDate // update할 경우에 마지막으로 update하는 시간으로 세팅
    @Column(name = "moddate")
    private LocalDateTime modDate;
}
