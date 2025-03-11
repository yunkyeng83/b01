package org.zerock.b01.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class TblMember {
    @Id
    @Column(length = 50)
    private String mid;
    @Column(length = 50, nullable = false)
    private String mpw;
    @Column(length = 100, nullable = false)
    private String mname;
    @Column(length = 50)
    private String uuid;
}
