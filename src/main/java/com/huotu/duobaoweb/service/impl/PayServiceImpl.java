package com.huotu.duobaoweb.service.impl;

import com.huotu.duobaoweb.common.CommonEnum;
import com.huotu.duobaoweb.entity.Orders;
import com.huotu.duobaoweb.entity.OrdersItem;
import com.huotu.duobaoweb.entity.UserBuyFlow;
import com.huotu.duobaoweb.entity.UserMoneyFlow;
import com.huotu.duobaoweb.model.PayResultModel;
import com.huotu.duobaoweb.repository.OrdersItemRepository;
import com.huotu.duobaoweb.repository.OrdersRepository;
import com.huotu.duobaoweb.repository.UserBuyFlowRepository;
import com.huotu.duobaoweb.repository.UserMoneyFlowRepository;
import com.huotu.duobaoweb.service.PayService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * Created by xhk on 2016/3/29.
 */
@Service
public class PayServiceImpl implements PayService {

    private static Log log = LogFactory.getLog(PayServiceImpl.class);

    @Autowired
    private OrdersRepository ordersRepository;

    @Autowired
    private OrdersItemRepository ordersItemRepository;

    @Autowired
    private UserBuyFlowRepository userBuyFlowRepository;

    @Autowired
    private UserMoneyFlowRepository userMoneyFlowRepository;

    @Override
    public PayResultModel solveWeixinPayResult(String orderNo,float money,String outOrderNo) {
        Orders orders=ordersRepository.findOne(orderNo);
        return this.doPay(orders,money, outOrderNo, CommonEnum.PayType.weixin);
    }

    /**
     * 支付成功后的操作，将物品数量减少，
     * @param orders
     * @param money
     */
    @Transactional
    private synchronized PayResultModel doPay(Orders orders, float money,String outOrderNo, CommonEnum.PayType purchaseSource) {
        log.info("进入支付主流程，in doPay()!");
        Date date=new Date();
        PayResultModel resultModel=new PayResultModel();
        OrdersItem ordersItem=ordersItemRepository.findByOrderId(orders.getId());

        if (orders == null||ordersItem==null) { //如果订单不存在
            log.info("订单不存在，支付失败!");
            resultModel.setSuccess(false);
            return resultModel;
        }
        //如果订单已经支付，则直接返回成功
        if(orders.getStatus().equals(CommonEnum.OrderStatus.payed)){
            log.info("订单已经支付成功!");
            resultModel.setSuccess(true);
            return resultModel;
        }
        //若果应支付金额与实际支付金额不符则返回false
        if(orders.getMoney().compareTo(new BigDecimal(String.valueOf(money)))!=0){
            log.info("实际支付的金额数量不合法!orders.getMoney():"+orders.getMoney().doubleValue()+";actral money:"+money);
            resultModel.setSuccess(false);
            return resultModel;
        }
        if(ordersItem.getIssue().getToAmount()<(ordersItem.getAmount()+ordersItem.getIssue().getBuyAmount())){

            log.info("期号已经被买满，进入下一期!");
            //用户的支付失败，转到下一期，当没有下一期的时候，就直接是支付失败
            //todo 罗国华接口
            resultModel.setSuccess(false);
            return resultModel;
        }
        log.info("开始正常的购买!");
        //正常的购买
        ordersItem.getIssue().setBuyAmount(ordersItem.getIssue().getBuyAmount()+ordersItem.getAmount());
        //todo 用户得到号码，罗国华接口 赋值到结果集
        //如果用户购买的数量已经达到总需数，则进行新的一期生成
        if(ordersItem.getIssue().getToAmount()<=ordersItem.getIssue().getBuyAmount()){
            log.info("开始生成新的期号!");
            //todo 更新期号 罗国华接口
        }

        ordersItem.setStatus(CommonEnum.OrderStatus.payed);

        orders.setPayType(purchaseSource);
        orders.setOutOrderNo(outOrderNo);
        orders.setStatus(CommonEnum.OrderStatus.payed);
        orders=ordersRepository.saveAndFlush(orders);
        ordersItem=ordersItemRepository.saveAndFlush(ordersItem);

        //用户购买流水
        UserBuyFlow userBuyFlow = new UserBuyFlow();
        //如果用户流水中已经有了对应期号对应用户的流水，则更新
        List<UserBuyFlow> userBuyFlows = userBuyFlowRepository.findAllByIssueAndUser(ordersItem.getIssue().getId(), orders.getUser().getId());
        if (userBuyFlows.size() != 0) {
            userBuyFlow = userBuyFlows.get(0);
            userBuyFlow.setAmount(ordersItem.getAmount() + userBuyFlow.getAmount());
            userBuyFlow.setTime(date.getTime());
        } else {
            userBuyFlow.setAmount(ordersItem.getAmount());
            userBuyFlow.setTime(date.getTime());
            userBuyFlow.setIssue(ordersItem.getIssue());
            userBuyFlow.setUser(orders.getUser());
        }
        userBuyFlow = userBuyFlowRepository.saveAndFlush(userBuyFlow);

        //金额购买流水
        UserMoneyFlow userMoneyFlowBuy = new UserMoneyFlow();
        userMoneyFlowBuy.setMoney(ordersItem.getIssue().getPricePercentAmount().multiply(new BigDecimal(String.valueOf(ordersItem.getAmount())))); //购买的为一部分的钱
        userMoneyFlowBuy.setUser(orders.getUser());
        //userMoneyFlow.setCurrentMoney(userOrder.getMoney().add(userOrder.getUser().getMoney()));
        userMoneyFlowBuy.setTime(date);
        userMoneyFlowBuy.setMoneyFlowType(CommonEnum.MoneyFlowType.buy);
        userMoneyFlowBuy.setRemarek("购买");
        userMoneyFlowBuy.setCurrentMoney(orders.getUser().getMoney());
        userMoneyFlowBuy = userMoneyFlowRepository.saveAndFlush(userMoneyFlowBuy);
        log.info("订单主流程处理完毕!");
        resultModel.setSuccess(true);
        return resultModel;
    }


}
