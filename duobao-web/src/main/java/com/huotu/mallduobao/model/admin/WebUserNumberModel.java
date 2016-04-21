package com.huotu.mallduobao.model.admin;
import com.huotu.mallduobao.entity.Issue;
import com.huotu.mallduobao.entity.Orders;
import com.huotu.mallduobao.entity.User;
import lombok.Getter;
import lombok.Setter;

/**
 * todo 实体待处理
 * Created by lhx on 2016/3/26.
 */
@Getter
@Setter
public class WebUserNumberModel {
    /**
     * 期号
     */
    private Issue issue;
    /**
     * 抽奖号码 (8位数字)
     * 格式 10005888
     */
    private Long number;

    /**
     * 用户
     */
    private User user;

    /**
     * 购买时间
     */
    private Long time;

    /**
     * 所属订单
     */
    private Orders orders;


}
