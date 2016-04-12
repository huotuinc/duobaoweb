/*
 *  * 版权所有:杭州火图科技有限公司
 *  * 地址:浙江省杭州市滨江区西兴街道阡陌路智慧E谷B幢4楼在地图中查看
 *  *
 *  * (c) Copyright Hangzhou Hot Technology Co., Ltd.
 *  * Floor 4,Block B,Wisdom E Valley,Qianmo Road,Binjiang District
 *  * 2013-2015. All rights reserved.
 */

package com.huotu.duobaoweb.controller.page;

import com.huotu.duobaoweb.entity.Goods;
import com.huotu.duobaoweb.entity.Issue;
import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import java.math.BigDecimal;

/**
 * Created by daisy.zhang on 2016/4/8.
 * 通过商品ID，跳转到商品详情页面，以下代码用于检查详情页面元素是否正确
 */


public class JumpToGoodsActivityDetailByGoodsId {

    //活动已经开始，但还没有任何人参与的页面显示
    public void NoJoined(WebDriver driver, Goods goods, Issue issue) {
        driver.get("http://localhost:8080/goods/detailByGoodsId?id=" + goods.getId());

        //设置当期购买人数为0，用于后面进度进行断言
        issue.setBuyAmount(0L);
        //判断页面标题是否正确
        Assert.assertEquals("奖品详情", driver.getTitle());
        //判断商品状态
        Assert.assertEquals("进行中", driver.findElement(By.xpath("/html/body/div[2]/div[1]/span[1]")).getText());
        //判断商品标题
        Assert.assertEquals(goods.getTitle(), driver.findElement(By.xpath("/html/body/div[2]/div[1]/span[3]/a")).getText());
        //判断期号是否正确
        Assert.assertEquals("参与期号：" + issue.getId(), driver.findElement(By.xpath("/html/body/div[2]/div[1]/div[1]/div/p")));
        //判断是否有进度条
        Assert.assertNotNull(driver.findElement(By.xpath("/html/body/div[2]/div[1]/div[1]/div/div/div")));
        //判断总需人数是否正确
        Assert.assertEquals(goods.getToAmount().toString(), driver.findElement(By.xpath("/html/body/div[2]/div[1]/div[2]/span[1]")).getText());
        //判断剩余人数是否正确
        Assert.assertEquals("剩余", driver.findElement(By.xpath("/html/body/div[2]/div[1]/div[2]/span[2]/text()")).getText());
        //剩余购买次数
        String remainAmount = String.valueOf(issue.getToAmount() - issue.getBuyAmount());
        Assert.assertEquals(remainAmount, driver.findElement(By.xpath("/html/body/div[2]/div[1]/div[2]/span[2]/text()")).getText());
        //判断无参与提示
        Assert.assertEquals("您还没有参与本次夺宝哦", driver.findElement(By.xpath("/html/body/div[2]/div[3]/p")).getText());
        //判断全价购买提示语
        Assert.assertEquals("全价购买", driver.findElement(By.xpath("/html/body/div[2]/div[4]/div/div[1]/p[1]")).getText());
        Assert.assertEquals("无需等待，直接获得商品！", driver.findElement(By.xpath("/html/body/div[2]/div[4]/div/div[1]/p[2]")).getText());
        //判断结算按钮是否存在
        Assert.assertEquals("结算", driver.findElement(By.xpath("/html/body/div[2]/div[4]/div/div[2]/a")).getText());
        //判断商品原价显示是否正确
        String costPrice = String.valueOf(issue.getPricePercentAmount().multiply(new BigDecimal(issue.getToAmount())));//原价 单价*总需
        Assert.assertEquals(costPrice, driver.findElement(By.xpath("/html/body/div[2]/div[4]/div/p[2]/b")).getText());
        //判断图文详情链接是否存在
        Assert.assertEquals("图文详情", driver.findElement(By.xpath("/html/body/div[2]/div[7]/ul/li[1]/a/span[1]")).getText());
        Assert.assertNotNull(driver.findElement(By.xpath("/html/body/div[2]/div[7]/ul/li[1]/a/span[2]/img")));
        Assert.assertEquals("建议在wifi下查看", driver.findElement(By.xpath("/html/body/div[2]/div[7]/ul/li[1]/span")).getText());
        //判断参与记录链接是否存在
        Assert.assertEquals("参与记录", driver.findElement(By.xpath("/html/body/div[2]/div[7]/ul/li[2]/a/span[1]")).getText());
        Assert.assertNotNull(driver.findElement(By.xpath("/html/body/div[2]/div[7]/ul/li[2]/a/span[2]/img")));
        //判断所有参与记录元素是否存在，以及参与记录应该为空
        Assert.assertEquals("所有参与纪录", driver.findElement(By.xpath("/html/body/div[2]/div[8]/p[1]/span[1]")).getText());
        Assert.assertNull(driver.findElement(By.xpath("/html/body/div[2]/div[8]/div/div[2]/div")));
    }

    //活动已经开始，有2个参与记录
    public void UserJoined(WebDriver driver, Goods goods, Issue issue) {
        driver.get("http://localhost:8080/goods/detailByGoodsId?id=" + goods.getId());

        //判断页面标题是否正确
        Assert.assertEquals("奖品详情", driver.getTitle());
        //判断商品状态
        Assert.assertEquals("进行中", driver.findElement(By.xpath("/html/body/div[2]/div[1]/span[1]")).getText());
        //判断商品标题
        Assert.assertEquals(goods.getTitle(), driver.findElement(By.xpath("/html/body/div[2]/div[1]/span[3]/a")).getText());
        //判断期号是否正确
        Assert.assertEquals("参与期号：" + issue.getId(), driver.findElement(By.xpath("/html/body/div[2]/div[1]/div[1]/div/p")));
        //判断是否有进度条
        Assert.assertNotNull(driver.findElement(By.xpath("/html/body/div[2]/div[1]/div[1]/div/div/div")));
        //判断总需人数是否正确
        Assert.assertEquals(goods.getToAmount().toString(), driver.findElement(By.xpath("/html/body/div[2]/div[1]/div[2]/span[1]")).getText());
        //判断剩余人数是否正确
        Assert.assertEquals("剩余", driver.findElement(By.xpath("/html/body/div[2]/div[1]/div[2]/span[2]/text()")).getText());
        //剩余购买次数
        String remainAmount = String.valueOf(issue.getToAmount() - issue.getBuyAmount());
        Assert.assertEquals(remainAmount, driver.findElement(By.xpath("/html/body/div[2]/div[1]/div[2]/span[2]/text()")).getText());
        //判断无参与提示
        Assert.assertEquals("您还没有参与本次夺宝哦", driver.findElement(By.xpath("/html/body/div[2]/div[3]/p")).getText());
        //判断全价购买提示语
        Assert.assertEquals("全价购买", driver.findElement(By.xpath("/html/body/div[2]/div[4]/div/div[1]/p[1]")).getText());
        Assert.assertEquals("无需等待，直接获得商品！", driver.findElement(By.xpath("/html/body/div[2]/div[4]/div/div[1]/p[2]")).getText());
        //判断结算按钮是否存在
        Assert.assertEquals("结算", driver.findElement(By.xpath("/html/body/div[2]/div[4]/div/div[2]/a")).getText());
        //判断商品原价显示是否正确
        String costPrice = String.valueOf(issue.getPricePercentAmount().multiply(new BigDecimal(issue.getToAmount())));//原价 单价*总需
        Assert.assertEquals(costPrice, driver.findElement(By.xpath("/html/body/div[2]/div[4]/div/p[2]/b")).getText());
        //判断图文详情链接是否存在
        Assert.assertEquals("图文详情", driver.findElement(By.xpath("/html/body/div[2]/div[7]/ul/li[1]/a/span[1]")).getText());
        Assert.assertNotNull(driver.findElement(By.xpath("/html/body/div[2]/div[7]/ul/li[1]/a/span[2]/img")));
        Assert.assertEquals("建议在wifi下查看", driver.findElement(By.xpath("/html/body/div[2]/div[7]/ul/li[1]/span")).getText());
        //判断参与记录链接是否存在
        Assert.assertEquals("参与记录", driver.findElement(By.xpath("/html/body/div[2]/div[7]/ul/li[2]/a/span[1]")).getText());
        Assert.assertNotNull(driver.findElement(By.xpath("/html/body/div[2]/div[7]/ul/li[2]/a/span[2]/img")));
        //判断所有参与记录元素是否存在，以及参与记录应该为空
        Assert.assertEquals("所有参与纪录", driver.findElement(By.xpath("/html/body/div[2]/div[8]/p[1]/span[1]")).getText());
        //判断首页购买时间是否存在
        Assert.assertNotNull(driver.findElement(By.xpath("/html/body/div[2]/div[8]/p[1]/span[2]")));
        //判断最新购买日期是否存在
        Assert.assertNotNull(driver.findElement(By.xpath("/html/body/div[2]/div[8]/div/div[1]/p")));
        //判断是否有两条参与记录
        Assert.assertEquals(2, driver.findElements(By.xpath("/html/body/div[2]/div[8]/div/div[2]/div")).size());
    }

    //活动已进入待开奖状态
    public void IssueDrawing(WebDriver driver, Goods goods, Issue issue) {
        driver.get("http://localhost:8080/goods/detailByGoodsId?id=" + goods.getId());

        //判断页面标题是否正确
        Assert.assertEquals("奖品详情", driver.getTitle());
        //判断商品标题
        Assert.assertEquals(goods.getTitle(), driver.findElement(By.xpath("/html/body/div[2]/div[1]/span[3]/a")).getText());
        //判断期号是否正确
        Assert.assertEquals("参与期号：" + issue.getId(), driver.findElement(By.xpath("/html/body/div[2]/div[1]/div[1]/div/p")));
        //判断揭晓倒计时文案
        Assert.assertEquals("揭晓倒计时：", driver.findElement(By.xpath("/html/body/div[2]/div[5]/p[2]/span/text()")));
        //判断倒计时元素是否存在
        Assert.assertNotNull(driver.findElement(By.xpath("/html/body/div[2]/div[5]/p[2]/span/i")));
        //计算详情入口是否存在
        Assert.assertEquals("计算详情：", driver.findElement(By.xpath("/html/body/div[2]/div[5]/p[2]/a/span")));
        //判断图文详情链接是否存在
        Assert.assertEquals("图文详情", driver.findElement(By.xpath("/html/body/div[2]/div[7]/ul/li[1]/a/span[1]")).getText());
        Assert.assertNotNull(driver.findElement(By.xpath("/html/body/div[2]/div[7]/ul/li[1]/a/span[2]/img")));
        Assert.assertEquals("建议在wifi下查看", driver.findElement(By.xpath("/html/body/div[2]/div[7]/ul/li[1]/span")).getText());
        //判断参与记录链接是否存在
        Assert.assertEquals("参与记录", driver.findElement(By.xpath("/html/body/div[2]/div[7]/ul/li[2]/a/span[1]")).getText());
        Assert.assertNotNull(driver.findElement(By.xpath("/html/body/div[2]/div[7]/ul/li[2]/a/span[2]/img")));
        //判断所有参与记录元素是否存在，以及参与记录应该为空
        Assert.assertEquals("所有参与纪录", driver.findElement(By.xpath("/html/body/div[2]/div[8]/p[1]/span[1]")).getText());
        //判断首页购买时间是否存在
        Assert.assertNotNull(driver.findElement(By.xpath("/html/body/div[2]/div[8]/p[1]/span[2]")));
        //判断最新购买日期是否存在
        Assert.assertNotNull(driver.findElement(By.xpath("/html/body/div[2]/div[8]/div/div[1]/p")));
        //判断是否有两条参与记录
        Assert.assertEquals(2, driver.findElements(By.xpath("/html/body/div[2]/div[8]/div/div[2]/div")).size());
    }

    //活动已开奖状态
    public void IssueDrawed(WebDriver driver, Goods goods, Issue issue) {
        driver.get("http://localhost:8080/goods/detailByGoodsId?id=" + goods.getId());
        //判断页面标题是否正确
        Assert.assertEquals("奖品详情", driver.getTitle());
        //判断商品标题
        Assert.assertEquals(goods.getTitle(), driver.findElement(By.xpath("/html/body/div[2]/div[1]/span[3]/a")).getText());
        //判断期号是否正确
        Assert.assertEquals("参与期号：" + issue.getId(), driver.findElement(By.xpath("/html/body/div[2]/div[1]/div[1]/div/p")));
        //判断获奖者图片标签是否存在
        Assert.assertNotNull(driver.findElement(By.xpath("/html/body/div[2]/div[6]/p/img")));

        //判断获奖者名称是否正确
        Assert.assertEquals("获奖者：" + issue.getAwardingUser().getUsername(),
                driver.findElement(By.xpath("/html/body/div[2]/div[6]/div/div[1]/div[1]/div/p[1]/a")).getText());
        //判断中奖者Ip是否存在
        Assert.assertNotNull(driver.findElement(By.xpath("/html/body/div[2]/div[6]/div/div[1]/div[1]/div/div/div/p[1]")));
        Assert.assertEquals("本期参与：",
                driver.findElement(By.xpath("/html/body/div[2]/div[6]/div/div[1]/div[1]/div/div/div/p[3]/text()[1]")).getText());
        //判断参与人数是否正确
        Assert.assertEquals(issue.getBuyAmount().toString(),
                driver.findElement(By.xpath("/html/body/div[2]/div[6]/div/div[1]/div[1]/div/div/div/p[3]/i")));
        Assert.assertEquals("人次", driver.findElement(By.xpath("/html/body/div[2]/div[6]/div/div[1]/div[1]/div/div/div/p[3]/text()[2]")));
        //判断揭晓时间元素是否存在
        Assert.assertNotNull(driver.findElement(By.xpath("/html/body/div[2]/div[6]/div/div[1]/div[1]/div/div/div/p[4]")));
        //判断用户头像是否存在
        Assert.assertNotNull(driver.findElement(By.xpath("/html/body/div[2]/div[6]/div/div[1]/div[2]/img")));
        //判断幸运号码是否正确
        Assert.assertEquals("幸运号码：", driver.findElement(By.xpath("/html/body/div[2]/div[6]/div/div[2]/span/text()")));
        Assert.assertEquals(issue.getLuckyNumber().toString(),
                driver.findElement(By.xpath("/html/body/div[2]/div[6]/div/div[2]/span/i")).getText());
        //计算详情入口是否存在
        Assert.assertEquals("计算详情：", driver.findElement(By.xpath("/html/body/div[2]/div[5]/p[2]/a/span")));
        //判断图文详情链接是否存在
        Assert.assertEquals("图文详情", driver.findElement(By.xpath("/html/body/div[2]/div[7]/ul/li[1]/a/span[1]")).getText());
        Assert.assertNotNull(driver.findElement(By.xpath("/html/body/div[2]/div[7]/ul/li[1]/a/span[2]/img")));
        Assert.assertEquals("建议在wifi下查看", driver.findElement(By.xpath("/html/body/div[2]/div[7]/ul/li[1]/span")).getText());
        //判断参与记录链接是否存在
        Assert.assertEquals("参与记录", driver.findElement(By.xpath("/html/body/div[2]/div[7]/ul/li[2]/a/span[1]")).getText());
        Assert.assertNotNull(driver.findElement(By.xpath("/html/body/div[2]/div[7]/ul/li[2]/a/span[2]/img")));
        //判断所有参与记录元素是否存在，以及参与记录应该为空
        Assert.assertEquals("所有参与纪录", driver.findElement(By.xpath("/html/body/div[2]/div[8]/p[1]/span[1]")).getText());
        //判断首页购买时间是否存在
        Assert.assertNotNull(driver.findElement(By.xpath("/html/body/div[2]/div[8]/p[1]/span[2]")));
        //判断最新购买日期是否存在
        Assert.assertNotNull(driver.findElement(By.xpath("/html/body/div[2]/div[8]/div/div[1]/p")));
        //判断是否有两条参与记录
        Assert.assertEquals(2, driver.findElements(By.xpath("/html/body/div[2]/div[8]/div/div[2]/div")).size());
    }

    public void NOGoodsID(WebDriver driver) {
        driver.get("http://localhost:8080/goods/detailByGoodsId?id=");
        Assert.assertEquals("错误页面", driver.getTitle());
        Assert.assertEquals("参数错误", driver.findElement(By.xpath("XXXXX")));
    }

    public void WrongGoodsID(WebDriver driver, String goodsId) {
        driver.get("http://localhost:8080/goods/detailByGoodsId?id=" + goodsId);
        Assert.assertEquals("错误页面", driver.getTitle());
        Assert.assertEquals("参数错误", driver.findElement(By.xpath("XXXXX")));
    }

    public void NotFindGoodsID(WebDriver driver, Long goodsId) {
        driver.get("http://localhost:8080/goods/detailByGoodsId?id=" + goodsId);
        Assert.assertEquals("错误页面", driver.getTitle());
        Assert.assertEquals("商品不存在", driver.findElement(By.xpath("XXXXX")));
    }

    public void GoodsUnCheck(WebDriver driver, Long goodsId) {
        driver.get("http://localhost:8080/goods/detailByGoodsId?id=" + goodsId);
        Assert.assertEquals("错误页面", driver.getTitle());
        Assert.assertEquals("商品不存在", driver.findElement(By.xpath("XXXXX")));
    }

    public void GoodsDown(WebDriver driver, Long goodsId) {
        driver.get("http://localhost:8080/goods/detailByGoodsId?id=" + goodsId);
        Assert.assertEquals("错误页面", driver.getTitle());
        Assert.assertEquals("商品已下架", driver.findElement(By.xpath("XXXXX")));
    }

    public void Expired(WebDriver driver, Long goodsId) {
        driver.get("http://localhost:8080/goods/detailByGoodsId?id=" + goodsId);
        Assert.assertEquals("错误页面", driver.getTitle());
        Assert.assertEquals("商品活动已结束", driver.findElement(By.xpath("XXXXX")));
    }

    public void NotStart(WebDriver driver, Long goodsId) {
        driver.get("http://localhost:8080/goods/detailByGoodsId?id=" + goodsId);
        Assert.assertEquals("错误页面", driver.getTitle());
        Assert.assertEquals("商品活动还未开始", driver.findElement(By.xpath("XXXXX")));
    }


}
