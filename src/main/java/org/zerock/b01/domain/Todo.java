package org.zerock.b01.domain;

import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
public class Todo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer tno;
    @Column(length = 100, nullable = false)
    private String title;
    @Column(nullable = false)
    private LocalDate dueDate;
    @Column(length = 500, nullable = false)
    private String writer;
    @ColumnDefault("0")
    private Byte finished;
//    @ColumnDefault("false")
//    private Boolean finished;
}
