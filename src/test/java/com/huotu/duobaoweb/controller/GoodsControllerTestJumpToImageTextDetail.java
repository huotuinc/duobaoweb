package com.huotu.duobaoweb.controller;

import com.huotu.duobaoweb.base.BaseTest;
import com.huotu.duobaoweb.boot.RootConfig;
import com.huotu.duobaoweb.common.CommonEnum;
import com.huotu.duobaoweb.controller.page.ImageTextDetailPage;
import com.huotu.duobaoweb.entity.Goods;
import com.huotu.duobaoweb.repository.GoodsRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.expression.ParseException;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;


/**
 * Created by daisy.zhang on 2016/4/6.
 * 测试通过商品ID，获取图文详情
 */
@SuppressWarnings("SpringJavaAutowiringInspection")
@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {RootConfig.class})
@ActiveProfiles("development")
@Transactional
public class GoodsControllerTestJumpToImageTextDetail extends BaseTest {

    @Autowired
    private GoodsRepository mockGoodsRep;

    private Goods mockGoods;


    @Before
    public void setUp() throws ParseException, java.text.ParseException {

        //模拟出一个商品
        mockGoods = new Goods();
        mockGoods.setTitle("daisy测试商品");
        mockGoods.setDefaultPictureUrl("DefaultPicture.jpg");
        mockGoods.setPictureUrls("http://img.sootuu.com/vector/2006-4/200642494234558.jpg," +
                "http://v1.qzone.cc/avatar/201306/01/11/28/51a96a7b752f0943.jpg!200x200.jpg");
        mockGoods.setCharacters("商品特征是红色");
        mockGoods.setStepAmount(1L); //单次购买最低量
        mockGoods.setDefaultAmount(1L); //购买时缺省人次
        mockGoods.setToAmount(10L);//总需人数
        mockGoods.setPricePercentAmount(new BigDecimal(1L)); //购买每人次单价
        mockGoods.setStatus(CommonEnum.GoodsStatus.up); //商品状态
        mockGoods.setStartTime(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse("2016-01-27 00:00:00")); //活动开始时间
        mockGoods.setEndTime(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse("2016-05-27 00:00:00")); //活动截止时间
        mockGoods.setShareTitle("丹青测试商品的分享标题"); //分享标题
        mockGoods.setShareDescription("丹青测试商品的分享描述"); //分享描述
        mockGoods.setSharePictureUrl("http://XXXXX.jpg"); //分享图片地址
        mockGoods.setToMallGoodsId(123456L);
        mockGoods.setAttendAmount(0L); //购买次数
        mockGoods.setViewAmount(2L); //浏览器
        mockGoods.setMerchantId(3347L); //设置商城ID
        mockGoods = mockGoodsRep.saveAndFlush(mockGoods);
    }


    //模拟的商品数据中添加了两张图片，看两张图片是否显示正确
    @Rollback
    @Test
    public void TestImageText() {
        ImageTextDetailPage page = new ImageTextDetailPage();
        page.to(driver, mockGoods.getId());
    }

    //商品并未设置图片，应该返回一张默认图片
    @Rollback
    @Test
    public void TestGoodsDefImg() {
        mockGoods.setPictureUrls("");
        ImageTextDetailPage page = new ImageTextDetailPage();
        page.GoodsDefImg(driver, mockGoods.getId());
    }

    //商品并未设置图片，也未设置默认图片
    @Rollback
    @Test
    public void TestGoodsNoImg() {
        mockGoods.setPictureUrls("");
        mockGoods.setDefaultPictureUrl("");
        ImageTextDetailPage page = new ImageTextDetailPage();
        page.GoodsNoImg(driver, mockGoods.getId());
    }

    //goodsID不传,检查错误提示页面
    @Rollback
    @Test
    public void TestNoGoodsId() {
        ImageTextDetailPage page = new ImageTextDetailPage();
        page.noGoodsId(driver);
    }

    //goodsID格式错误，检查错误提示页面
    @Rollback
    @Test
    public void TestWrongGoodsId() {
        ImageTextDetailPage page = new ImageTextDetailPage();
        page.WrongGoodsId(driver, "abc");
    }

    //goodsID在数据库中找不到
    @Rollback
    @Test
    public void TestGoodsIdNotFind() {
        Long goodsId = 999999L;
        ImageTextDetailPage page = new ImageTextDetailPage();
        page.GoodsIdNotFind(driver, goodsId);
    }

    //商品状态已下架
    @Rollback
    @Test
    public void TestGoodsUncheck() {
        mockGoods.setStatus(CommonEnum.GoodsStatus.uncheck);
        ImageTextDetailPage page = new ImageTextDetailPage();
        page.GoodsUncheck(driver, mockGoods.getId());
    }

    //商品状态未审核
    @Rollback
    @Test
    public void TestGoodsDown() {
        mockGoods.setStatus(CommonEnum.GoodsStatus.down);
        ImageTextDetailPage page = new ImageTextDetailPage();
        page.GoodsDown(driver, mockGoods.getId());
    }


}
