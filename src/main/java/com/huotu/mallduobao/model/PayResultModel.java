package com.huotu.mallduobao.model;

import com.huotu.mallduobao.common.CommonEnum;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Created by xhk on 2016/4/7.
 */
@Setter
@Getter
public class PayResultModel {

    /**
     * 是否支付成功
     */
    private boolean success;

    /**
     * 成功支付得到的号码
     */
    private List<Long> resultNumber;

    /**
     *支付结果类型
     */
    private CommonEnum.PayResult resultType;
}
