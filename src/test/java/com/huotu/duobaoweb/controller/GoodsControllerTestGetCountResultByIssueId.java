package com.huotu.duobaoweb.controller;

import com.alibaba.fastjson.JSONObject;
import com.huotu.duobaoweb.base.BaseTest;
import com.huotu.duobaoweb.boot.MVCConfig;
import com.huotu.duobaoweb.boot.RootConfig;
import com.huotu.duobaoweb.common.CommonEnum;
import com.huotu.duobaoweb.controller.page.GetCountResultByIssueIdPage;
import com.huotu.duobaoweb.entity.CountResult;
import com.huotu.duobaoweb.entity.Goods;
import com.huotu.duobaoweb.entity.Issue;
import com.huotu.duobaoweb.entity.User;
import com.huotu.duobaoweb.entity.UserNumber;
import com.huotu.duobaoweb.repository.CountResultRepository;
import com.huotu.duobaoweb.repository.GoodsRepository;
import com.huotu.duobaoweb.repository.IssueRepository;
import com.huotu.duobaoweb.repository.UserNumberRepository;
import com.huotu.duobaoweb.repository.UserRepository;
import com.huotu.huobanplus.sdk.base.BaseClientSpringConfig;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

/**
 * Created by daisy.zhang on 2016/4/7.
 * 测试获取计算结果
 */
@SuppressWarnings("SpringJavaAutowiringInspection")
@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {RootConfig.class, MVCConfig.class, BaseClientSpringConfig.class})
@ActiveProfiles("development")
@Transactional
public class GoodsControllerTestGetCountResultByIssueId extends BaseTest {
    @Autowired
    IssueRepository mockIssueRep;
    @Autowired
    GoodsRepository mockGoodsRep;
    @Autowired
    CountResultRepository mockCRR;
    @Autowired
    UserNumberRepository userNumberRepository;
    @Autowired
    UserRepository userRepository;

    private Goods mockGoods;
    private Issue mockIssue;
    private CountResult mockCountRes;
    private User mockUserA;
    private User mockUserB;
    private UserNumber mockUserNumA;
    private UserNumber mockUserNumB;

    @Before
    public void setUp() throws IOException, ParseException {
        //模拟一期
        mockGoods = generateGoods(10L, false, mockGoodsRep, mockIssueRep);
        mockGoods.setTitle("daisy测试商品");
        mockGoods.setDefaultPictureUrl("DefaultPicture.jpg");
        mockGoods.setPictureUrls("DefaultPicture.jpg");
        mockGoods.setCharacters("商品特征是红色");
        mockGoods.setStepAmount(1L); //单次购买最低量
        mockGoods.setDefaultAmount(1L); //购买时缺省人次
        mockGoods.setToAmount(10L);//总需人数
        mockGoods.setPricePercentAmount(new BigDecimal(1L)); //购买每人次单价
        mockGoods.setStatus(CommonEnum.GoodsStatus.up); //商品状态
        mockGoods.setIssue(mockIssue); //当期期号
        mockGoods.setStartTime(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse("2016-01-27 00:00:00")); //活动开始时间
        mockGoods.setEndTime(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse("2016-05-27 00:00:00")); //活动截止时间
        mockGoods.setShareTitle("丹青测试商品的分享标题"); //分享标题
        mockGoods.setShareDescription("丹青测试商品的分享描述"); //分享描述
        mockGoods.setSharePictureUrl("http://XXXXX.jpg"); //分享图片地址
        mockGoods.setToMallGoodsId(123456L);
        mockGoods.setAttendAmount(1L); //购买次数
        mockGoods.setViewAmount(2L); //浏览器
        mockGoods.setMerchantId(3447L); //设置商城ID

        //模拟一个期号
        mockIssue = new Issue();
        mockIssue.setGoods(mockGoods);//所属活动商品
        mockIssue.setStepAmount(mockGoods.getStepAmount());//单次购买最低量
        mockIssue.setDefaultAmount(mockGoods.getDefaultAmount()); //缺省购买人次
        mockIssue.setToAmount(mockGoods.getToAmount()); //总需购买人次
        mockIssue.setBuyAmount(10L); //已购买的人次
        mockIssue.setPricePercentAmount(mockGoods.getPricePercentAmount()); //每人次单价
        mockIssue.setAttendAmount(mockGoods.getAttendAmount()); //购买次数,在中奖时从每期中累计此值
        mockIssue.setStatus(CommonEnum.IssueStatus.drawed);//状态
        mockIssue.setAwardingDate(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse("2016-04-28 05:00:00"));//开奖日期
        mockIssue.setLuckyNumber(123456L);
        mockIssue = mockIssueRep.saveAndFlush(mockIssue);

        //模拟用户
        mockUserA = generateUserWithOpenId("123456", "77777777", userRepository);
        mockUserA.setUsername("daisyA");
        mockUserB = generateUserWithOpenId("56789", "88888888", userRepository);
        mockUserB.setUsername("daisyB");

        //模拟两个userNum并添加到list里面
        mockUserNumA = saveUserNumber(mockUserA, mockIssue, 2);
        mockUserNumB = saveUserNumber(mockUserB, mockIssue, 2);

        List<UserNumber> mockUserNumList = new ArrayList<>();
        mockUserNumList.add(mockUserNumA);
        mockUserNumList.add(mockUserNumB);

        //模拟开奖结果
        mockCountRes = new CountResult();
        mockCountRes.setIssueNo("20160812");
        mockCountRes.setNumberA(1234567L);
        mockCountRes.setNumberB(81256L);
        mockCountRes.setUserNumbers(mockUserNumList);
        mockCountRes.setIssueAmount(mockIssue.getToAmount().intValue());
        mockCountRes = mockCRR.saveAndFlush(mockCountRes);

    }

    //查看返回计算结果模型是否正确
    @Test
    public void testJXJG() throws Exception {
        GetCountResultByIssueIdPage page = new GetCountResultByIssueIdPage();
        page.to(driver, mockIssue, mockCountRes);
    }

}
