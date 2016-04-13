package com.huotu.mallduobao.repository;

import com.huotu.mallduobao.entity.City;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by lhx on 2016/3/29.
 */
@Repository
public interface CityRepository extends JpaRepository<City,Integer> ,JpaSpecificationExecutor<City>{

    List<City> findByParentId(Integer parentId);
}
