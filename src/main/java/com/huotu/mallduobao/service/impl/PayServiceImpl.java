package com.huotu.mallduobao.service.impl;

import com.huotu.huobanplus.sdk.common.repository.GoodsRestRepository;
import com.huotu.mallduobao.entity.*;
import com.huotu.mallduobao.model.PayResultModel;
import com.huotu.mallduobao.repository.*;
import com.huotu.mallduobao.service.*;
import com.huotu.mallduobao.utils.CommonEnum;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
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
    private UserNumberService userNumberService;

    @Autowired
    private GoodsRestRepository goodsRestRepository;

    @Autowired
    private RaidersCoreService raidersCoreService;

    @Autowired
    private OrdersItemRepository ordersItemRepository;

    @Autowired
    private UserBuyFlowRepository userBuyFlowRepository;

    @Autowired
    private UserBuyFailRepository userBuyFailRepository;

    @Autowired
    private UserBuyFailService userBuyFailService;

    @Autowired
    private UserMoneyFlowRepository userMoneyFlowRepository;

    @Autowired
    private GoodsService goodsService;


    @Override
    public PayResultModel solveWeixinPayResult(String orderNo, float money, String outOrderNo) throws IOException {
        return this.doPay(orderNo, money, outOrderNo, CommonEnum.PayType.weixin);
    }

    /**
     * 支付成功后的操作，将物品数量减少
     *
     * @param orderNo 订单号
     * @param money 支付金额
     * @param outOrderNo 外部订单号
     * @param purchaseSource 支付类型
     */
    @Transactional
    public synchronized PayResultModel doPay(String orderNo, float money, String outOrderNo, CommonEnum.PayType purchaseSource) throws IOException {
        log.info("进入支付主流程，in doPay()!");
        Orders orders = ordersRepository.findOne(orderNo);
        Date date = new Date();
        PayResultModel resultModel = new PayResultModel();
        if (orders == null) {//如果订单不存在
            log.info("订单不存在，支付失败!");
            resultModel.setSuccess(false);
            return resultModel;
        }
        OrdersItem ordersItem = ordersItemRepository.findByOrderId(orders.getId());

        if (ordersItem == null) { //如果详情订单不存在
            log.info("订单不存在，支付失败!");
            resultModel.setSuccess(false);
            return resultModel;
        }
        //如果订单已经支付，则直接返回成功
        if (orders.getStatus().equals(CommonEnum.OrderStatus.payed)) {
            log.info("订单已经支付成功!");
            resultModel.setSuccess(true);
            resultModel.setResultType(CommonEnum.PayResult.normalPay);
            return resultModel;
        }
        //若果应支付金额与实际支付金额不符则返回false
        if (orders.getMoney().compareTo(new BigDecimal(String.valueOf(money))) != 0) {
            log.info("实际支付的金额数量不合法!orders.getMoney():" + orders.getMoney().doubleValue() + ";actural money:" + money);
            resultModel.setSuccess(false);
            return resultModel;
        }
        if (ordersItem.getIssue().getToAmount() < (ordersItem.getAmount() + ordersItem.getIssue().getBuyAmount())) {

            log.info("期号已经被买满，进入下一期!");
            //用户的支付失败，转到下一期，当没有下一期的时候，就直接是支付失败(这些都异步罗国华处理，这里只需要存到数据库即可)
            userBuyFailService.recordToBuyFail(orders, ordersItem);
            //返回用户成功，提示延期

            //订单标记为失败
            ordersItem.setStatus(CommonEnum.OrderStatus.fail);
            ordersItem = ordersItemRepository.saveAndFlush(ordersItem);
            orders.setStatus(CommonEnum.OrderStatus.fail);

            orders.setPayType(purchaseSource);
            orders.setOutOrderNo(outOrderNo);
            orders = ordersRepository.saveAndFlush(orders);

            resultModel.setResultType(CommonEnum.PayResult.turnToBuyFail);
            resultModel.setSuccess(true);
            return resultModel;
        } else {
            log.info("开始正常的购买!");
            //正常的购买
//        if (orders.getOrderType().equals(CommonEnum.OrderType.allpay)) {
//            log.info("开始全额购买!");
//            //如果是全额购买
//            this.allPay(orders, ordersItem, outOrderNo, purchaseSource);
//            //全额购买的提示信息
//            resultModel.setResultType(CommonEnum.PayResult.allPay);
//            resultModel.setSuccess(true);
//            return resultModel;
//        }
            //如果不是全额购买
            ordersItem.getIssue().setBuyAmount(ordersItem.getIssue().getBuyAmount() + ordersItem.getAmount());
            //跟新当前参与次数
            ordersItem.getIssue().setAttendAmount(ordersItem.getIssue().getAttendAmount() + 1);
            //用户得到号码，罗国华接口 赋值到结果集
            Boolean result = raidersCoreService.generateUserNumber(orders.getUser(), ordersItem.getIssue(), ordersItem.getAmount(), orders);

            //List<Long> userNumbers=userNumberService.getUserNumbersByUserAndIssue(orders.getUser(), ordersItem.getIssue());
            //得到所有的数字来传递给前端显示 暂时不需要了
            //resultModel.setResultNumber(userNumbers);

            ordersItem.setStatus(CommonEnum.OrderStatus.payed);
            ordersItem = ordersItemRepository.saveAndFlush(ordersItem);
            //如果用户购买的数量已经达到总需数，则进行新的一期生成
            if (ordersItem.getIssue().getToAmount() <= ordersItem.getIssue().getBuyAmount()) {
                log.info("开始生成新的期号!");
                //更新商品的参与次数
                Goods goods = goodsService.upateGoodsAttendAmount(ordersItem.getIssue().getGoods());

                //更新期号 罗国华接口
                //Issue issue = raidersCoreService.generateIssue(ordersItem.getIssue().getGoods());
                Issue issue = raidersCoreService.generateIssue(goods);
            }



            orders.setStatus(CommonEnum.OrderStatus.payed);
        }
        orders.setPayType(purchaseSource);
        orders.setOutOrderNo(outOrderNo);
        orders = ordersRepository.saveAndFlush(orders);
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
        userMoneyFlowBuy.setMoney(orders.getMoney()); //购买的为一部分的钱
        userMoneyFlowBuy.setUser(orders.getUser());
        //userMoneyFlow.setCurrentMoney(userOrder.getMoney().add(userOrder.getUser().getMoney()));
        userMoneyFlowBuy.setTime(date);
        userMoneyFlowBuy.setMoneyFlowType(CommonEnum.MoneyFlowType.buy);
        userMoneyFlowBuy.setRemarek("购买");
        userMoneyFlowBuy.setCurrentMoney(orders.getUser().getMoney());
        userMoneyFlowBuy = userMoneyFlowRepository.saveAndFlush(userMoneyFlowBuy);
        log.info("订单主流程处理完毕!");
        resultModel.setSuccess(true);
        resultModel.setResultType(CommonEnum.PayResult.normalPay);
        return resultModel;
    }

    /**
     * 全额购买 暂时废弃 by xhk
     *
     * @param orders
     * @param ordersItem
     */
    private void allPay(Orders orders, OrdersItem ordersItem, String outOrderNo, CommonEnum.PayType purchaseSource) throws IOException {
        //如果是全额购买
        Date date = new Date();
        if (ordersItem.getIssue().getToAmount() <= ordersItem.getIssue().getBuyAmount()) {
            log.info("开始生成新的期号!");
            // 全额购买减库存
            com.huotu.huobanplus.common.entity.Goods goods = goodsRestRepository.getOneByPK(ordersItem.getIssue().getGoods().getToMallGoodsId());
            if (goods.getStock() > 0) {
                goods.setStock(goods.getStock() - 1);
            }
        }
        ordersItem.setStatus(CommonEnum.OrderStatus.payed);
        orders.setPayType(purchaseSource);
        orders.setOutOrderNo(outOrderNo);
        orders.setStatus(CommonEnum.OrderStatus.payed);
        orders = ordersRepository.saveAndFlush(orders);
        ordersItem = ordersItemRepository.saveAndFlush(ordersItem);

        //todo 全额购买流水
        UserBuyFlow userBuyFlow = new UserBuyFlow();
        userBuyFlow.setAmount(ordersItem.getAmount());
        userBuyFlow.setTime(date.getTime());
        userBuyFlow.setIssue(ordersItem.getIssue());
        userBuyFlow.setUser(orders.getUser());

        //todo 全额购买流水
        userBuyFlow = userBuyFlowRepository.saveAndFlush(userBuyFlow);
        UserMoneyFlow userMoneyFlowBuy = new UserMoneyFlow();
        userMoneyFlowBuy.setMoney(ordersItem.getIssue().getPricePercentAmount().multiply(new BigDecimal(String.valueOf(ordersItem.getAmount())))); //购买的为一部分的钱
        userMoneyFlowBuy.setUser(orders.getUser());
        //userMoneyFlow.setCurrentMoney(userOrder.getMoney().add(userOrder.getUser().getMoney()));
        userMoneyFlowBuy.setTime(date);
        userMoneyFlowBuy.setMoneyFlowType(CommonEnum.MoneyFlowType.buy);
        userMoneyFlowBuy.setRemarek("全额购买");
        userMoneyFlowBuy.setCurrentMoney(orders.getUser().getMoney());
        userMoneyFlowBuy = userMoneyFlowRepository.saveAndFlush(userMoneyFlowBuy);
        log.info("订单主流程处理完毕!");
    }

}
