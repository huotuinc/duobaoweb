package com.huotu.mallduobao.service;

import com.huotu.mallduobao.exceptions.InterrelatedException;
import com.huotu.mallduobao.utils.CodeType;
import com.huotu.mallduobao.utils.VerificationType;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * 通用验证码服务
 *
 * @author CJ
 */
public interface VerificationService {
    /**
     * 验证数据库中是否存在这个手机号与验证码相对应的
     * 用于web版密码的提交时的验证
     * by xhk
     *
     * @param phone 手机号
     * @param code  验证码
     * @return
     */
    @Transactional
    boolean checkPhoneAndCode(String phone, String code);

    //void sendCode(String phone, VerificationProject fanmore, String code, Date date, VerificationType verificationType,);

    /**
     * 可以使用验证码的项目
     */
    enum VerificationProject {
        //【粉猫】您的验证码是{1},为了保护您的账户安全,验证短信请勿转发他人。工作人员不会向你索取验证码。
        fanmore("26807", "(验证码:%s)为了保护您的账户安全，验证短信请勿转发他人。工作人员不会向你索取验证码。"),
        lottery("26807","亲，恭喜您，您参与的宝贝#（%s）#已经中奖啦，快去填写收件地址吧，请随时保持电话畅通。"),
        crab_error("20001", "抓取异常，请在5分钟之内进行手动抽奖操作。");

        private final String format;
        private final String templateId;

        public String getTemplateId() {
            return templateId;
        }

        public String getFormat() {
            return format;
        }

        VerificationProject(String templateId, String format) {
            this.templateId = templateId;
            this.format = format;
        }
    }

    /**
     * 是否支持语音播报
     *
     * @return true 如果支持
     */
    boolean supportVoice();

    /**
     * 发送验证码
     *
     * @param mobile      接受者手机号码
     * @param project     项目信息，比如短信包含内容什么的
     * @param code        验证码
     * @param currentDate 当前日期
     * @param type        验证码类型
     * @param sentType    发送类型
     * @throws IllegalStateException    90秒内已发过
     * @throws IllegalArgumentException 手机号码或者其他参数有误
     * @throws NoSuchMethodException    该发送类别不受支持
     * @throws InterrelatedException    第三方平台错误
     */
    @Transactional
    void sendCode(String mobile, VerificationProject project, String code, Date currentDate, VerificationType type, CodeType sentType)
            throws IllegalStateException, IllegalArgumentException, NoSuchMethodException, InterrelatedException;

    /**
     * 验证该验证码
     *
     * @param mobile      接受者手机
     * @param project     项目信息，比如短信内容包含什么来着
     * @param code        验证码
     * @param currentDate 当前日期
     * @param type        验证码类型
     * @return true表示验证通过
     * @throws IllegalArgumentException 手机号码或者其他参数有误
     */
    @Transactional
    boolean verifyCode(String mobile, VerificationProject project, String code, Date currentDate, VerificationType type) throws IllegalArgumentException;

}
