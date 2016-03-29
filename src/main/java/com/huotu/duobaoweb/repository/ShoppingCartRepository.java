package com.huotu.duobaoweb.repository;

import com.huotu.duobaoweb.entity.ShoppingCart;
import com.huotu.duobaoweb.entity.User;
import org.luffy.lib.libspring.data.ClassicsRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * Created by xhk on 2016/3/25.
 */
@Repository
public interface ShoppingCartRepository  extends JpaRepository<ShoppingCart, Long>, ClassicsRepository<ShoppingCart>, JpaSpecificationExecutor<ShoppingCart> {

    @Modifying
    @Query("delete from ShoppingCart sc where sc.user=?1")
    void clearShoppingCarts(User user);


    @Query("select sc from ShoppingCart as sc where sc.user.id=?1")
    ShoppingCart findOneByUserId(Long userId);
}
