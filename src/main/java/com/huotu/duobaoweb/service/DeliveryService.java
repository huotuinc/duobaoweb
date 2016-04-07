package com.huotu.duobaoweb.service;


import com.huotu.duobaoweb.entity.Delivery;
import com.huotu.duobaoweb.model.DeliveryModel;
import org.springframework.data.domain.Page;

import java.net.URISyntaxException;

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
    void addRecpeiptAddress(Long deliveryId, String receiver, String mobile, String details);
}
