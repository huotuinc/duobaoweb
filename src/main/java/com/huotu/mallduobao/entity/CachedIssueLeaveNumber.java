package com.huotu.mallduobao.entity;

import com.huotu.mallduobao.entity.pk.CachedIssueLeaveNumberPK;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Cacheable;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;

/**
 * 缓存的 每期剩余的号码
 * Created by lgh on 2016/1/27.
 */
@Entity
@Getter
@Setter
@Cacheable(value = false)
@IdClass(value = CachedIssueLeaveNumberPK.class)
public class CachedIssueLeaveNumber {

    @Id
    private Long issueId;

    @Id
    private Long number;

    public CachedIssueLeaveNumber() {
    }

    public CachedIssueLeaveNumber(Long issueId, Long number) {
        this.issueId = issueId;
        this.number = number;
    }
}
