package com.huotu.duobaoweb.model;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by xhk on 2016/3/28.
 */
@Setter
@Getter
public class ShoppingCartsModel {


    /**
     * ���ﳵid
     */
    private Long cartId;
    /**
     * ��Ʒ����
     */
    private String detail;

    /**
     * ��������
     */
    private Long needNumber;

    /**
     * ʣ������
     */
    private Long leftNumber;

    /**
     * ������
     */
    private Long buyNum;

    /**
     *������
     */
    private Double buyMoney;

    /**
     * ����
     */
    private Double perMoney;

    /**
     * ���ﳵͼƬ·��
     */
    private String imgUrl;
}
