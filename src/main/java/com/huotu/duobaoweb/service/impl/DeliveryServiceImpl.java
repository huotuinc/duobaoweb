package com.huotu.duobaoweb.service.impl;


import com.huotu.duobaoweb.entity.Delivery;
import com.huotu.duobaoweb.model.DeliveryModel;
import com.huotu.duobaoweb.repository.DeliveryRepository;
import com.huotu.duobaoweb.service.DeliveryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by lhx on 2016/2/20.
 */
@Service
public class DeliveryServiceImpl implements DeliveryService {

    @Autowired
    DeliveryRepository deliveryRepository;

    @Override
    public Delivery getOneByIssueId(Long issueId) {
        return deliveryRepository.findByIssueId(issueId);
    }

    @Override
    public DeliveryModel findByIssueId(Long issueId) {
        Delivery delivery = deliveryRepository.findByIssueId(issueId);
        DeliveryModel deliveryModel = null;
        if (delivery!=null){
            deliveryModel = new DeliveryModel();
            //todo 由于程序运行不起来注释了下面的代码 by xhk
            //deliveryModel.setPid(delivery.getId());
            deliveryModel.setDeliveryStatus(delivery.getDeliveryStatus().getValue());
            deliveryModel.setDetails(delivery.getDetails());
            deliveryModel.setReceiver(delivery.getReceiver());
            deliveryModel.setMobile(delivery.getMobile());
            deliveryModel.setRecieveGoodsTime(delivery.getRecieveGoodsTime());
            deliveryModel.setDeliveryTime(delivery.getDeliveryTime());
            deliveryModel.setConfirmAddressTime(delivery.getConfirmAddressTime());
            deliveryModel.setIssueId(delivery.getIssue().getId());
            deliveryModel.setUserId(delivery.getUser().getId());
            deliveryModel.setUsername(delivery.getUser().getUsername());
            deliveryModel.setAwardingDate(delivery.getIssue().getAwardingDate());
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


}
