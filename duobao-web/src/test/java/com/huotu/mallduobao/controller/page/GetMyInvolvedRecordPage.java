package com.huotu.mallduobao.controller.page;

import com.huotu.mallduobao.entity.*;
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
public class GetMyInvolvedRecordPage {
    private Log log = LogFactory.getLog(GetMyInvolvedRecordPage.class);


    public void to (WebDriver driver, User user,  Issue currentIssue)
    {
        String lastTime="0";
        driver.get("http://localhost/personal/getMyInvolvedRecord?userId="+user.getId()+"&type="+currentIssue.getStatus() +"&lastTime="+lastTime);

        driver.findElement(By.xpath("html/body/div[1]/div[1]/ul/li[1]/div/a")).click();
        List<WebElement> biaoti = driver.findElements(By.xpath("html/body/div[1]/div[1]/ul/li"));
        String title=driver.findElement(By.xpath("html/body/div[1]/div[2]/div/div[1]/div/p[1]/a")).getText();
        String qihao=driver.findElement(By.className("hui")).getText();
        String zongxu=driver.findElement(By.xpath("html/body/div[1]/div[2]/div/div[1]/div/div[3]/span[1]")).getText();
        String caiyu=driver.findElement(By.xpath("html/body/div[1]/div[2]/div/div[1]/div/div[4]/span[1]/i")).getText();


        Assert.assertEquals(currentIssue.getGoods().getTitle(),title);
        Assert.assertEquals("参与期号："+currentIssue.getId(),qihao);
        Assert.assertEquals("总需："+currentIssue.getGoods().getToAmount(),zongxu);
        Assert.assertEquals(currentIssue.getGoods().getAttendAmount(),caiyu);
        Assert.assertEquals(4,biaoti.size());

    }
}
