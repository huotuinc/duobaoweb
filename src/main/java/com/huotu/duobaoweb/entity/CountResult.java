package com.huotu.duobaoweb.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.util.List;

/**
 * 中奖后计算结果
 * Created by lgh on 2016/1/28.
 */
@Entity
@Getter
@Setter

public class CountResult {


    /**
     * 时时彩期号
     * 如20160128038
     */
    @Id
    private String issueNo;

    /**
     * 截止该商品中奖时间点前最后50条全站参与记录
     */
    private Long numberA;
    /***
     * 时时彩开奖结果
     */
    private Long numberB;


    /**
     * 对应的最后50条参与记录
     */
    @OneToMany
    private List<UserNumber> userNumbers;

    /**
     * 对应期号数量
     */
    private Integer issueAmount;
}
