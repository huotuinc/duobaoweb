package com.huotu.duobaoweb.service.impl;


import com.huotu.duobaoweb.common.CommonEnum;
import com.huotu.duobaoweb.entity.Delivery;
import com.huotu.duobaoweb.entity.pk.DeliveryPK;
import com.huotu.duobaoweb.model.DeliveryModel;
import com.huotu.duobaoweb.repository.DeliveryRepository;
import com.huotu.duobaoweb.service.DeliveryService;
import com.huotu.duobaoweb.service.StaticResourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URISyntaxException;
import java.util.Date;

/**
 * Created by lhx on 2016/2/20.
 */
@Service
public class DeliveryServiceImpl implements DeliveryService {

    @Autowired
    DeliveryRepository deliveryRepository;

    @Autowired
    StaticResourceService staticResourceService;

    @Override
    public Delivery getOneByIssueId(Long issueId) {
        return deliveryRepository.findByIssueId(issueId);
    }

    @Override
    public DeliveryModel findByIssueId(Long issueId) throws URISyntaxException {
        Delivery delivery = deliveryRepository.findByIssueId(issueId);
        DeliveryModel deliveryModel = null;
        if (delivery!=null){
            deliveryModel = new DeliveryModel();
            //todo 由于程序运行不起来注释了下面的代码 by xhk
            deliveryModel.setPid(delivery.getIssue().getId());
            deliveryModel.setDeliveryStatus(delivery.getDeliveryStatus().getValue());
            deliveryModel.setDetails(delivery.getDetails());
            deliveryModel.setReceiver(delivery.getReceiver());
            deliveryModel.setMobile(delivery.getMobile());
//            deliveryModel.setRecieveGoodsTime(delivery.getRecieveGoodsTime());
//            deliveryModel.setDeliveryTime(delivery.getDeliveryTime());
            deliveryModel.setConfirmAddressTime(delivery.getConfirmAddressTime());
            deliveryModel.setIssueId(delivery.getIssue().getId());
            deliveryModel.setUserId(delivery.getUser().getId());
            deliveryModel.setUsername(delivery.getUser().getUsername());
            deliveryModel.setAwardingDate(delivery.getIssue().getAwardingDate());
            deliveryModel.setTitle(delivery.getIssue().getGoods().getTitle());
            deliveryModel.setToAmount(delivery.getIssue().getToAmount());
            deliveryModel.setDefaultPictureUrl(staticResourceService.getResource(delivery.getIssue().getGoods().getDefaultPictureUrl()).toString());
            deliveryModel.setLuckyNumber(delivery.getIssue().getLuckyNumber());
        }
        return deliveryModel;
    }

    @Override
    @Transactional
    public Delivery updataDelivery(Delivery delivery) {
        return deliveryRepository.saveAndFlush(delivery);
    }

    @Override
    public Delivery findById(Long deliveryId) {
        return deliveryRepository.findOne(deliveryId);
    }


    @Override
    @Transactional
    public void addRecpeiptAddress(Long deliveryId, String receiver, String mobile, String details) {
        Delivery delivery = deliveryRepository.findByIssueId(deliveryId);
        delivery.setReceiver(receiver);
        delivery.setMobile(mobile);
        delivery.setDetails(details);
        delivery.setConfirmAddressTime(new Date());
        delivery.setDeliveryStatus(CommonEnum.DeliveryStatus.ConfirmAddress);
        deliveryRepository.saveAndFlush(delivery);
    }


}
