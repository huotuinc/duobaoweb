package com.huotu.duobaoweb.repository;


import com.huotu.duobaoweb.common.CommonEnum;
import com.huotu.duobaoweb.entity.OrdersItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by lhx on 2016/1/19.
 */
@Repository
public interface OrdersItemRepository extends JpaRepository<OrdersItem, Long>,JpaSpecificationExecutor<OrdersItem> {
    @Query("select oi from OrdersItem as oi where oi.order.user.id = ?1 and oi.order.status = com.huotu.duobaoweb.common.CommonEnum.OrderStatus.payed  and oi.issue.status =?2")
    List<OrdersItem> findByUserIdAndStatus(Long userId, CommonEnum.IssueStatus status);

    @Query("select oi from OrdersItem as oi where oi.order.user.id = ?1 and oi.issue.goods.id=?2  and oi.issue.id = ?3 and oi.issue.status=com.huotu.duobaoweb.common.CommonEnum.IssueStatus.drawed")
    List<OrdersItem> findByUserIdAndIssuId(Long userId, Long gooodId, Long issueId);

    @Query("select oi from OrdersItem as oi where oi.order.id = ?1")
    OrdersItem findByOrderId(String id);
}
