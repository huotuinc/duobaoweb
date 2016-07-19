package com.huotu.mallduobao.entity;


import com.huotu.mallduobao.utils.CodeType;
import com.huotu.mallduobao.utils.VerificationType;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;

/**
 * 验证码
 */
@Entity
@Getter
@Setter
@Table(indexes = {@Index(columnList = "mobile"),@Index(columnList = "sendTime")})
public class VerificationCode {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false,length = 11)
    private String mobile;

    @Column(nullable = false)
    private VerificationType type;

    private CodeType codeType;

    @Column(nullable = false,length = 8)
    private String code;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    private Date sendTime;

}