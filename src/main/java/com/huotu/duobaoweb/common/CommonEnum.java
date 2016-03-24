package com.huotu.duobaoweb.common;

import com.huotu.common.api.ICommonEnum;

/**
 * Created by lgh on 2016/1/15.
 */
public interface CommonEnum {

    /**
     * 支付类型
     */
    enum PayType implements ICommonEnum {
        weixin(0, "微信支付"),

        alipay(1, "支付宝支付"),

        remain(2, "余额支付"),
        simulate(3, "模拟支付");

        private int value;

        private String name;

        PayType(int value, String name) {
            this.value = value;
            this.name = name;
        }


        public int getValue() {
            return value;
        }

        public void setValue(int value) {
            this.value = value;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }


    /**
     * 订单状态
     */
    enum OrderStatus implements ICommonEnum {
        paying(0, "待付款"),
        payed(1, "支付完成"),
        havenotenough(2, "订单中库存存在不足"), //订单
        changetosave(3, "期号数量不足,子订单金额退到余额"),//订单详情
        changetofail(4, "期号数量不足,子订单余额购买失败"), //订单详情
        fail(5, "失败");

        private int value;
        private String name;

        OrderStatus(int value, String name) {
            this.value = value;
            this.name = name;
        }

        public int getValue() {
            return value;
        }

        public void setValue(int value) {
            this.value = value;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    /**
     * 金钱流水类型
     */
    enum MoneyFlowType implements ICommonEnum {
        weixin(0, "微信支付"),
        alipay(1, "支付宝支付"),
        buy(2, "购买"),
        put(3, "充值红包");

        private int value;

        private String name;

        MoneyFlowType(int value, String name) {
            this.value = value;
            this.name = name;
        }


        public int getValue() {
            return value;
        }

        public void setValue(int value) {
            this.value = value;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    /**
     * 版本升级类型
     */
    enum VersionUpdateType implements ICommonEnum {

        NO(0, "无更新"),

        INCREMENT(1, "增量更新"),

        WHOLE(2, "整包更新"),

        FORCE_INCREMENT(3, "强制增量更新"),

        FORCE_WHOLE(4, "强制整包更新");

        private int value;

        public int getValue() {
            return value;
        }

        public void setValue(int value) {
            this.value = value;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        private String name;

        VersionUpdateType(int value, String name) {
            this.value = value;
            this.name = name;
        }
    }

    /**
     * 用户来源类型
     */
    enum UserFromType {
        register(0, "直接注册"),
        qq(1, "qq授权"),
        weixin(2, "微信授权");
//        simulate(3, "模拟");

        private int value;

        public int getValue() {
            return value;
        }

        public void setValue(int value) {
            this.value = value;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        private String name;

        UserFromType(int value, String name) {
            this.value = value;
            this.name = name;
        }
    }


    /**
     * 订单类型
     */
    enum OrderType implements ICommonEnum {
        raiders(0, "夺宝"),
        put(1, "充值"),
        change(2, "部分夺宝，部分充值"),
        simulate(3, "模拟购买");

        private int value;

        private String name;

        OrderType(int value, String name) {
            this.value = value;
            this.name = name;
        }


        public int getValue() {
            return value;
        }

        public void setValue(int value) {
            this.value = value;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    /**
     * 商品状态
     */
    enum GoodsStatus implements ICommonEnum {
        uncheck(0, "未审核"),
        up(1, "已上架"),
        down(2, "下架");

        private int value;

        private String name;

        GoodsStatus(int value, String name) {
            this.value = value;
            this.name = name;
        }


        public int getValue() {
            return value;
        }

        public void setValue(int value) {
            this.value = value;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    enum IssueStatus implements ICommonEnum {
        going(0, "进行中"),
        drawing(1, "待开奖"),
        drawed(2, "已开奖");

        private int value;

        private String name;

        IssueStatus(int value, String name) {
            this.value = value;
            this.name = name;
        }


        public int getValue() {
            return value;
        }

        public void setValue(int value) {
            this.value = value;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }


    /**
     * 红包状态
     */
    enum RedPacketsStatus implements ICommonEnum {
        unused(0, "未使用"),
        using(1, "使用中"),
        used(2, "已使用"),
        expire(3, "过期");

        private int value;

        private String name;

        RedPacketsStatus(int value, String name) {
            this.value = value;
            this.name = name;
        }


        public int getValue() {
            return value;
        }

        public void setValue(int value) {
            this.value = value;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    /**
     * 发货状态
     */
    enum DeliveryStatus implements ICommonEnum {

        /**
         * 获得奖品 默认状态
         */
        GetPrize(0, "获得奖品"),
        ConfirmAddress(1, "确认收货地址"),
        WaitingDelivery(2, "等待奖品派发"),
        RecieveGoods(4, "确认收货"),
        Finished(5, "已收货"),
        ShareOrdering(6, "已晒单");
        private int value;

        private String name;

        DeliveryStatus(int value, String name) {
            this.value = value;
            this.name = name;
        }


        public int getValue() {
            return value;
        }

        public void setValue(int value) {
            this.value = value;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    /**
     * 红包类型
     */
    enum RedPacketType implements ICommonEnum {
        put(0, "充值红包"),
        deducation(1, "抵扣红包");

        private int value;

        private String name;

        RedPacketType(int value, String name) {
            this.value = value;
            this.name = name;
        }


        public int getValue() {
            return value;
        }

        public void setValue(int value) {
            this.value = value;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    enum RedPactketsDistributeType implements ICommonEnum {
        BuyAndDeliver(0, "购买并转发"),
        send(1, "领取"),
        put(2, "充值"),
        lottery(3, "抽奖"),
        register(4,"注册");

        private int value;

        private String name;

        RedPactketsDistributeType(int value, String name) {
            this.value = value;
            this.name = name;
        }


        public int getValue() {
            return value;
        }

        public void setValue(int value) {
            this.value = value;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

    }

    /**
     * 推送消息类型
     */
    enum PushMessageType implements ICommonEnum {

        /**
         * 2:红包
         * <p>红包</p>
         */
        RedPackets(2,"红包"),
        /**
         * 3:版本更新
         * <p>携带数据为版本更新摘要</p>
         */
        Version(3, "版本更新"),
        /**
         * 4:任务推送
         * <p>任务ID</p>
         */
        Task(4, "任务推送"),
        /**
         * 5:消息提醒
         * <p>提示阅读消息，携带数据为空</p>
         */
        RemindMessage(5, "消息提醒"),
        /**
         * 6:通知
         * <p>携带数据为纯文本消息</p>
         */
        Notify(6, "通知");

        private int value;

        public int getValue() {
            return value;
        }

        public void setValue(int value) {
            this.value = value;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        private String name;

        PushMessageType(int value, String name) {
            this.value = value;
            this.name = name;
        }


    }

    /**
     * 购买并转发类型
     */
    enum BuyAndTurnType {
        none(0, "默认"),
        buy(1, "购买"),
        turn(2, "转发");
        private int value;

        public int getValue() {
            return value;
        }

        public void setValue(int value) {
            this.value = value;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        private String name;

        BuyAndTurnType(int value, String name) {
            this.value = value;
            this.name = name;
        }
    }
}
