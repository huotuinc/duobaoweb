/*
 * 版权所有:杭州火图科技有限公司
 * 地址:浙江省杭州市滨江区西兴街道阡陌路智慧E谷B幢4楼
 *
 * (c) Copyright Hangzhou Hot Technology Co., Ltd.
 * Floor 4,Block B,Wisdom E Valley,Qianmo Road,Binjiang District
 * 2013-2015. All rights reserved.
 */

package com.huotu.mallduobao.service.impl;


import com.huotu.mallduobao.entity.VerificationCode;
import com.huotu.mallduobao.exceptions.InterrelatedException;
import com.huotu.mallduobao.model.ResultModel;
import com.huotu.mallduobao.service.CommonConfigService;
import com.huotu.mallduobao.service.VerificationService;
import com.huotu.mallduobao.utils.HttpSender;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.util.Formatter;
import java.util.Locale;

/**
 * @author CJ
 */
@Service
public class YimeiVerificationService extends AbstractVerificationService implements VerificationService {

    private static final Log log = LogFactory.getLog(YimeiVerificationService.class);
    @Autowired
    private Environment env;

    @Autowired
    private CommonConfigService commonConfigService;

    public YimeiVerificationService() {
        log.info("伊美短信平台使用中……");
    }

    @Override
    public boolean supportVoice() {
        return false;
    }

    @Override
    public void doSend(VerificationProject project, VerificationCode code) throws InterrelatedException {
        String smsContent = new Formatter(Locale.CHINA).format(project.getFormat(), code.getCode()).toString();
        ResultModel resultSMS = send(code.getMobile(), smsContent);
        if (resultSMS.getCode() != 0) throw new InterrelatedException();
    }


    private ResultModel send(String mobile, String msg) {
        ResultModel resultModel = new ResultModel();
        resultModel.setCode(-1000);
        resultModel.setMessage("未知错误");

        try {
            String errMsg = "";
            int code = 10000;
            String text = HttpSender.batchSend(commonConfigService.getCLMessageServerUrl(), commonConfigService.getCLMessageAccount()
                    , commonConfigService.getCLMessagePassword(), mobile, msg, true, null, null);
            code = Integer.parseInt(text.split("\n")[0].split(",")[1]);
            switch (code) {
                case 0:
                    errMsg = "发送成功";
                    break;
                case 101:
                    errMsg = "无此用户";
                    break;
                case 102:
                    errMsg = "密码错";
                    break;
                case 103:
                    errMsg = "提交过快（提交速度超过流速限制）";
                    break;
                case 104:
                    errMsg = "系统忙（因平台侧原因，暂时无法处理提交的短信）";
                    break;
                case 105:
                    errMsg = "敏感短信（短信内容包含敏感词）";
                    break;
                case 106:
                    errMsg = "消息长度错（>536或<=0）";
                    break;
                case 107:
                    errMsg = "包含错误的手机号码";
                    break;
                case 108:
                    errMsg = "手机号码个数错（群发>50000或<=0;单发>200或<=0）";
                    break;
                case 109:
                    errMsg = "无发送额度（该用户可用短信数已使用完）";
                    break;
                case 110:
                    errMsg = "不在发送时间内";
                    break;
                case 111:
                    errMsg = "超出该账户当月发送额度限制";
                    break;
                case 112:
                    errMsg = "无此产品，用户没有订购该产品";
                    break;
                case 113:
                    errMsg = "extno格式错（非数字或者长度不对）";
                    break;

                case 115:
                    errMsg = "自动审核驳回";
                    break;
                case 116:
                    errMsg = "签名不合法，未带签名（用户必须带签名的前提下）";
                    break;
                case 117:
                    errMsg = "IP地址认证错,请求调用的IP地址不是系统登记的IP地址";
                    break;
                case 118:
                    errMsg = "用户没有相应的发送权限";
                    break;
                case 119:
                    errMsg = "用户已过期";
                    break;
            }
            resultModel.setCode(code);
            resultModel.setMessage(errMsg);

        } catch (Exception ex) {
        }
        return resultModel;
    }
}
