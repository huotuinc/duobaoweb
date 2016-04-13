/*
 *  * 版权所有:杭州火图科技有限公司
 *  * 地址:浙江省杭州市滨江区西兴街道阡陌路智慧E谷B幢4楼在地图中查看
 *  *
 *  * (c) Copyright Hangzhou Hot Technology Co., Ltd.
 *  * Floor 4,Block B,Wisdom E Valley,Qianmo Road,Binjiang District
 *  * 2013-2015. All rights reserved.
 */

package com.huotu.mallduobao.controller.page;

import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import java.math.BigDecimal;

/**
 * Created by daisy.zhang on 2016/4/7.
 * 活动首页元素检查
 */
public class JumpToGoodsActivityIndexPage {

    /**
     * 检查活动中时各页面元素
     *
     * @param costPrice    商品原价
     * @param currentPrice 商品现价
     * @param attendAmount 商品现价
     */
    public void to(WebDriver driver, Long goodsId, BigDecimal costPrice, BigDecimal currentPrice, Long attendAmount) {
        driver.get("http://localhost:8080/goods/index?id=" + goodsId);
        //判断标题是否正确
        Assert.assertEquals("用1元买iphone 6S", driver.getTitle());
        //判断图片是否存在
        Assert.assertNotNull(driver.findElement(By.xpath("/html/body/div/div[1]/img")));
        //判断倒计时区是否存在
        Assert.assertNotNull(driver.findElement(By.xpath("/html/body/div/div[1]/div/div/p")));
        //判断原价是否正确
        Assert.assertEquals(costPrice.toString(), driver.findElement(By.xpath("/html/body/div/p[1]/span[2]/b")).getText());
        //判断现价是否正确
        Assert.assertEquals(currentPrice.toString(), driver.findElement(By.xpath("/html/body/div/p[1]/text()")));
        //判断商品详情入口是否存在
        Assert.assertNotNull("商品详情", driver.findElement(By.xpath("/html/body/div/p[1]/a/span")).getText());
        //判断已参与人数
        Assert.assertEquals(attendAmount.toString(), driver.findElement(By.xpath("/html/body/div/p[3]/span")).getText());
        //判断继续购买按钮是否存在
        Assert.assertEquals("继续购买", driver.findElement(By.xpath("/html/body/div/div[2]/button")).getText());
        //判断继续购买按钮是否存在
        Assert.assertEquals("参与1元夺宝", driver.findElement(By.xpath("/html/body/div/div[3]/button")).getText());
        //判断分享提示按钮是否存在
        Assert.assertEquals("分享/收藏活动", driver.findElement(By.xpath("/html/body/div/div[4]/button")).getText());
        //判断分享按钮是否存在
        Assert.assertEquals("查看我的参与", driver.findElement(By.xpath("/html/body/div/div[5]/button")).getText());
        //判断活动提示是否存在
        Assert.assertEquals("活动提示", driver.findElement(By.xpath("/html/body/div/div[6]/div/span")).getText());
        Assert.assertEquals("⑴.确保你能查看中奖信息，请务必收藏本活动页面以及关注公众号。\n" +
                "⑵.夺宝所有商品均从正规渠道采购，100%正品，可享受厂家提供的全国联保服务。\n" +
                "⑶.遇到售后问题可通过公众号联系客户。\n" +
                "⑷.商家保留法律范围内允许的对活动的解释权。", driver.findElement(By.xpath("/html/body/div/div[6]/p")).getText());

    }

    //GoodsID不传判断容错
    public void NoGoodsID(WebDriver driver) {
        driver.get("http://localhost:8080/goods/index?id");
        Assert.assertEquals("错误页面", driver.getTitle());
        Assert.assertEquals("参数错误", driver.findElement(By.xpath("XXXXX")));
    }

    //GoodsID格式不正确不传判断容错
    public void WrongGoodsID(WebDriver driver, String goodsId) {
        driver.get("http://localhost:8080/goods/index?id=" + goodsId);
        Assert.assertEquals("错误页面", driver.getTitle());
        Assert.assertEquals("参数错误", driver.findElement(By.xpath("XXXXX")));
    }

    //GoodsID在数据库中搜索不到不传判断容错
    public void NotFindGoodsID(WebDriver driver, long goodsId) {
        driver.get("http://localhost:8080/goods/index?id=" + goodsId);
        Assert.assertEquals("错误页面", driver.getTitle());
        Assert.assertEquals("商品不存在", driver.findElement(By.xpath("XXXXX")));
    }

    //GoodsID状态未审核传判断容错
    public void GoodUnCheck(WebDriver driver, long goodsId) {
        driver.get("http://localhost:8080/goods/index?id=" + goodsId);
        Assert.assertEquals("错误页面", driver.getTitle());
        Assert.assertEquals("商品不存在", driver.findElement(By.xpath("XXXXX")));
    }

    //商品状态为已下架，判断容错
    public void GoodsDown(WebDriver driver, long goodsId) {
        driver.get("http://localhost:8080/goods/index?id=" + goodsId);
        Assert.assertEquals("错误页面", driver.getTitle());
        Assert.assertEquals("商品不存在", driver.findElement(By.xpath("XXXXX")));
    }

    //商品活动已过期，判断容错
    public void Expired(WebDriver driver, long goodsId) {
        driver.get("http://localhost:8080/goods/index?id=" + goodsId);
        Assert.assertEquals("错误页面", driver.getTitle());
        Assert.assertEquals("商品活动已结束", driver.findElement(By.xpath("XXXXX")));
    }

    //商品活动还未开始，判断容错
    public void NotStart(WebDriver driver, long goodsId) {
        driver.get("http://localhost:8080/goods/index?id=" + goodsId);
        Assert.assertEquals("错误页面", driver.getTitle());
        Assert.assertEquals("商品不存在", driver.findElement(By.xpath("XXXXX")));
    }


}
