package com.huotu.mallduobao.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * 系统配置
 * <p>
 * DatabaseVersion 数据库版本
 */
@Entity
@Getter
@Setter
@AllArgsConstructor
public class SystemConfig {
    @Id
    @Column(length = 50)
    private String code;

    @Column(nullable = false, length = 100)
    private String valueForCode;

    @Column(length = 100)
    private String remark;

    public SystemConfig() {
    }
}