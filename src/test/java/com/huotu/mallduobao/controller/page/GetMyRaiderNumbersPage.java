package com.huotu.mallduobao.controller.page;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.openqa.selenium.By;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;


import java.util.List;


/**
 * Created by lgh on 2016/3/30.
 */


public class GetMyRaiderNumbersPage {
    private Log log = LogFactory.getLog(GetMyRaiderNumbersPage.class);


    public void to(WebDriver driver, Long userId, Long issueId) {
        driver.get("http://localhost/personal/getMyRaiderNumbers?userId=" + userId + "&issueId=" + issueId);
        PageFactory.initElements(driver, GetMyRaiderNumbersPage.class);
        List<WebElement> webElement = driver.findElements(By.cssSelector("div.commfont"));
        log.info(webElement.size());
    }


}
