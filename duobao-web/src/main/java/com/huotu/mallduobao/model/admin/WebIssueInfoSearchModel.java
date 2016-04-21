package com.huotu.mallduobao.model.admin;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by zhang on 2016/3/7.
 */
@Getter
@Setter
public class WebIssueInfoSearchModel {

    /**
     * 商品Id
     */
    private Long goodsId = 0L;

    /**
     * 状态 0进行中 1待开奖 2已揭晓
     */
    private Integer status = -1;

    /**
     * 期号
     */
    private Long issueId;
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
     * 指定查询页码
     */
    private Integer pageNoStr = 0;


}
