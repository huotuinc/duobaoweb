/*
 *  * 版权所有:杭州火图科技有限公司
 *  * 地址:浙江省杭州市滨江区西兴街道阡陌路智慧E谷B幢4楼在地图中查看
 *  *
 *  * (c) Copyright Hangzhou Hot Technology Co., Ltd.
 *  * Floor 4,Block B,Wisdom E Valley,Qianmo Road,Binjiang District
 *  * 2013-2015. All rights reserved.
 */

package com.huotu.duobaoweb.controller.page;

import com.huotu.duobaoweb.entity.CountResult;
import com.huotu.duobaoweb.entity.Issue;
import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * Created by daisy.zhang on 2016/4/8.
 * 计算详情页面元素检查
 */
public class GetCountResultByIssueIdPage {
    public void to(WebDriver driver, Issue issue, CountResult countRes) {
        driver.get("http://localhost:8080/goods/index?id=" + issue.getId());
        //判断页面标题是否正确
        Assert.assertEquals("计算结果", driver.getTitle());
        //判断文案显示
        Assert.assertEquals("[(数值A+数值B)÷商品所需人次]取余数+100000001",
                driver.findElement(By.xpath("/html/body/div/div[1]/p[2]")).getText());
        Assert.assertEquals("计算A", driver.findElement(By.xpath("/html/body/div/div[2]/p[1]")).getText());
        Assert.assertEquals("截止该商品开奖时间点前最后50条全站参与纪录",
                driver.findElement(By.xpath("/html/body/div/div[2]/p[2]")).getText());
        Assert.assertEquals(countRes.getNumberA().toString(),
                driver.findElement(By.xpath("/html/body/div/div[2]/p[2]")).getText());
        Assert.assertEquals("展开", driver.findElement(By.xpath("/html/body/div/div[2]/p[3]/a/span[2]")).getText());
        //判断展开后的图片小图标是否存在
        Assert.assertNotNull(driver.findElement(By.xpath("/html/body/div/div[2]/p[3]/a/span[1]/img")));
        //判断夺宝列表标题是否正确
        Assert.assertEquals("夺宝时间", driver.findElement(By.xpath("/html/body/div/div[3]/ul/li[1]/p[1]")));
        Assert.assertEquals("用户账号", driver.findElement(By.xpath("/html/body/div/div[3]/ul/li[1]/p[2]")));

        //判断夺宝列表第一条中奖内容，时间元素是否存在
        Assert.assertNotNull(driver.findElement(By.xpath("/html/body/div/div[3]/ul/li[2]/p[1]/text()")));
        //判断夺宝列表第一条中奖内容，用户中奖号码
        Assert.assertEquals("→" + countRes.getUserNumbers().get(0).getNumber(),
                driver.findElement(By.xpath("/html/body/div/div[3]/ul/li[2]/p[1]/i")));
        //判断夺宝列表第一条中奖内容，中奖用户账户名
        Assert.assertEquals(countRes.getUserNumbers().get(0).getUser().getUsername(),
                driver.findElement(By.xpath("/html/body/div/div[3]/ul/li[2]/p[2]")).getText());
        //判断夺宝列表第二条中奖内容，时间元素是否存在
        Assert.assertNotNull(driver.findElement(By.xpath("/html/body/div/div[3]/ul/li[3]/p[1]/text()")));
        //判断夺宝列表第二条中奖内容，用户中奖号码
        Assert.assertEquals("→" + countRes.getUserNumbers().get(1).getNumber(),
                driver.findElement(By.xpath("/html/body/div/div[3]/ul/li[3]/p[1]/i")));
        //判断夺宝列表第一条中奖内容，中奖用户账户名
        Assert.assertEquals(countRes.getUserNumbers().get(2).getUser().getUsername(),
                driver.findElement(By.xpath("/html/body/div/div[3]/ul/li[3]/p[2]")).getText());

        //数值B判断
        Assert.assertEquals("数值B", driver.findElement(By.xpath("/html/body/div/div[4]/p[1]")).getText());
        Assert.assertEquals("最近一期福利彩票“老时彩票”开奖结果", driver.findElement(By.xpath("/html/body/div/div[4]/p[2]")).getText());
        Assert.assertEquals("=" + countRes.getNumberB(), driver.findElement(By.xpath("/html/body/div/div[4]/p[3]/i")).getText());
        Assert.assertEquals("(第" + countRes.getNumberB() + "期)" + countRes.getNumberB(),
                driver.findElement(By.xpath("/html/body/div/div[4]/p[3]/text()")).getText());
        //检查开奖查询按钮是否存在
        Assert.assertEquals("开奖查询", driver.findElement(By.xpath("/html/body/div/div[4]/p[3]/a/i")).getText());

        //页面文字断言
        Assert.assertEquals("计算结果", driver.findElement(By.xpath("/html/body/div/div[5]/p[1]")).getText());
        //断言该期中奖号码
        Assert.assertEquals("幸运号码：", driver.findElement(By.xpath("/html/body/div/div[5]/p[2]/text()")).getText());
        Assert.assertEquals(issue.getLuckyNumber(), driver.findElement(By.xpath("/html/body/div/div[5]/p[2]/i")));

    }
}
