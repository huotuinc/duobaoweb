package com.huotu.duobaoweb.controller.page;

import com.huotu.duobaoweb.entity.UserBuyFlow;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;

/**
 * Created by daisy.zhang on 2016/4/7.
 * 检查图文详情页面元素是否正确
 */
public class ImageTextDetailPage {
//    private Log log = LogFactory.getLog(GetMyRaiderNumbersPage.class);

    //模型中添加2张图片，检查是否有两张图
    public void to(WebDriver driver, Long goodsId) {
        driver.get("http://localhost:8080/goods/imageTextDetail?id=" + goodsId);
        String title = driver.getTitle();
        List<WebElement> elements = driver.findElements(By.tagName("img"));
        Assert.assertEquals("图文详情", title);
        Assert.assertEquals(2, elements.size());
    }

    //模型中有设置默认图片，但没有设置图片，查默认图片是否显示
    public void GoodsDefImg(WebDriver driver, Long goodsId) {
        driver.get("http://localhost:8080/goods/imageTextDetail?id=" + goodsId);
        String title = driver.getTitle();
        List<WebElement> elements = driver.findElements(By.tagName("img"));
        Assert.assertEquals("图文详情", title);
        Assert.assertEquals(1, elements.size());
    }

    //模型中没有任何图片，空页面显示，还是有空提示待定
    public void GoodsNoImg(WebDriver driver, Long goodsId) {
        driver.get("http://localhost:8080/goods/imageTextDetail?id=" + goodsId);
        String title = driver.getTitle();
        List<WebElement> elements = driver.findElements(By.tagName("img"));
        Assert.assertEquals("图文详情", title);
        Assert.assertEquals(0, elements.size());
    }

    //GoodID不传判断容错
    public void noGoodsId(WebDriver driver) {
        driver.get("http://localhost:8080/goods/imageTextDetail");
        String title = driver.getTitle();
        String message = driver.findElement(By.id("XXXX")).getText();
        Assert.assertEquals("错误页面", title);
        Assert.assertEquals("参数错误", message);
    }

    //GoodID格式错误，判断容错
    public void WrongGoodsId(WebDriver driver, String goodsId) {
        driver.get("http://localhost:8080/goods/imageTextDetail?id=" + goodsId);
        String title = driver.getTitle();
        String message = driver.findElement(By.id("XXXX")).getText();
        Assert.assertEquals("错误页面", title);
        Assert.assertEquals("参数错误", message);
    }

    //GoodId在数据库中不存在，判断容错
    public void GoodsIdNotFind(WebDriver driver, Long goodsId) {
        driver.get("http://localhost:8080/goods/imageTextDetail?id=" + goodsId);
        String title = driver.getTitle();
        String message = driver.findElement(By.id("XXXX")).getText();
        Assert.assertEquals("错误页面", title);
        Assert.assertEquals("商品不存在", message);
    }

    //商品状态还未审核，判断容错
    public void GoodsUncheck(WebDriver driver, Long goodsId) {
        driver.get("http://localhost:8080/goods/imageTextDetail?id=" + goodsId);
        String title = driver.getTitle();
        String message = driver.findElement(By.id("XXXX")).getText();
        Assert.assertEquals("错误页面", title);
        Assert.assertEquals("商品未上架", message);
    }

    //商品状态已下架，判断容错
    public void GoodsDown(WebDriver driver, Long goodsId) {
        driver.get("http://localhost:8080/goods/imageTextDetail?id=" + goodsId);
        String title = driver.getTitle();
        String message = driver.findElement(By.id("XXXX")).getText();
        Assert.assertEquals("错误页面", title);
        Assert.assertEquals("商品已下架", message);
    }
}
