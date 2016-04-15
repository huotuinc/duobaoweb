package com.huotu.mallduobao;

import com.alibaba.fastjson.JSONObject;
import com.huotu.huobanplus.common.entity.Merchant;
import com.huotu.huobanplus.sdk.common.repository.GoodsRestRepository;
import com.huotu.huobanplus.sdk.common.repository.MerchantRestRepository;
import com.huotu.mallduobao.base.BaseTest;
import com.huotu.mallduobao.boot.MVCConfig;
import com.huotu.mallduobao.boot.RootConfig;
import com.huotu.mallduobao.entity.Goods;
import com.huotu.mallduobao.entity.Issue;
import com.huotu.mallduobao.repository.GoodsRepository;
import com.huotu.mallduobao.repository.IssueRepository;
import com.huotu.mallduobao.service.CommonConfigService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

import static org.hamcrest.Matchers.greaterThan;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;


/**
 * Created by daisy.zhang on 2016/4/15.
 * 测试创建期号
 */
@SuppressWarnings("SpringJavaAutowiringInspection")
@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {RootConfig.class, MVCConfig.class})
@ActiveProfiles("development")
@Transactional
public class ApiControllerTest extends BaseTest{
    @Autowired
    private CommonConfigService commonConfigService;
    @Autowired
    private GoodsRepository goodsRepository;
    @Autowired
    private IssueRepository issueRepository;


    private Goods mockGoods;

    @Before
    public void setUp() throws Exception {
        //模拟一个商品
        mockGoods = daisyMockGoods();
        //模拟一期

    }
    //商城商品有库存，商品期号成功创建
    @Test
    public void testCreate() throws Exception {
        //设置模拟商品与商城商品绑定
        mockGoods.setToMallGoodsId(17153L);
        goodsRepository.saveAndFlush(mockGoods);

        //当前库中最新的期号
        if (issueRepository.findAll().size() == 0){
            String sign = DigestUtils.md5DigestAsHex((mockGoods.getId().toString() +
                    commonConfigService.getDuobaoApiKey()).getBytes());
            MvcResult result = mockMvc.perform(get("/api/generateIssue")
                    .param("goodsId",mockGoods.getId().toString()).param("sign",sign))
                    .andDo(print())
                    .andReturn();
            String content = new String(result.getResponse().getContentAsByteArray(), "UTF-8");
            JSONObject obj = JSONObject.parseObject(content);
            Assert.assertEquals("成功", obj.get("message"));
            Assert.assertEquals("1", obj.get("code"));
            Assert.assertEquals("并没有创建新的期号",1,issueRepository.findAll().size());
            Assert.assertNotNull("当前商品的期号没有被更新",mockGoods.getIssue());
        }else {

        }

    }
    //商城商品库存为0，商品期号创建失败
    @Test
    public void testCreateFail() throws Exception {
        mockGoods.setToMallGoodsId(17250L);
        goodsRepository.saveAndFlush(mockGoods);
        String sign = DigestUtils.md5DigestAsHex((mockGoods.getId().toString() +
                commonConfigService.getDuobaoApiKey()).getBytes());
        MvcResult result = mockMvc.perform(get("/api/generateIssue").param("goodsId",mockGoods.getId().toString()).param("sign",sign))
                .andReturn();
        String content = new String(result.getResponse().getContentAsByteArray(), "UTF-8");
        JSONObject obj = JSONObject.parseObject(content);
        Assert.assertEquals("失败", obj.get("message"));
        Assert.assertEquals(1001, obj.get("code"));
    }
    //sign错误，则创建失败
    @Test
    public void testSignWrong() throws Exception {
        String sign = "999999";
        MvcResult result = mockMvc.perform(get("/api/generateIssue").param("goodsId",mockGoods.getId().toString()).param("sign",sign))
                .andDo(print())
                .andReturn();
        String content = new String(result.getResponse().getContentAsByteArray(), "UTF-8");
        JSONObject obj = JSONObject.parseObject(content);
        Assert.assertEquals("失败", obj.get("message"));
        Assert.assertEquals(1001, obj.get("code"));

    }


}
