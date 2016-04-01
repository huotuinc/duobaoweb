package com.huotu.duobaoweb.controller;
import com.huotu.duobaoweb.common.CommonEnum;
import com.huotu.duobaoweb.entity.Goods;
import com.huotu.duobaoweb.entity.Issue;
import com.huotu.duobaoweb.repository.GoodsRepository;
import com.huotu.duobaoweb.repository.IssueRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;


import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by daisy.zhang on 2016/3/30.
 */

@Transactional
@RunWith(SpringJUnit4ClassRunner.class)
public class GoodsControllerTestJumpToGoodsActivityIndex {
    @Autowired
    MockMvc mockMvc;
    @Autowired
    private GoodsRepository mockGoodsRep;
    @Autowired
    private IssueRepository mockIssueRep;

    private Issue mockIssue;
    private Goods mockGoods;
    @Before
    public void init() throws ParseException {
        //模拟一个期号
        mockIssue = new Issue();
        mockIssue.setGoods(mockGoods);//所属活动商品
        mockIssue.setStepAmount(mockGoods.getStepAmount());//单次购买最低量
        mockIssue.setDefaultAmount(mockGoods.getDefaultAmount()); //缺省购买人次
        mockIssue.setToAmount(mockGoods.getToAmount()); //总需购买人次
        mockIssue.setBuyAmount(0L);
        mockIssue.setPricePercentAmount(mockGoods.getPricePercentAmount());
        mockIssue.setAttendAmount(mockGoods.getAttendAmount());
        mockIssue.setStatus(CommonEnum.IssueStatus.going);
        mockIssue.setAwardingDate(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse("2016-01-28 05:00:00"));
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

    //Good不传，错误码和错误信息还未定义
    @Test
    public void TestNoGoodsID() throws Exception {
        mockMvc.perform(get("/index").param("goodsId",""))
                .andExpect(status().isOk())
                .andExpect((jsonPath("$.resultData.Code").value(1001)))
                .andExpect((jsonPath("$.resultData.resultDescription").value("参数错误")));
    }

    //测试商品ID在数据库中不存在，错误码和错误信息还未定义
    @Test
    public void TestNotFindGoodsID() throws Exception {
        mockMvc.perform(get("/index").param("goodsId","-1"))
                .andExpect(status().isOk())
                .andExpect((jsonPath("$.resultData.Code").value(1002)))
                .andExpect((jsonPath("$.resultData.resultDescription").value("商品未找到")));
    }


    //测试商品ID正确，活动处于进行中，用户未登陆状态
    @Test
    public void TestIndexShow() throws Exception {
        //原价
        BigDecimal costPrice= mockGoods.getPricePercentAmount().multiply(new BigDecimal(mockGoods.getToAmount()));
        //现价
        BigDecimal currentPrice= mockGoods.getPricePercentAmount().multiply(new BigDecimal(mockGoods.getStepAmount()));
        mockMvc.perform(get("/index").param("goodsId",mockGoods.getId().toString()))
                .andExpect(status().isOk())
                .andExpect((jsonPath("$.resultData.Code").value(1)))
                .andExpect((jsonPath("$.resultData.data.id").value(mockGoods.getId().toString())))
                .andExpect((jsonPath("$.resultData.data.defaultPictureUrl").value(mockGoods.getDefaultPictureUrl())))
                .andExpect((jsonPath("$.resultData.data.costPrice").value(costPrice)))
                .andExpect((jsonPath("$.resultData.data.currentPrice").value(currentPrice)))
                .andExpect((jsonPath("$.resultData.data.startTime").isNotEmpty()))
                .andExpect((jsonPath("$.resultData.data.endTime").isNotEmpty()))
                .andExpect((jsonPath("$.resultData.data.joinCount").value(false)))
                .andExpect((jsonPath("$.resultData.data.joined").value(false)))
                .andExpect((jsonPath("$.resultData.data.issueId").isEmpty()));

    }

}
