package com.huotu.duobaoweb.repository;


import com.huotu.duobaoweb.entity.CountResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by lgh on 2016/1/28.
 */
@Repository
public interface CountResultRepository extends JpaRepository<CountResult,String> {

}
