package com.huotu.duobaoweb.entity.pk;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * Created by lgh on 2016/1/27.
 */
@Getter
@Setter
public class CachedIssueLeaveNumberPK implements Serializable {
    private Long issueId;

    private Long number;
}
