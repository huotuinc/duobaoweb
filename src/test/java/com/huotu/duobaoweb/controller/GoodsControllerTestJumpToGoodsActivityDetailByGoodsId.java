package com.huotu.duobaoweb.controller;

import com.huotu.duobaoweb.common.CommonEnum;
import com.huotu.duobaoweb.entity.Goods;
import com.huotu.duobaoweb.entity.Issue;
import com.huotu.duobaoweb.entity.User;
import com.huotu.duobaoweb.repository.GoodsRepository;
import com.huotu.duobaoweb.repository.IssueRepository;
import com.huotu.duobaoweb.repository.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by daisy.zhang on 2016/4/6.
 */
@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@ActiveProfiles("development")
@Transactional
public class GoodsControllerTestJumpToGoodsActivityDetailByGoodsId {
    @Autowired
    MockMvc mockMvc;
    @Autowired
    private GoodsRepository mockGoodsRep;
    @Autowired
    private IssueRepository mockIssueRep;
    @Autowired
    private UserRepository mockUserRep;

    private Issue mockIssue;
    private Goods mockGoods;
    private User mockUser;

    @Before
    public void init() throws ParseException {
        //模拟一个期号
        mockIssue = new Issue();
        mockIssue.setGoods(mockGoods);//所属活动商品
        mockIssue.setStepAmount(mockGoods.getStepAmount());//单次购买最低量
        mockIssue.setDefaultAmount(mockGoods.getDefaultAmount()); //缺省购买人次
        mockIssue.setToAmount(mockGoods.getToAmount()); //总需购买人次
        mockIssue.setBuyAmount(2L); //已购买的人次
        mockIssue.setPricePercentAmount(mockGoods.getPricePercentAmount()); //每人次单价
        mockIssue.setAttendAmount(mockGoods.getAttendAmount()); //购买次数,在中奖时从每期中累计此值
        mockIssue.setStatus(CommonEnum.IssueStatus.going);//状态
        mockIssue.setAwardingDate(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse("2016-04-28 05:00:00"));//开奖日期
        mockIssue = mockIssueRep.save(mockIssue);

        //模拟出一个商品
        mockGoods = new Goods();
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
        mockGoods.setAttendAmount(0L); //购买次数
        mockGoods.setViewAmount(2L); //浏览器
        mockGoods.setMerchantId(3347L); //设置商城ID
        mockGoods = mockGoodsRep.save(mockGoods);
    }

    //商品ID不传,判断容错（错误码还未定义，定义后统一修改）
    @Test
    public void TestNOGoodsID() throws Exception {
        mockMvc.perform(get("/detailByGoodsId").param("goodsId", ""))
                .andExpect(status().isOk())
                .andExpect((jsonPath("$.resultData.Code").value(1001)))
                .andExpect((jsonPath("$.resultData.resultDescription").value("参数错误")));
    }

    //商品ID错误,判断容错（错误码还未定义，定义后统一修改）
    @Test
    public void TestWrongGoodsID() throws Exception {
        mockMvc.perform(get("/detailByGoodsId").param("goodsId", ""))
                .andExpect(status().isOk())
                .andExpect((jsonPath("$.resultData.Code").value(1001)))
                .andExpect((jsonPath("$.resultData.resultDescription").value("参数错误")));
    }

    //商品ID在数据库中不存在,判断容错（错误码还未定义，定义后统一修改）
    @Test
    public void TestNotFindGoodsID() throws Exception {
        mockMvc.perform(get("/detailByGoodsId").param("goodsId", "999999999"))
                .andExpect(status().isOk())
                .andExpect((jsonPath("$.resultData.Code").value(1002)))
                .andExpect((jsonPath("$.resultData.resultDescription").value("商品未找到")));
    }

    //商品状态未审核,判断容错（错误码还未定义，定义后统一修改）
    @Test
    public void TestGoodUnCheck() throws Exception {
        mockGoods.setStatus(CommonEnum.GoodsStatus.uncheck);
        mockMvc.perform(get("/detailByGoodsId").param("goodsId", mockGoods.getId().toString()))
                .andExpect(status().isOk())
                .andExpect((jsonPath("$.resultData.Code").value(10009)))
                .andExpect((jsonPath("$.resultData.Msg").value("商品未审核")));

    }

    //商品已经过期,判断容错（错误码还未定义，定义后统一修改）
    @Test
    public void TestExpired() throws Exception {
        mockGoods.setStatus(CommonEnum.GoodsStatus.up);
        mockGoods.setEndTime(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse("2016-03-27 00:00:00"));
        mockMvc.perform(get("/detailByGoodsId").param("goodsId", mockGoods.getId().toString()))
                .andExpect(status().isOk())
                .andExpect((jsonPath("$.resultData.Code").value(10011)))
                .andExpect((jsonPath("$.resultData.Msg").value("商品已过期")));
    }

    //商品还未开始活动,判断容错（错误码还未定义，定义后统一修改）
    @Test
    public void TestNotStart() throws Exception {
        mockGoods.setStatus(CommonEnum.GoodsStatus.up);
        mockGoods.setStartTime(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse("2018-03-27 00:00:00"));
        mockGoods.setEndTime(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse("2019-03-27 00:00:00"));
        mockMvc.perform(get("/detailByGoodsId").param("goodsId", mockGoods.getId().toString()))
                .andExpect(status().isOk())
                .andExpect((jsonPath("$.resultData.Code").value(10012)))
                .andExpect((jsonPath("$.resultData.Msg").value("商品活动还未开始")));
    }

    //商品活动开始，但还未有任何人参与，判断接口返回模型数据是否正确
    @Test
    public void TestNoJoined() throws Exception {
        mockGoods.setStatus(CommonEnum.GoodsStatus.up);
        mockGoods.setStartTime(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse("2016-03-27 00:00:00"));
        mockGoods.setEndTime(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse("2019-03-27 00:00:00"));
        mockIssue.setBuyAmount(0L);
        mockMvc.perform(get("/detailByGoodsId").param("goodsId", mockGoods.getId().toString()))
                .andExpect(status().isOk())
                .andExpect((jsonPath("$.resultData.Code").value(10012)))
                .andExpect((jsonPath("$.resultData.data.id").value(mockGoods.getId())))
                .andExpect((jsonPath("$.resultData.data.issueId").value(mockGoods.getIssue())))
                .andExpect((jsonPath("$.resultData.data.pictureUrls").isNotEmpty()))
                .andExpect((jsonPath("$.resultData.data.status").value(0)))
                .andExpect((jsonPath("$.resultData.data.title").value(mockGoods.getTitle())))
                .andExpect((jsonPath("$.resultData.data.progress").value(mockIssue.getBuyAmount() / mockGoods.getToAmount())))
                .andExpect((jsonPath("$.resultData.data.toAmount").value(mockIssue.getToAmount())))
                .andExpect((jsonPath("$.resultData.data.remainAmount").value(mockIssue.getToAmount() - mockIssue.getBuyAmount())))
                .andExpect((jsonPath("$.resultData.data.defaultAmount").value(mockIssue.getDefaultAmount())))
                .andExpect((jsonPath("$.resultData.data.stepAmount").value(mockIssue.getStepAmount())))
                .andExpect((jsonPath("$.resultData.data.joinCount").value(mockIssue.getBuyAmount())))
                .andExpect((jsonPath("$.resultData.data.numbers").isEmpty()))
                .andExpect((jsonPath("$.resultData.data.fullPrice").value(mockIssue.getPricePercentAmount().multiply(new BigDecimal(mockIssue.getToAmount())))))
                .andExpect((jsonPath("$.resultData.data.toAwardTime").isNotEmpty()))
                .andExpect((jsonPath("$.resultData.data.awardUserName").isEmpty()))
                .andExpect((jsonPath("$.resultData.data.awardUserCityName").isEmpty()))
                .andExpect((jsonPath("$.resultData.data.awardUserIp").isEmpty()))
                .andExpect((jsonPath("$.resultData.data.awardUserJoinCount").isEmpty()))
                .andExpect((jsonPath("$.resultData.data.awardTime").value(mockIssue.getAwardingDate())))
                .andExpect((jsonPath("$.resultData.data.luckNumber").isEmpty()))
                .andExpect((jsonPath("$.resultData.data.awardUserHead").isEmpty()))
                .andExpect((jsonPath("$.resultData.data.firstBuyTime").isEmpty()));
    }

    //商品活动有用户参与过，查看模型返回数据是否正确
    @Test
    public void TestUserJoined() {

    }

    //商品状态等待开奖
    //商品状态已开奖


}
