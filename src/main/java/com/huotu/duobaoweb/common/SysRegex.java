package com.huotu.duobaoweb.common;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SysRegex {
	private static String _RegNoNagInt = "^\\d+$";// 非负整数（正整数 + 0）
	private static String _RegRectInt = "^[0-9]*[1-9][0-9]*$";// 正整数
	private static String _RegNoRectInt = "^((-\\d+)(0+))$";// 非正整数（负整数 + 0）
	private static String _RegNagInt = "^-[0-9]*[1-9][0-9]*$";// 负整数
	private static String _RegInt = "^-?\\d+$";// 整数
	private static String _RegNoNagDouble = "^\\d+(\\.\\d+)?$";// 非负浮点数（正浮点数 +
																// 0）
	private static String _RegRectDouble = "^(([0-9]+\\.[0-9]*[1-9][0-9]*)([0-9]*[1-9][0-9]*\\.[0-9]+)([0-9]*[1-9][0-9]*))$";// 正浮点数
	private static String _RegNoRectDouble = "^((-\\d+(\\.\\d+)?)(0+(\\.0+)?))$";// 非正浮点数（负浮点数
																					// +
																					// 0）
	private static String _RegNagDouble = "^(-(([0-9]+\\.[0-9]*[1-9][0-9]*)([0-9]*[1-9][0-9]*\\.[0-9]+)([0-9]*[1-9][0-9]*)))$";// 负浮点数
	private static String _RegDouble = "^(-?\\d+)(\\.\\d+)?$";// 浮点数
	private static String _RegABC = "^[A-Za-z]+$";// 由26个英文字母组成的字符串
	private static String _RegabcBig = "^[A-Z]+$";// 由26个英文字母的大写组成的字符串
	private static String _Regabc = "^[a-z]+$";// 由26个英文字母的小写组成的字符串
	private static String _RegABCNum = "^[A-Za-z0-9]+$";// 由数字和26个英文字母组成的字符串
	private static String _RegNomalInput = "^\\w+$";// 由数字、26个英文字母或者下划线组成的字符串
	private static String _RegABCNumCh = "^[a-zA-Z0-9\\u4e00-\\u9fa5]+$";// 字母数字汉字
	private static String _RegABCCh = "^[a-zA-Z\\u4e00-\\u9fa5]+$";// 字母数字汉字
	private static String _RegCh = "^[\\u4e00-\\u9fa5]+$";// 汉字
	private static String _RegNum = "^[0-9]+$";// 数字
	// / <summary>
	// / email地址
	// / </summary>
	private static String _RegEmail = "^[.\\-_a-zA-Z0-9]+@([.\\-_a-zA-Z0-9]+\\.)+[a-zA-Z0-9]{2,3}$";
	// / <summary>
	// / 小数
	// / </summary>
	private static String _RegDecimal = "^[0].\\d{1,2}|[1]$";
	// / <summary>
	// / 电话号码
	// / </summary>
	private static String _RegTel = "^(\\d+-)?(\\d{4}-?\\d{7}|\\d{3}-?\\d{8}|^\\d{7,8})(-\\d+)?$";
	private static String _RegFixedTel = "^\\d{3}-\\d{8}|\\d{4}-\\d{7}$";
	private static String _RegMobileNo = "^(1)\\d{10}$";
	// / <summary>
	// / 年月日
	// / </summary>
	// private static String _RegDate
	// ="^2\\d{3}-(?:0?[1-9]|1[0-2])-(?:0?[1-9]|[1-2]\\d|3[0-1])(?:0?[1-9]|1\\d|2[0-3]):(?:0?[1-9]|[1-5]\\d):(?:0?[1-9]|[1-5]\\d)$";
	// / <summary>
	// / 后缀名
	// / </summary>
	private static String _RegPostfix = "^\\.(?i:gif|jpg)$";
	// / <summary>
	// / Ip
	// / </summary>
	private static String _RegIP = "^(\\d{1,2}|1\\d\\d|2[0-4]\\d|25[0-5])\\.(\\d{1,2}|1\\d\\d|2[0-4]\\d|25[0-5])\\.(\\d{1,2}|1\\d\\d|2[0-4]\\d|25[0-5])\\.(\\d{1,2}|1\\d\\d|2[0-4]\\d|25[0-5])$";

	private static String _RegChinese = "^[\\u4e00-\\u9fa5]$";
	private static String _RegDoubleByte = "^[^\\x00-\\xff]$";
	private static String _RegCard = "^\\d{15}|\\d{18}$";
	private static String _RegPost = "^[1-9]\\d{5}(?!\\d)$";
	private static String _RegQQ = "^[1-9][0-9]{4,}$";

	private static String _RegGUID = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-f]{4}-[0-9a-fA-f]{4}-[0-9a-fA-F]{12}$";

	private static String _RegChapNo = "^[0-9]{8}_[0-9]{6}$";

	private static String _RegChapTime = "^[0-9]{2}:[0-9]{2}:[0-9]{2}$";
	private static String _RegLoginName = "^[A-Za-z]+[A-Za-z0-9_\\-]+$";

	private static String _RegVersion = "^[0-9].[0-9].[0-9]$";

	public SysRegex() {
	}

	// / <summary>
	// / 由英文字母组成
	// / </summary>
	// / <param name="input"></param>
	// / <returns></returns>
	public static Boolean IsValidABC(String input) {
		Pattern pattern = Pattern.compile(_RegABC);
		Matcher matcher = pattern.matcher(input);
		return matcher.matches();
	}

	// / <summary>
	// / 由数字组成
	// / </summary>
	// / <param name="input"></param>
	// / <returns></returns>
	public static Boolean IsValidInt(String input) {
		Pattern pattern = Pattern.compile(_RegInt);
		Matcher matcher = pattern.matcher(input);
		return matcher.matches();
	}

	// / <summary>
	// / 由数字和26个英文字母组成的字符串
	// / </summary>
	// / <param name="input"></param>
	// / <returns></returns>
	public static Boolean IsValidABCNum(String input) {
		Pattern pattern = Pattern.compile(_RegABCNum);
		Matcher matcher = pattern.matcher(input);
		return matcher.matches();
	}

	// / <summary>
	// / 由数字、26个英文字母或者下划线组成的字符串
	// / </summary>
	// / <param name="input"></param>
	// / <returns></returns>
	public static Boolean IsValidNomalInput(String input) {
		Pattern pattern = Pattern.compile(_RegNomalInput);
		Matcher matcher = pattern.matcher(input);
		return matcher.matches();
	}

	// / <summary>
	// / 验证Email地址
	// / </summary>
	// / <param name="input"></param>
	// / <returns></returns>
	public static Boolean IsValidEmail(String input) {

		Pattern pattern = Pattern.compile(_RegEmail);
		Matcher matcher = pattern.matcher(input);
		return matcher.matches();
	}

	// / <summary>
	// / 验证是否为小数
	// / </summary>
	// / <param name="input"></param>
	// / <returns></returns>
	public static Boolean IsValidDecimal(String input) {

		Pattern pattern = Pattern.compile(_RegDecimal);
		Matcher matcher = pattern.matcher(input);
		return matcher.matches();
	}

	// / <summary>
	// / 验证是否为电话号码
	// / </summary>
	// / <param name="input"></param>
	// / <returns></returns>
	public static Boolean IsValidTel(String input) {

		Pattern pattern = Pattern.compile(_RegTel);
		Matcher matcher = pattern.matcher(input);
		return matcher.matches();
	}

	// / <summary>
	// / 固定电话
	// / </summary>
	// / <param name="input"></param>
	// / <returns></returns>
	public static Boolean IsValidFixedTel(String input) {

		Pattern pattern = Pattern.compile(_RegFixedTel);
		Matcher matcher = pattern.matcher(input);
		return matcher.matches();
	}

	// / <summary>
	// / 手机号
	// / </summary>
	// / <param name="input"></param>
	// / <returns></returns>
	public static Boolean IsValidMobileNo(String input) {

		Pattern pattern = Pattern.compile(_RegMobileNo);
		Matcher matcher = pattern.matcher(input);
		return matcher.matches();
	}

	// / <summary>
	// / 验证年月日
	// / </summary>
	// / <param name="input"></param>
	// / <returns></returns>
	// public static Boolean IsValidDate(String input)
	// {
	// return Regex.IsMatch(input, _RegDate);
	// }
	// / <summary>
	// / 验证后缀名
	// / </summary>
	// / <param name="input"></param>
	// / <returns></returns>
	public static Boolean IsValidPostfix(String input) {

		Pattern pattern = Pattern.compile(_RegPostfix);
		Matcher matcher = pattern.matcher(input);
		return matcher.matches();
	}

	// / <summary>
	// / 验证IP
	// / </summary>
	// / <param name="input"></param>
	// / <returns></returns>
	public static Boolean IsValidIp(String input) {

		Pattern pattern = Pattern.compile(_RegIP);
		Matcher matcher = pattern.matcher(input);
		return matcher.matches();
	}

	// / <summary>
	// / 中文字符
	// / </summary>
	// / <param name="input"></param>
	// / <returns></returns>
	public static Boolean IsValidChinese(String input) {

		Pattern pattern = Pattern.compile(_RegChinese);
		Matcher matcher = pattern.matcher(input);
		return matcher.matches();
	}

	// / <summary>
	// / 双字节字符(包括汉字在内)：
	// / </summary>
	// / <param name="input"></param>
	// / <returns></returns>
	public static Boolean IsValidDoubleByte(String input) {
		Pattern pattern = Pattern.compile(_RegDoubleByte);
		Matcher matcher = pattern.matcher(input);
		return matcher.matches();
	}

	// / <summary>
	// / 身份证[中国的身份证为15位或18]
	// / </summary>
	// / <param name="input"></param>
	// / <returns></returns>
	public static Boolean IsValidCard(String input) {

		Pattern pattern = Pattern.compile(_RegCard);
		Matcher matcher = pattern.matcher(input);
		return matcher.matches();
	}

	// / <summary>
	// /中国邮政编码为6位数字
	// / </summary>
	// / <param name="input"></param>
	// / <returns></returns>
	public static Boolean IsValidPost(String input) {
		Pattern pattern = Pattern.compile(_RegPost);
		Matcher matcher = pattern.matcher(input);
		return matcher.matches();
	}

	// / <summary>
	// /腾讯QQ号从10000开始
	// / </summary>
	// / <param name="input"></param>
	// / <returns></returns>
	public static Boolean IsValidQQ(String input) {

		Pattern pattern = Pattern.compile(_RegQQ);
		Matcher matcher = pattern.matcher(input);
		return matcher.matches();
	}

	// / <summary>
	// / 字母数字和汉字
	// / </summary>
	// / <param name="input"></param>
	// / <returns></returns>
	public static Boolean IsValidAbcNumCh(String input) {

		Pattern pattern = Pattern.compile(_RegABCNumCh);
		Matcher matcher = pattern.matcher(input);
		return matcher.matches();
	}

	// / <summary>
	// / 字母汉字组合
	// / </summary>
	// / <param name="input"></param>
	// / <returns></returns>
	public static Boolean IsValidAbcCh(String input) {

		Pattern pattern = Pattern.compile(_RegABCCh);
		Matcher matcher = pattern.matcher(input);
		return matcher.matches();
	}

	// / <summary>
	// / 汉字组成
	// / </summary>
	// / <param name="input"></param>
	// / <returns></returns>
	public static Boolean IsValidCh(String input) {
		Pattern pattern = Pattern.compile(_RegCh);
		Matcher matcher = pattern.matcher(input);
		return matcher.matches();
	}

	// / <summary>
	// / 有数字组成
	// / </summary>
	// / <param name="input"></param>
	// / <returns></returns>
	public static Boolean IsValidNum(String input) {

		Pattern pattern = Pattern.compile(_RegNum);
		Matcher matcher = pattern.matcher(input);
		return matcher.matches();
	}

	// / <summary>
	// / 是否是统一编码
	// / </summary>
	// / <param name="input"></param>
	// / <returns></returns>
	public static Boolean IsGUID(String input) {
		Pattern pattern = Pattern.compile(_RegGUID);
		Matcher matcher = pattern.matcher(input);
		return matcher.matches();
	}

	// / <summary>
	// / 是否是课件编号如 20100809_120945
	// / </summary>
	// / <param name="input"></param>
	// / <returns></returns>
	public static Boolean IsChapNo(String input) {
		Pattern pattern = Pattern.compile(_RegChapNo);
		Matcher matcher = pattern.matcher(input);
		return matcher.matches();
	}

	// / <summary>
	// / 是否是课件时长格式如 01:02:03
	// / </summary>
	// / <param name="input"></param>
	// / <returns></returns>
	public static Boolean IsChapTime(String input) {

		Pattern pattern = Pattern.compile(_RegChapTime);
		Matcher matcher = pattern.matcher(input);
		return matcher.matches();
	}

	// / <summary>
	// / 用户名，登录名(字母数字_-)
	// / </summary>
	// / <param name="input"></param>
	// / <returns></returns>
	public static Boolean IsLoginName(String input) {

		Pattern pattern = Pattern.compile(_RegLoginName);
		Matcher matcher = pattern.matcher(input);
		return matcher.matches();
	}

	// / <summary>
	// / 版本号 如1.10.6
	// / </summary>
	// / <param name="input"></param>
	// / <returns></returns>
	public static Boolean IsVersion(String input) {

		Pattern pattern = Pattern.compile(_RegVersion);
		Matcher matcher = pattern.matcher(input);
		return matcher.matches();
	}
}
