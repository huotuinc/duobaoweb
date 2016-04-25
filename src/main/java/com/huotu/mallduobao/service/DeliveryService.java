package com.huotu.mallduobao.service;


import com.huotu.mallduobao.entity.Delivery;
import com.huotu.mallduobao.model.DeliveryModel;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.util.Map;

/**
 * Created by lhx on 2016/2/20.
 */
public interface DeliveryService {
    /**
     * 中奖纪录的发货单model信息
     * @param issueId 期号id
     * @return AppDeliveryModel
     */
    DeliveryModel findByIssueId(Long issueId) throws URISyntaxException;

    /**
     * 中奖纪录的发货单信息
     * @param issueId 期号id
     * @return Delivery
     */
    Delivery getOneByIssueId(Long issueId);

    /**
     * 修改发货信息
     * @param delivery 发货清单
     * @return
     */
    Delivery updataDelivery(Delivery delivery);

    /**
     * 获取发货信息
     * @param deliveryId 发货单id
     * @return Delivery
     */
    Delivery findById(Long deliveryId);


    /**
     * 添加收货人信息
     * @param deliveryId
     * @param receiver
     * @param mobile
     * @param details
     */
    boolean addRecpeiptAddress(Long deliveryId, String receiver, String mobile, String details,String remark) throws IOException;



    /**
     * 添加货品信息
     * @param issueId
     * @param productId
     * @param productName
     */
    void addDeliveryProductInfo(Long issueId, Long productId, String productName);

}
