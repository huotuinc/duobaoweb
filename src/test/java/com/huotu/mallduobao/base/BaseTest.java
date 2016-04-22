package com.huotu.mallduobao.base;

import com.huotu.mallduobao.utils.CommonEnum;
import com.huotu.mallduobao.controller.page.AbstractPage;
import com.huotu.mallduobao.entity.*;
import com.huotu.mallduobao.entity.Goods;
import com.huotu.mallduobao.entity.User;
import com.huotu.mallduobao.repository.*;
import com.huotu.mallduobao.service.CommonConfigService;
import com.huotu.mallduobao.service.RaidersCoreService;
import com.huotu.mallduobao.service.StaticResourceService;
import com.huotu.huobanplus.sdk.common.repository.GoodsRestRepository;
import org.junit.Before;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.htmlunit.webdriver.MockMvcHtmlUnitDriverBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.DigestUtils;
import org.springframework.web.context.WebApplicationContext;

import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.UUID;

/**
 * Created by lhx on 2016/3/25.
 */

public class BaseTest {


    @Autowired
    UserRepository userRepository;

    @Autowired
    StaticResourceService staticResourceService;

    @Autowired
    GoodsRepository goodsRepository;

    @Autowired
    IssueRepository issueRepository;

    @Autowired
    DeliveryRepository deliveryRepository;

    @Autowired
    UserBuyFlowRepository userBuyFlowRepository;

    @Autowired
    OrdersRepository ordersRepository;

    @Autowired
    OrdersItemRepository ordersItemRepository;

    @Autowired
    UserNumberRepository userNumberRepository;

    @Autowired
    RaidersCoreService raidersCoreService;

    @Autowired
    CommonConfigService commonConfigService;

    @Autowired
    GoodsRestRepository goodsRestRepository;

    protected WebDriver driver;

//    @Resource(name = "entityManagerFactory")
//    protected EntityManagerFactory entityManagerFactory;
//
//    @Resource(name = "transactionManager")
//    protected JpaTransactionManager transactionManager;


    @Autowired
    WebApplicationContext context;

    //    @Autowired
    protected MockMvc mockMvc;

    /**
     * 初始化webdriver
     */
    @Before
    public void init() throws UnsupportedEncodingException {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
        driver = MockMvcHtmlUnitDriverBuilder.mockMvcSetup(mockMvc).build();
    }

    /**
     * 初始化逻辑页面
     *
     * @param <T>   该页面相对应的逻辑页面
     * @param clazz 该页面相对应的逻辑页面的类
     * @return 页面实例
     */
    public <T extends AbstractPage> T initPage(Class<T> clazz) {
        T page = PageFactory.initElements(driver, clazz);
        page.validate();
        return page;
    }

    public User generateUser(@NotNull String password, UserRepository userRepository) throws UnsupportedEncodingException {
        return generateUserWithOpenId(password, UUID.randomUUID().toString(), userRepository);
    }


    public User generateUserWithOpenId(@NotNull String password, String openId, UserRepository userRepository) throws UnsupportedEncodingException {
        String userName = generateInexistentMobile(userRepository);
        User user = new User();
        user.setEnabled(true);
        user.setRegTime(new Date(System.currentTimeMillis() + new Random().nextInt(360 * 30 * 24 * 60 * 60)));
        user.setUsername(userName);
        user.setPassword(DigestUtils.md5DigestAsHex(password.getBytes("UTF-8")).toLowerCase());
        user.setUserHead(staticResourceService.USER_HEAD_PATH + "defaultH.jpg");
        user.setWeixinOpenId(openId);
        user.setWeixinBinded(true);
        user.setUserFromType(CommonEnum.UserFromType.register);
        user.setMoney(new BigDecimal("1000000"));
        user.setMerchantId(3447L);
        return userRepository.saveAndFlush(user);
    }

    /**
     * 返回一个不存在的手机号码
     *
     * @param userRepository
     * @return 手机号码
     */
    public String generateInexistentMobile(UserRepository userRepository) {
        while (true) {
            String number = "" + System.currentTimeMillis();
            number = number.substring(number.length() - 11);
            char[] data = number.toCharArray();
            data[0] = '1';
            number = new String(data);
            if (userRepository.count() == 0)
                return number;
            if (userRepository.countByMobile(number) == 0) {
                return number;
            }
        }
    }

    /**
     * 生成商品
     *
     * @param amount          人次量
     * @param goodsRepository
     * @param issueRepository
     * @return
     */
    public Goods generateGoods(Long amount, GoodsRepository goodsRepository, IssueRepository issueRepository) throws IOException {
        return generateGoods(amount, false, goodsRepository, issueRepository);
    }

    /**
     * 生成商品
     *
     * @param amount          人次量
     * @param userSimulated   是否启用模拟用户
     * @param goodsRepository
     * @param issueRepository
     * @return
     */
    public Goods generateGoods(Long amount, Boolean userSimulated, GoodsRepository goodsRepository, IssueRepository issueRepository) throws IOException {
        Goods goods = new Goods();
        goods.setStatus(CommonEnum.GoodsStatus.up);
        goods.setToAmount(amount);
        goods.setDefaultAmount(10L);
        goods.setPricePercentAmount(new BigDecimal(1));
        goods.setMerchantId(Long.parseLong(commonConfigService.getMallCustomerId()));

        com.huotu.huobanplus.common.entity.Goods mallGoods = generateMallGoods();
        goods.setToMallGoodsId(mallGoods.getId());

        goods = goodsRepository.saveAndFlush(goods);
        raidersCoreService.generateIssue(goods);


        return goods;
    }

    public com.huotu.huobanplus.common.entity.Goods generateMallGoods() throws IOException {
        com.huotu.huobanplus.common.entity.Goods goods = new com.huotu.huobanplus.common.entity.Goods();
        goods.setStock(100);
        goods.setCreateTime(new Date());
        goods.setScenes(4);
        goods.setDisabled(false);
        goods.setMarketPrice(100);
        goods.setPrice(100);
        return goodsRestRepository.insert(goods);
    }

    /***
     * 创建已购买的订单
     *
     * @param user
     * @param issue
     * @param amount
     * @param ordersRepository
     * @param ordersItemRepository
     * @param issueRepository
     * @return
     */
    public Orders generateOrdersWithPayed(User user, Issue issue, Long amount
            , OrdersRepository ordersRepository, OrdersItemRepository ordersItemRepository
            , IssueRepository issueRepository) throws IOException {
        Date date = new Date();

        BigDecimal money = issue.getPricePercentAmount().multiply(new BigDecimal(amount));

        Orders orders = new Orders();
        orders.setId(raidersCoreService.createOrderNo(date, user.getId()));
        orders.setTime(date);
        orders.setStatus(CommonEnum.OrderStatus.payed);
        orders.setUser(user);
        orders.setPayType(CommonEnum.PayType.remain);
        orders.setMoney(money);
        orders.setOrderType(CommonEnum.OrderType.raiders);
        orders.setPayTime(date);
        orders.setTotalMoney(money);
        orders = ordersRepository.saveAndFlush(orders);

        OrdersItem ordersItem = new OrdersItem();
        ordersItem.setStatus(CommonEnum.OrderStatus.payed);
        ordersItem.setAmount(amount);
        ordersItem.setIssue(issue);
        ordersItem.setOrder(orders);
        ordersItemRepository.saveAndFlush(ordersItem);

        issue.setBuyAmount(issue.getBuyAmount() + amount);
        issue = issueRepository.saveAndFlush(issue);
        //创建抽奖号码
        raidersCoreService.generateUserNumber(user, issue, amount, orders);

        if (issue.getBuyAmount() >= issue.getToAmount()) {
            raidersCoreService.generateIssue(issue.getGoods());
        }

        return orders;
    }


    /**
     * 添加个商品
     *
     * @return 返回添加后的商品
     */
    public Goods saveGodds() {
        Goods goods = new Goods();
        goods.setDefaultPictureUrl("/resources/images/dsds.jpg");
        goods.setPictureUrls("/resources/images/dsds.jpg,/resources/images/dsds.jpg,/resources/images/dsds.jpg");
        goods.setTitle("iphone6s");
        goods.setCharacters("独一无二");
        goods.setDefaultAmount(10L);
        goods.setStepAmount(10L);
        goods.setToAmount(5890L);
        goods.setPricePercentAmount(new BigDecimal(1L));
        goods.setStatus(CommonEnum.GoodsStatus.up);
        Date curDate = new Date();
        goods.setStartTime(curDate);
        goods.setEndTime(new Date(curDate.getTime() + 3600 * 24 * 1000));
        goods.setShareTitle("分享标题");
        goods.setShareDescription("分享描述");
        goods.setSharePictureUrl("/resources/images/dsds.jpg");
        goods.setMerchantId(3447L);
        goods.setToMallGoodsId(17066L);
        return goodsRepository.saveAndFlush(goods);
    }


    /**
     * 获取一期夺宝
     *
     * @param goods
     * @return
     */
    public Issue saveIssue(Goods goods, User user, int i) {
        Issue issue = new Issue();
        if (i % 2 == 0) {
            issue.setStatus(CommonEnum.IssueStatus.drawing);
        } else {
            if (i % 3 == 0) {
                issue.setStatus(CommonEnum.IssueStatus.drawed);
                issue.setAwardingUser(user);
                issue.setAwardingDate(new Date());
                issue.setLuckyNumber(20160121l + i);


            } else {
                issue.setStatus(CommonEnum.IssueStatus.going);
            }
        }
        issue.setPricePercentAmount(new BigDecimal(1));
        issue.setToAmount(20l);
        issue.setBuyAmount(12l);
        issue.setDefaultAmount(1l);
        issue.setGoods(goods);
        issue.setStepAmount(1L);
        issue = issueRepository.saveAndFlush(issue);
        if (i % 3 == 0) {
            Delivery delivery = new Delivery();
            delivery.setDeliveryStatus(CommonEnum.DeliveryStatus.GetPrize);
            delivery.setUser(user);
            delivery.setIssue(issue);
            delivery.setIsCommit(false);
            goods.setIssue(issue);
            goodsRepository.saveAndFlush(goods);
            deliveryRepository.saveAndFlush(delivery);
        }
        return issue;
    }

    /**
     * 添加用户中奖号码
     *
     * @param user
     * @param issue
     * @param i
     * @return
     */
    public UserNumber saveUserNumber(User user, Issue issue, int i) {
        UserNumber userNumber = new UserNumber();
        userNumber.setIssue(issue);
        userNumber.setUser(user);
        userNumber.setNumber(100031L + i);
        return userNumberRepository.saveAndFlush(userNumber);
    }

    /**
     * 添加订单
     *
     * @param user
     * @param i
     * @return
     */
    public Orders saveOrders(User user, int i) {
        Orders order = new Orders();
        order.setId("20160115080808485" + i);
        if (i % 2 == 0) {
            order.setPayType(CommonEnum.PayType.weixin);
        } else {
            order.setPayType(CommonEnum.PayType.alipay);
        }

        order.setStatus(CommonEnum.OrderStatus.payed);
        order.setMoney(BigDecimal.valueOf(10));
        order.setUser(user);
        order.setOrderType(CommonEnum.OrderType.raiders);
        order.setPayTime(new Date());
        return ordersRepository.saveAndFlush(order);
    }

    /**
     * 添加订单明细
     *
     * @param orders
     * @param issue
     * @return
     */
    public OrdersItem saveOrderItem(Orders orders, Issue issue) {
        OrdersItem oi = new OrdersItem();
        oi.setAmount(10L);
        oi.setOrder(orders);
        oi.setIssue(issue);
        oi.setStatus(CommonEnum.OrderStatus.paying);
        return ordersItemRepository.saveAndFlush(oi);
    }

    /**
     * 添加用户夺宝记录
     *
     * @param user
     * @param issue
     * @return
     */
    public UserBuyFlow saveUserBuyFlow(User user, Issue issue) {
        UserBuyFlow userBuyFlow = new UserBuyFlow();
        userBuyFlow.setTime(new Date().getTime());
        userBuyFlow.setUser(user);
        userBuyFlow.setIssue(issue);
        userBuyFlow.setAmount(1460701803441L);
        return userBuyFlowRepository.saveAndFlush(userBuyFlow);
    }

    //丹青测试专用，创建商品，不准修改，谢谢配合
    public Goods daisyMockGoods() throws Exception {
        Goods mockGoods = new Goods();
        //模拟出一个商品
        mockGoods.setTitle("daisy测试商品");
        mockGoods.setDefaultPictureUrl("Default.jpg");
        mockGoods.setPictureUrls("13.jpg,456.jpg");
        mockGoods.setCharacters("商品特征是红色");
        mockGoods.setStepAmount(1L); //单次购买最低量
        mockGoods.setDefaultAmount(1L); //购买时缺省人次
        mockGoods.setToAmount(20L);//总需人数
        mockGoods.setPricePercentAmount(new BigDecimal(1L)); //购买每人次单价
        mockGoods.setStatus(CommonEnum.GoodsStatus.up); //商品状态
        mockGoods.setStartTime(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse("2016-01-27 00:00:00")); //活动开始时间
        mockGoods.setEndTime(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse("2019-05-27 00:00:00")); //活动截止时间
        mockGoods.setShareTitle("丹青测试商品的分享标题"); //分享标题
        mockGoods.setShareDescription("丹青测试商品的分享描述"); //分享描述
        mockGoods.setSharePictureUrl("http://XXXXX.jpg"); //分享图片地址
        mockGoods.setToMallGoodsId(17153L);
        mockGoods.setAttendAmount(10L); //购买次数
        mockGoods.setViewAmount(2L); //浏览量
        mockGoods.setMerchantId(3447L); //设置商城ID
        mockGoods = goodsRepository.saveAndFlush(mockGoods);
        return mockGoods;
    }

    //丹青测试用于模拟期号，不准修改，谢谢配合
    public Issue daisyMockIssue(Goods goods) throws ParseException {
        Issue mockIssue = new Issue();
        mockIssue.setGoods(goods);//所属活动商品
        mockIssue.setStepAmount(goods.getStepAmount());//单次购买最低量
        mockIssue.setDefaultAmount(goods.getDefaultAmount()); //缺省购买人次
        mockIssue.setToAmount(goods.getToAmount()); //总需购买人次
        mockIssue.setBuyAmount(0L); //已购买的人次
        mockIssue.setPricePercentAmount(goods.getPricePercentAmount()); //每人次单价
        mockIssue.setAttendAmount(goods.getAttendAmount()); //购买次数,在中奖时从每期中累计此值
        mockIssue.setStatus(CommonEnum.IssueStatus.going);//状态
        mockIssue.setAwardingDate(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse("2016-04-11 05:00:00"));//开奖日期
        mockIssue = issueRepository.saveAndFlush(mockIssue);
        return mockIssue;
    }

    //找出列表中最大的期号
    public Long findMaxIssueId(List<Issue> issues) {
        int j = issues.size();
        Long issueId = issues.get(0).getId();
        for (int i = 1; i < j; i++) {
            Long nextID = issues.get(i).getId();
            if (issueId < nextID)
                issueId = nextID;
        }
        return issueId;
    }



    /***************************COSY测试商品，不准你动*************************/


    //创建商品
    Goods goods;

    public Goods createGoods()
    {
        //商品
        goods = new Goods();
        goods.setTitle("cosytest");
        goods.setCharacters("这是测试商品");
        goods.setDefaultPictureUrl("/resources/images/aa.jpg");
        goods.setPictureUrls("/resources/images/bb.jpg,/resources/images/cc.jpg");
        goods.setSharePictureUrl("/resources/images/dd.jpg");
        goods.setDefaultAmount(10L);
        goods.setToAmount(100L);
        goods.setPricePercentAmount(new BigDecimal(1));
        goods.setStatus(CommonEnum.GoodsStatus.up);
        goods = goodsRepository.saveAndFlush(goods);
        return goods;

    }


    //创建用户
    User currentUser;
    public  User createUser()
    {
        currentUser = new User();
        currentUser.setUsername("cosylj");
        currentUser.setPassword("123456");
        currentUser.setMobile("13600541783");
        currentUser.setMobileBinded(true);
        currentUser.setWeixinOpenId("111");
        currentUser.setMerchantId(3447L);
        currentUser.setMoney(new BigDecimal(100));
        currentUser.setRegTime(new Date());
        currentUser=userRepository.saveAndFlush(currentUser);
        return currentUser;
    }

    //期号
    Issue currentIssue;

    public Issue createIssue(Goods goods,User currentUser) throws Exception
    {
        currentIssue = new Issue();
        currentIssue.setGoods(goods);
        currentIssue.setDefaultAmount(10L);
        currentIssue.setToAmount(100L);
        currentIssue.setBuyAmount(10L);
        currentIssue.setAwardingDate(new Date());
        currentIssue.setPricePercentAmount(new BigDecimal(1));
        currentIssue.setAttendAmount(10L);
        currentIssue.setStatus(CommonEnum.IssueStatus.drawed);
        currentIssue.setAwardingUser(currentUser);
        currentIssue= issueRepository.saveAndFlush(currentIssue);
        return currentIssue;
    }

    //收货地址
    Delivery delivery;

    public Delivery  createDelivery(Issue currentIssue,User currentUser) throws Exception
    {


        delivery = new Delivery();
        delivery.setIssue(currentIssue);
        delivery.setUser(currentUser);
        delivery.setDeliveryStatus(CommonEnum.DeliveryStatus.GetPrize);
        delivery.setIsCommit(false);
        delivery = deliveryRepository.saveAndFlush(delivery);
        return delivery;
    }

    //用户购买商品的记录
    UserBuyFlow userBuyFlow;

    public UserBuyFlow createUserBuyFlow () throws Exception
    {


        userBuyFlow = new UserBuyFlow();
        userBuyFlow.setUser(currentUser);
        userBuyFlow.setIssue(currentIssue);
        userBuyFlow.setAmount(10L);
        userBuyFlow= userBuyFlowRepository.saveAndFlush(userBuyFlow);
        for (int i=0 ; i<10; i++){
            UserNumber userNumber = new UserNumber();
            userNumber.setIssue(currentIssue);
            userNumber.setUser(currentUser);
            userNumber.setNumber(100000L+i);
            userNumberRepository.save(userNumber);

        }
        return userBuyFlow;

    }



    //创建订单
    Orders orders;

    public Orders createOrders()
    {
        orders = new Orders();
        orders.setId("2016011508080848599");
        orders.setUser(currentUser);
        orders.setReceiver("cosylj");
        orders.setOrderType(CommonEnum.OrderType.raiders);
        orders.setTotalMoney(new BigDecimal(10));
        orders.setMoney(new BigDecimal(10));
        orders.setPayType(CommonEnum.PayType.alipay);
        orders.setStatus(CommonEnum.OrderStatus.payed);
        orders.setDetails("aaaaa");
        orders= ordersRepository.saveAndFlush(orders);
        return orders;
    }

}

