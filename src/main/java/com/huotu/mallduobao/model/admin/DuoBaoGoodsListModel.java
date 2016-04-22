package com.huotu.mallduobao.model.admin;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by zhang on 2016/4/1.
 */
@Setter
@Getter
public class DuoBaoGoodsListModel {

    private Long id;

    private String title;

    private String characters;

    private Long stepAmount;

    private Long defaultAmount;

    private Long toAmount;

    private BigDecimal pricePercentAmount;

    private String statusName;

    private Date startTime;

    private Date endTime;

    private Integer status;


}
