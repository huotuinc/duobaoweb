/*
 * 版权所有:杭州火图科技有限公司
 * 地址:浙江省杭州市滨江区西兴街道阡陌路智慧E谷B幢4楼在地图中查看
 *
 * (c) Copyright Hangzhou Hot Technology Co., Ltd.
 * Floor 4,Block B,Wisdom E Valley,Qianmo Road,Binjiang District
 * 2013-2015. All rights reserved.
 */
package com.huotu.mallduobao.controller;

import com.alibaba.fastjson.JSONObject;
import com.huotu.huobanplus.sdk.common.repository.GoodsRestRepository;
import com.huotu.mallduobao.base.BaseTest;
import com.huotu.mallduobao.boot.MVCConfig;
import com.huotu.mallduobao.boot.RootConfig;
import com.huotu.mallduobao.entity.Goods;
import com.huotu.mallduobao.entity.Issue;
import com.huotu.mallduobao.repository.GoodsRepository;
import com.huotu.mallduobao.repository.IssueRepository;
import com.huotu.mallduobao.service.CommonConfigService;
import com.huotu.mallduobao.utils.CommonEnum;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;


/**
 * Created by daisy.zhang on 2016/4/15.
 * 测试创建期号
 */
@SuppressWarnings("SpringJavaAutowiringInspection")
@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {RootConfig.class, MVCConfig.class})
@ActiveProfiles("development")
@Transactional
public class ApiControllerTest extends BaseTest {
    @Autowired
    private CommonConfigService commonConfigService;
    @Autowired
    private GoodsRepository goodsRepository;
    @Autowired
    private IssueRepository issueRepository;
    @Autowired
    private GoodsRestRepository goodsRestRepository;


    private Goods mockGoods;
    private Issue mockIssue;
    private com.huotu.huobanplus.common.entity.Goods mallGoods;

    @Before
    public void setUp() throws Exception {
        //模拟一个商品
        mockGoods = daisyMockGoods();

    }

    //商城商品有库存，之前从未生成过期号，商品期号成功创建，并且
    @Test
    public void testCreate() throws Exception {
        //设置模拟商品与商城商品绑定
        mockGoods.setToMallGoodsId(17288L);
        goodsRepository.saveAndFlush(mockGoods);

        //当前库中最新的期号
        String sign = DigestUtils.md5DigestAsHex((mockGoods.getId().toString() +
                commonConfigService.getDuobaoApiKey()).getBytes());
        MvcResult result = mockMvc.perform(get("/api/generateIssue")
                .param("goodsId", mockGoods.getId().toString()).param("sign", sign))
                .andDo(print())
                .andReturn();
        String content = new String(result.getResponse().getContentAsByteArray(), "UTF-8");
        JSONObject obj = JSONObject.parseObject(content);
        Assert.assertEquals("成功", obj.get("message"));
        Assert.assertEquals("1", obj.get("code"));
        Assert.assertEquals("并没有创建新的期号", 1, issueRepository.findAll().size());
        Assert.assertNotNull("当前商品的期号没有被更新", mockGoods.getIssue());

    }

    //商城商品库存有，但是夺宝商品之前已经产生了期号
    @Test
    public void testCreateHaveIssue() throws Exception {
        //设置模拟商品与商城商品绑定
        mockGoods.setToMallGoodsId(17288L);
        //模拟一期商品
        mockIssue = daisyMockIssue(mockGoods);
        mockGoods.setIssue(mockIssue);
        goodsRepository.saveAndFlush(mockGoods);

        //当前库中最新的期号
        Long issueId = mockIssue.getId();
        String sign = DigestUtils.md5DigestAsHex((mockGoods.getId().toString() +
                commonConfigService.getDuobaoApiKey()).getBytes());
        MvcResult result = mockMvc.perform(get("/api/generateIssue")
                .param("goodsId", mockGoods.getId().toString()).param("sign", sign))
                .andDo(print())
                .andReturn();
        String content = new String(result.getResponse().getContentAsByteArray(), "UTF-8");
        JSONObject obj = JSONObject.parseObject(content);
        Assert.assertEquals("成功", obj.get("message"));
        Assert.assertEquals("1", obj.get("code"));
        //获取数据库中最新期号
        Long newIssueID = findMaxIssueId(issueRepository.findAllIssueByGoodsId(mockGoods.getId()));
        System.out.println("test---->" + issueId + "    " + newIssueID);
        Assert.assertNotEquals("并没有创建新的期号", issueId, newIssueID);
        Assert.assertEquals("之前期的状态变没有为待开奖", CommonEnum.IssueStatus.drawing, issueRepository.findOne(issueId).getStatus());
        Assert.assertEquals("没有更新商品的最新期号", newIssueID, mockGoods.getIssue().getId());
        Assert.assertEquals("当前期状态不为进行中", CommonEnum.IssueStatus.going, issueRepository.findOne(newIssueID).getStatus());


    }

    //    商城商品库存为1，测试临界值的情况
    @Test
    public void testCreateOne() throws Exception {
        //获取商城商品，并修改库存为1
        mallGoods = goodsRestRepository.getOneByPK(17275L);
        mallGoods.setStock(1);
        //设置模拟商品与商城商品绑定
        mockGoods.setToMallGoodsId(mallGoods.getId());
        goodsRepository.saveAndFlush(mockGoods);

        //当前库中最新的期号
        String sign = DigestUtils.md5DigestAsHex((mockGoods.getId().toString() +
                commonConfigService.getDuobaoApiKey()).getBytes());
        MvcResult result = mockMvc.perform(get("/api/generateIssue")
                .param("goodsId", mockGoods.getId().toString()).param("sign", sign))
                .andDo(print())
                .andReturn();
        String content = new String(result.getResponse().getContentAsByteArray(), "UTF-8");
        JSONObject obj = JSONObject.parseObject(content);
        Assert.assertEquals("成功", obj.get("message"));
        Assert.assertEquals("1", obj.get("code"));
        Assert.assertEquals("并没有创建新的期号", 1, issueRepository.findAll().size());
        Assert.assertNotNull("当前商品的期号没有被更新", mockGoods.getIssue());

    }

    //商城商品库存为0，商品期号创建失败
    @Test
    public void testCreateFail() throws Exception {
        //获取商城商品，并修改库存为1
        mallGoods = goodsRestRepository.getOneByPK(17250L);
        mallGoods.setStock(0);
        mockGoods.setToMallGoodsId(mallGoods.getId());
        goodsRepository.saveAndFlush(mockGoods);
        String sign = DigestUtils.md5DigestAsHex((mockGoods.getId().toString() +
                commonConfigService.getDuobaoApiKey()).getBytes());
        MvcResult result = mockMvc.perform(get("/api/generateIssue").param("goodsId", mockGoods.getId().toString()).param("sign", sign))
                .andReturn();
        String content = new String(result.getResponse().getContentAsByteArray(), "UTF-8");
        JSONObject obj = JSONObject.parseObject(content);
        Assert.assertEquals("生成期号失败", obj.get("message"));
        Assert.assertEquals("1002", obj.get("code"));
    }

    //sign错误，则创建失败
    @Test
    public void testSignWrong() throws Exception {
        String sign = "999999";
        MvcResult result = mockMvc.perform(get("/api/generateIssue").param("goodsId", mockGoods.getId().toString()).param("sign", sign))
                .andDo(print())
                .andReturn();
        String content = new String(result.getResponse().getContentAsByteArray(), "UTF-8");
        JSONObject obj = JSONObject.parseObject(content);
        Assert.assertEquals("签名失败", obj.get("message"));
        Assert.assertEquals("1001", obj.get("code"));

    }


}
