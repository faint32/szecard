package com.engc.szeduecard.bean;

/**
 * 卡状态 常量 类
 * 
 * @ClassName: CardStatus
 * @Description: TODO
 * @author wutao
 * @date 2013-10-28 上午9:33:05
 * 
 */
public final class CardStatus {
	public static final int UN_OPEN_CARD_ACCOUNT = -1; // 表示未 开户
	public static final int NORMAL_CARD = 1; // 表示正常
	public static final int REPORT_LOSS_ED_CARD = 2;// 已挂失
	public static final int FREEZE_ED_CARD = 7; // 已冻结
	public static final int LOGOUT_ED_CARD = 8; // 已注销
	public static final int LOGOUT_ING_CARD = 9; // 已登记注销
	public static final int OVERDRAW_FREEZE = 6; // 透支冻结

}
