package com.huotu.mallduobao.repository;

import com.huotu.mallduobao.entity.Orders;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by admin on 2016/1/19.
 */
@Repository
public interface OrdersRepository  extends JpaRepository<Orders,String> {

}
