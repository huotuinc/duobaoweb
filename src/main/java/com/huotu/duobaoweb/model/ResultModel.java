/*
 * 版权所有:杭州火图科技有限公司
 * 地址:浙江省杭州市滨江区西兴街道阡陌路智慧E谷B幢4楼
 *
 * (c) Copyright Hangzhou Hot Technology Co., Ltd.
 * Floor 4,Block B,Wisdom E Valley,Qianmo Road,Binjiang District
 * 2013-2015. All rights reserved.
 */

package com.huotu.duobaoweb.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResultModel {
	private int error;

	private int code;

	private String message;

	private String url;

	private Object data;
}