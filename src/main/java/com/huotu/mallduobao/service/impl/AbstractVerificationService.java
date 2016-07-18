package com.huotu.mallduobao.service.impl;

import com.huotu.mallduobao.entity.VerificationCode;
import com.huotu.mallduobao.exceptions.InterrelatedException;
import com.huotu.mallduobao.repository.VerificationCodeRepository;
import com.huotu.mallduobao.service.VerificationService;
import com.huotu.mallduobao.utils.CodeType;
import com.huotu.mallduobao.utils.SysRegex;
import com.huotu.mallduobao.utils.VerificationType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * @author CJ
 */
public abstract class AbstractVerificationService implements VerificationService {

    @Autowired
    private VerificationCodeRepository verificationCodeRepository;
    /**
     * 允许间隔60秒
     */
    private int gapSeconds = 60;

    @Transactional
    public void sendCode(String mobile, VerificationProject project, String code, Date currentDate, VerificationType type, CodeType sentType)
            throws IllegalStateException, IllegalArgumentException, NoSuchMethodException, InterrelatedException {
        if (!SysRegex.IsValidMobileNo(mobile)) {
            throw new IllegalArgumentException("号码不对");
        }
        if (sentType == null) {
            sentType = CodeType.text;
        }

        if (!supportVoice() && sentType == CodeType.voice) {
            throw new NoSuchMethodException("还不支持语音播报");
        }

        VerificationCode verificationCode = verificationCodeRepository.findByMobileAndTypeAndCodeType(mobile, type, sentType);
        if (verificationCode != null) {
            //刚刚发送过
            if (currentDate.getTime() - verificationCode.getSendTime().getTime() < gapSeconds * 1000) {
                throw new IllegalStateException("刚刚发过");
            }
        } else {
            verificationCode = new VerificationCode();
            verificationCode.setMobile(mobile);
            verificationCode.setType(type);
            verificationCode.setCodeType(sentType);
        }
        verificationCode.setSendTime(currentDate);
        verificationCode.setCode(code);
        verificationCode = verificationCodeRepository.save(verificationCode);

        doSend(project, verificationCode);
    }

    protected abstract void doSend(VerificationProject project, VerificationCode code) throws InterrelatedException;

    @Transactional
    public boolean verifyCode(String mobile, VerificationProject project, String code, Date currentDate, VerificationType type) throws IllegalArgumentException {
        if (!SysRegex.IsValidMobileNo(mobile)) {
            throw new IllegalArgumentException("号码不对");
        }
        List<VerificationCode> codeList = verificationCodeRepository.findByMobileAndTypeAndSendTimeGreaterThan(mobile, type, new Date(currentDate.getTime() - gapSeconds * 1000));

//        Date limitTime=new Date(currentDate.getTime() - gapSeconds * 1000);
//        StringBuilder hql = new StringBuilder();
//        hql.append("select vc from VerificationCode as vc where vc.mobile=:mobile " +
//                " and vc.type=:type " );//+
//                //" and vc.sendTime>:limitTime ");
//        List<VerificationCode> codeList = verificationCodeRepository.queryHql(hql.toString(), query -> {
//            query.setParameter("mobile", mobile);
//            query.setParameter("type", type);
//           // query.setParameter("limitTime", limitTime);
//        });

        for (VerificationCode verificationCode : codeList) {
            if (verificationCode.getCode().equals(code))
                return true;
        }
        return false;
    }

    @Transactional
    public boolean checkPhoneAndCode(String phone, String code){
        if(verificationCodeRepository.findByMobileAndCode(phone,code)!=null){
            return true;
        }else{
        return false;
        }
    }
    public int getGapSeconds() {
        return gapSeconds;
    }

    public void setGapSeconds(int gapSeconds) {
        this.gapSeconds = gapSeconds;
    }
}
