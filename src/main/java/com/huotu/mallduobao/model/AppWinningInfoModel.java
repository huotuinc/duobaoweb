package com.huotu.mallduobao.model;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * Created by admin on 2016/3/9.
 */
@Getter
@Setter
public class AppWinningInfoModel implements Serializable {
    /**
     * 用户ID
     */
    Long userId;

    /**
     * 红包信息
     */
    String winningInfo;

    /**
     * 红包Id
     */
    Long rid;

}
