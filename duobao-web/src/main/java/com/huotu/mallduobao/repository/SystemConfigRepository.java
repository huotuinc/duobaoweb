package com.huotu.mallduobao.repository;

import com.huotu.mallduobao.entity.SystemConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * Created by lgh on 2016/4/19.
 */
@Repository
public interface SystemConfigRepository extends JpaRepository<SystemConfig, String>,JpaSpecificationExecutor<String> {


}
