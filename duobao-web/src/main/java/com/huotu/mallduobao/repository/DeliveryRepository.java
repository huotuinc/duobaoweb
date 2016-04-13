package com.huotu.mallduobao.repository;


import com.huotu.mallduobao.entity.Delivery;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * Created by lhx on 2016/2/20.
 */
@Repository
public interface DeliveryRepository extends JpaRepository<Delivery, Long>, JpaSpecificationExecutor<Delivery> {
    /**
     * 获取中奖信息
     *
     * @param issueId
     * @return
     */
    @Query("select d from Delivery as d where d.issue.id=?1")
    Delivery findByIssueId(Long issueId);


}
