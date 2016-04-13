package com.huotu.duobaoweb.controller.page;

import com.huotu.mallduobao.entity.Issue;
import com.huotu.mallduobao.entity.ShoppingCart;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * Created by cosy on 2016/4/6.
 *
 */
public class GetBalancePage {
    private Log log = LogFactory.getLog(GetBalancePage.class);

    public void to(WebDriver driver, ShoppingCart shoppingCart)
    {
        driver.get("http://localhost/web/balance?cartId="+ shoppingCart.getId() +"buyNum="+shoppingCart.getBuyAmount());
        String total=driver.findElement(By.xpath("html/body/div[1]/div[1]/span[2]/b")).getText();
        String title= driver.findElement(By.xpath("html/body/div[1]/div[2]/p/span[1]")).getText();
        String weixin=driver.findElement(By.xpath("html/body/div[1]/div[4]/label/div[2]/div")).getText();
        String alpay=driver.findElement(By.xpath("html/body/div[1]/div[5]/label/div[2]/div")).getText();

        Assert.assertEquals("10",total);
        Assert.assertEquals("cosytest",title);
        Assert.assertEquals("微信支付",weixin);
        Assert.assertEquals("支付宝",alpay);
        if(driver.findElement(By.xpath("html/body/div[1]/div[4]/label/div[2]/div")).isSelected())
        {
            System.out.print("success");
        }
    }

}
