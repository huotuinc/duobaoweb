package com.huotu.mallduobao.repository;

import com.huotu.mallduobao.entity.ShoppingCart;
import com.huotu.mallduobao.entity.User;
import org.luffy.lib.libspring.data.ClassicsRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by xhk on 2016/3/25.
 */
@Repository
public interface ShoppingCartRepository  extends JpaRepository<ShoppingCart, Long>, ClassicsRepository<ShoppingCart>, JpaSpecificationExecutor<ShoppingCart> {

    @Modifying
    @Transactional
    @Query("delete from ShoppingCart sc where sc.user=?1")
    void clearShoppingCarts(User user);


    @Query("select sc from ShoppingCart as sc where sc.user.id=?1")
    ShoppingCart findOneByUserId(Long userId);
}
