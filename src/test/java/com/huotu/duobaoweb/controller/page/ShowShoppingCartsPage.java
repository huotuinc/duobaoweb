package com.huotu.duobaoweb.controller.page;

import com.huotu.duobaoweb.entity.Goods;
import com.huotu.duobaoweb.entity.Issue;
import com.huotu.duobaoweb.entity.User;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;


/**
 * Created by cosy on 2016/4/6.
 *
 * 实现某用户购物车下有商品，无商品两种情况
 */


public class ShowShoppingCartsPage {
    private Log log = LogFactory.getLog(ShowShoppingCartsPage.class);

    //用户购物车下无商品
    public void nollGoods(WebDriver driver,User user)
    {
        driver.get("http://localhost/web/showShoppingCarts?userId="+user.getId());

        String zhifu=driver.findElement(By.className("tit_rem_big")).getText();
        Assert.assertEquals("支付总计：0元",zhifu);
        if (driver.findElement(By.className("zuijia tit_rem_big")).isEnabled()==false)
        {
             System.out.print("pass");
        }
    }

    //用户购物车下有商品
    public void existGoods(WebDriver driver, User currentUser, Issue currentIssue)
    {
        driver.get("http://localhost/web/showShoppingCarts?userId="+currentUser.getId());
        String title=driver.findElement(By.xpath(".//*[@id='show']/div[1]/div/p[1]/a")).getText();
        String zongxu=driver.findElement(By.xpath(".//*[@id='show']/div[1]/div/div[1]/span[1]")).getText();
        String shengyu=driver.findElement(By.xpath(".//*[@id='show']/div[1]/div/div[1]/span[2]/i")).getText();
        String zhifu=driver.findElement(By.className("tit_rem_big")).getText();
        String buttontext=driver.findElement(By.className("zuijia tit_rem_big")).getText();
        Assert.assertEquals("cosytest",title);
        Assert.assertEquals("总需："+currentIssue.getToAmount(),zongxu);
        Assert.assertEquals("90",shengyu);
        Assert.assertEquals("支付总计：10元",zhifu);
        if (driver.findElement(By.className("zuijia tit_rem_big")).isEnabled())
        {
            System.out.print("pass");
        }

    }
}
