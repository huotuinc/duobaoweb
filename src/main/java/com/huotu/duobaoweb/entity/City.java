package com.huotu.duobaoweb.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

/**
 * Created by lhx on 2016/3/29.
 * 城市信息表
 * parentId = 0 省
 * 其他市县
 */
@Entity
@Getter
@Setter
public class City {

    /**
     * 城市id
     */
    @Id
    private Integer id;

    /**
     * 城市名
     */
    @Column(length = 20)
    private String cityName;

    /**
     * 父类id
     */
    private Integer parentId;

}
