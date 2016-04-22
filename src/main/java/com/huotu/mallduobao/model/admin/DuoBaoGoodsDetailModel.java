package com.huotu.mallduobao.model.admin;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * Created by zhang on 2016/4/6.
 */
@Getter
@Setter
public class DuoBaoGoodsDetailModel {
    private String title;
    private String characters;
    private Long stepAmount;
    private Long defaultAmount;
    private Long toAmount;
    private BigDecimal pricePercentAmount;
    private Date startTime;
    private Date endTime;
    private List<String> pictureUrls;
    private String shareTitle;
    private String shareDescription;
    private String sharePictureUrl;
    private String mallGoodsTitle;
    private Long attendAmount;
    private Long viewAmount;
    private String merchantNickName;
    private String statusName;
}
