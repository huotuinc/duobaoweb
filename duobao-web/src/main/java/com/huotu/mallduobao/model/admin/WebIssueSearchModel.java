package com.huotu.mallduobao.model.admin;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by admin on 2016/2/17.
 */
@Getter
@Setter
public class WebIssueSearchModel {
    /**
     * 中奖用户名
     */
    String username;

    /**
     * 中奖用户Id
     */
    Long userId;

    /**
     * 期号
     */
    String issueId;

    /**
     * 某期状态
     */
    Integer issueStatus;

    /**
     * 商品标题
     */
    String goodsTitle;

    /**
     * 商品ID
     */
    Long goodsId;

    /**
     * 排序字段 0：序号(ID)
     *
     */
    private Integer sort=0;

    /**
     * 排序方式 0：降序|1：升序
     */
    private Integer raSortType = 0;

    /**
     * 开始时间
     */
    private String startTime="";

    /**
     * 结束时间
     */
    private String endTime="";

    /**
     * 指定查询页码
     */
    private Integer pageNoStr = 0;

    /**
     * 商家id
     */
    private Long customerId;


}
