package com.huotu.mallduobao.service.impl;


import com.huotu.mallduobao.entity.SystemConfig;
import com.huotu.mallduobao.repository.SystemConfigRepository;
import com.huotu.mallduobao.service.VersionUpgrade;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by lgh on 2016/2/27.
 */
@Service
public class BaseService {

    private static Log log = LogFactory.getLog(BaseService.class);

    @Autowired
    private SystemConfigRepository systemConfigRepository;

    /**
     * 尝试系统升级,在发现需要升级以后将调用升级者,可以通过JDBC操作数据表.
     * <p>
     * <p>
     * 需要注意的是,版本升级采用的是逐步升级策略,比如数据库标记版本为1.0 然后更新到3.0 中间还存在2.0(这也是为什么版本标记是用枚举保
     * 存的原因),那么会让升级者升级到2.0再到3.0
     * </p>
     * <p>
     * 如果没有发现数据库版本标记 那么就默认为已经是当前版本了.
     * </p>
     *
     * @param systemStringVersionKey 保存版本信息的key,必须确保唯一;如果当前没有相关信息,则认为已经是当前版本了.
     * @param clazz                  维护版本信息的枚举类
     * @param currentVersion         当前版本
     * @param upgrader               负责提供系统升级业务的升级者
     * @param <T>                    维护版本信息的枚举类
     */
    @Transactional
    public <T extends Enum> void systemUpgrade(String systemStringVersionKey, Class<T> clazz
            , T currentVersion, VersionUpgrade<T> upgrader) {

        log.info("system upate to " + currentVersion);
        SystemConfig databaseVession = systemConfigRepository.findOne(systemStringVersionKey);
        try {
            if (databaseVession == null) {
                databaseVession = new SystemConfig();
                databaseVession.setCode(systemStringVersionKey);
                databaseVession.setValueForCode(String.valueOf(currentVersion.ordinal()));
                systemConfigRepository.save(databaseVession);
            } else {
                T database = clazz.getEnumConstants()[Integer.parseInt(databaseVession.getValueForCode())];
                if (database != currentVersion) {
                    upgrade(systemStringVersionKey, clazz, database, currentVersion, upgrader);
                }
            }
        } catch (Exception ex) {
            throw new InternalError("Failed Upgrade Database", ex);
        }

    }

    private <T extends Enum> void upgrade(String systemStringVersionKey, Class<T> clazz, T origin, T target, VersionUpgrade<T> upgrader)
            throws Exception {
        log.debug("Subsystem prepare to upgrade to " + target);
        boolean started = false;
        for (T step : clazz.getEnumConstants()) {
            if (origin == null || origin.ordinal() < step.ordinal()) {
                started = true;
            }

            if (started) {
                log.debug("Subsystem upgrade step: to " + target);
                upgrader.upgradeToVersion(step);
                log.debug("Subsystem upgrade step done");
            }

            if (step == target)
                break;
        }

        SystemConfig databaseVersion = systemConfigRepository.findOne(systemStringVersionKey);
        if (databaseVersion == null) {
            throw new InternalError("!!!No Current Version!!!");
        }
        databaseVersion.setValueForCode(String.valueOf(target.ordinal()));
        systemConfigRepository.save(databaseVersion);

    }
}
