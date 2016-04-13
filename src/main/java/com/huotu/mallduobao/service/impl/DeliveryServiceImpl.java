package com.huotu.mallduobao.service.impl;


import com.huotu.common.base.HttpHelper;
import com.huotu.mallduobao.common.CommonEnum;
import com.huotu.mallduobao.common.PublicParameterHolder;
import com.huotu.mallduobao.entity.Delivery;
import com.huotu.mallduobao.model.DeliveryModel;
import com.huotu.mallduobao.model.WebPublicModel;
import com.huotu.mallduobao.repository.DeliveryRepository;
import com.huotu.mallduobao.service.CommonConfigService;
import com.huotu.mallduobao.service.DeliveryService;
import com.huotu.mallduobao.service.StaticResourceService;
import com.huotu.huobanplus.sdk.common.repository.MerchantRestRepository;
import com.jayway.jsonpath.JsonPath;
import org.codehaus.plexus.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by lhx on 2016/2/20.
 */
@Service
public class DeliveryServiceImpl implements DeliveryService {

    @Autowired
    DeliveryRepository deliveryRepository;

    @Autowired
    StaticResourceService staticResourceService;

    @Autowired
    CommonConfigService commonConfigService;

    @Autowired
    MerchantRestRepository merchantRestRepository;

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
            deliveryModel.setConfirmAddressTime(delivery.getConfirmAddressTime());
            deliveryModel.setIssueId(delivery.getIssue().getId());
            deliveryModel.setUserId(delivery.getUser().getId());
            deliveryModel.setUsername(delivery.getUser().getUsername());
            deliveryModel.setAwardingDate(delivery.getIssue().getAwardingDate());
            deliveryModel.setTitle(delivery.getIssue().getGoods().getTitle());
            deliveryModel.setToAmount(delivery.getIssue().getToAmount());
            deliveryModel.setProductSpec(delivery.getProductSpec());
            deliveryModel.setMallOrderId(delivery.getMallOrderId());
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
    public boolean addRecpeiptAddress(Long customerId,Long deliveryId, String receiver, String mobile, String details,String remark) throws IOException {
        WebPublicModel common = PublicParameterHolder.getParameters();
        Delivery delivery = deliveryRepository.findByIssueId(deliveryId);
        if (delivery.getIsCommit()){
            return false;
        }
        Date date = new Date();
        Map<String,String> map = new HashMap<>();
        String [] detail = details.split(",");
        map.put("action","submit");
        //todo lhx
//        map.put("customerid",customerId+"");
        map.put("customerid",3447+"");
        //todo lhx
//        map.put("goodsid",delivery.getIssue().getGoods().getToMallGoodsId()+"");
        map.put("goodsid",17066+"");
        //todo lhx
//        map.put("productid", delivery.getProductId()+"");
        //todo lhx
        map.put("productid", 13281+"");
        map.put("nums","1");
        map.put("shipname",URLEncoder.encode(receiver,"utf-8"));
        map.put("shipmobile",mobile);
        map.put("shipaddr",URLEncoder.encode(detail[detail.length-1],"utf-8"));
        map.put("shipprovince",URLEncoder.encode(detail[0],"utf-8"));
        map.put("shipcity", URLEncoder.encode(detail[1],"utf-8"));
        map.put("shiparea",URLEncoder.encode(detail[2],"utf-8"));
        map.put("memo",URLEncoder.encode(remark,"utf-8"));
//        map.put("openid",common.getOpenId());
        //todo lhx 用户openid
        map.put("openid","oJtvet5XBcAG3J9EK9N4llL_dwaA");
        map.put("memberid","0");
        map.put("srctype","5");
        map.put("srcid",delivery.getIssue().getGoods().getId()+"");
        map.put("timestamp", date.getTime()+ "");
        String sign = getSign(map);
        map.put("sign", sign);
        //todo lhx
        String url = "http://"+merchantRestRepository.getOneByPK(String.valueOf(common.getCustomerId())).getSubDomain()+"."+commonConfigService.getMaindomain().trim()+"/api/order.aspx";
//        url = "http://192.168.1.16:8899/api/order.aspx";
        String json = HttpHelper.postRequest(url, map);
        int code = JsonPath.read(json, "$.code");
        if (code==1){
            delivery.setReceiver(receiver);
            delivery.setMobile(mobile);
            delivery.setDetails(details);
            delivery.setConfirmAddressTime(date);
            delivery.setRemark(remark);
            delivery.setDeliveryStatus(CommonEnum.DeliveryStatus.ConfirmAddress);
            String mallOrderId = JsonPath.read(json, "$.data.orderid");
            delivery.setIsCommit(true);
            delivery.setMallOrderId(mallOrderId);
            delivery.setDeliveryStatus(CommonEnum.DeliveryStatus.ConfirmOrder);
            deliveryRepository.saveAndFlush(delivery);
            return true;
        }
        return false;
    }

    @Override
    public String getSign(Map<String, String> map) throws UnsupportedEncodingException {
        Map<String, String> resultMap = new TreeMap<>();
        for (Object key : map.keySet()) {
            resultMap.put(key.toString(), map.get(key));
        }
        StringBuilder strB = new StringBuilder();
        for (String key : resultMap.keySet()) {
            if (!"sign".equals(key) && !StringUtils.isEmpty(resultMap.get(key))) {
                strB.append("&" + key + "=" + resultMap.get(key));
            }
        }
        String toSign = (strB.toString().length() > 0 ? strB.toString().substring(1) : "") + commonConfigService.getMallKey();
        return DigestUtils.md5DigestAsHex(toSign.getBytes("UTF-8")).toLowerCase();
    }

    @Override
    public void addDeliveryProductInfo(Long issueId, Long productId, String productName) {
        Delivery delivery = deliveryRepository.findByIssueId(issueId);
        delivery.setProductId(productId);
        delivery.setProductSpec(productName);
        delivery.setDeliveryStatus(CommonEnum.DeliveryStatus.ConfirmProduct);
        deliveryRepository.saveAndFlush(delivery);
    }


}
