package com.huotu.mallduobao.repository;


import com.huotu.mallduobao.utils.CommonEnum;
import com.huotu.mallduobao.entity.OrdersItem;
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
    @Query("select oi from OrdersItem as oi where oi.order.id = ?1")
    OrdersItem findByOrderId(String id);
}
