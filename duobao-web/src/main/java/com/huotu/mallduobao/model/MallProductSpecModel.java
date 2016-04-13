package com.huotu.mallduobao.model;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by lhx on 2016/4/8.
 */
@Getter
@Setter
public class MallProductSpecModel {
    private Long id;
    private String spec;
    private int stock;
    private int freeze;
}
