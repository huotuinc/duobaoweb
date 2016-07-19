package com.huotu.mallduobao.repository;

import com.huotu.mallduobao.entity.VerificationCode;
import com.huotu.mallduobao.utils.CodeType;
import com.huotu.mallduobao.utils.VerificationType;
import org.luffy.lib.libspring.data.ClassicsRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**
 * @author CJ
 */
@Repository
public interface VerificationCodeRepository extends JpaRepository<VerificationCode,Long>, ClassicsRepository<VerificationCode>,JpaSpecificationExecutor<VerificationCode> {

    /**
     * 根据手机和类型返回验证码
     * @param mobile
     * @param type
     * @param codeType
     * @return
     */
    VerificationCode findByMobileAndTypeAndCodeType(String mobile, VerificationType type, CodeType codeType);

    /**
     * 根据手机和类型返回验证码
     * @param mobile
     * @param type
     * @param last 最晚许可的发送时间
     * @return
     */
    List<VerificationCode> findByMobileAndTypeAndSendTimeGreaterThan(String mobile, VerificationType type, Date last);

    /**
     *验证是否存在当前手机号和验证码的数据
     * @return
     */
    VerificationCode findByMobileAndCode(String phone, String code);

}
