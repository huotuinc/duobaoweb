package com.huotu.duobaoweb.controller.page;

import com.huotu.duobaoweb.entity.Delivery;
import com.huotu.duobaoweb.entity.Goods;
import com.huotu.duobaoweb.entity.Issue;
import com.huotu.duobaoweb.entity.User;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;

/**
 * Created by cosy on 2016/4/5.
 */
public class GetOneLotteryInfoPage {

    private Log log = LogFactory.getLog(GetOneLotteryInfoPage.class);

    public  void to(WebDriver driver, User currentUser, Issue currentIssue, Goods goods,Delivery delivery)
    {
        Long userId=currentUser.getId();
        Long issueId=currentIssue.getId();
        driver.get("http://localhost/personal/getOneLotteryInfo?userid="+userId+"issueId="+issueId);
        String stat=driver.findElement(By.className("tit_rem_big")).getText();
        String jiangp=driver.findElement(By.className("fl")).getText();
        List<WebElement> elements=driver.findElements(By.className("fl clear"));
        String dizhi=driver.findElement(By.className("fl tit_rem hui clear")).getText();
        String title=driver.findElement(By.xpath("html/body/div[1]/div[3]/div/div/div[1]/div/p[1]/a")).getText();
        Assert.assertEquals("奖品状态",stat);
        Assert.assertEquals("获得奖品",jiangp);
        Assert.assertEquals(4,elements.size());
        Assert.assertEquals(delivery.getDetails(),dizhi);
        Assert.assertEquals(goods.getTitle(),title);
    }

}
