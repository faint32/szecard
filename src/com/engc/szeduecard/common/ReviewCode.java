package com.engc.szeduecard.common;

/**
 * 审核 类型代码类
 * 
 * @author samwu
 * 
 */
public class ReviewCode {
	// 发布活动，推送到管理员审核，审核中
	public static final String ACTIVITY_RELEASE_REVIEWING = "A1";
	// 管理员审核活动后，将信息推送至申请人，已审核
	public static final String ACTIVITY_RELEASE_REVIEWED = "A2";
	// 志愿者申请加入活动，信息推送至管理员 ，待审核
	public static final String APPLY_JOIN_ACTIVITY_REVIEWING = "A3";
	// 管理员审核用户加入请求 ，作出审核 ，将消息推送至申请人，已审核
	public static final String APPLY_JOIN_ACTIVITY_REVIEWED = "A4";
	//活动终止 推送，推送 给参与活动人员
	public static final String ACTIVITY_STOP="STOP";
	// 管理发布活动，推送至所有人
    public static final String ACTIVITY_RELEASE_REVIEWED_TO_PERSON = "A5";
	
	// 发布新闻 ，将信息推送至管理员 审核，审核中
	public static final String NEWS_RELEASE_REVIEWING = "N1";
	// 管理员 审核 发布新闻请求，作出审核，将消息推送至发布人，已审核
	public static final String NEWS_RELEASE_REVIEWED = "N2";

	// 发布招募信息，推送至管理员 审核，审核中
	public static final String RECRUIT_RELEASE_REVIEWING = "R1";

	// 管理员审核招募信息，作出审核，将消息推送至申请人 ，已审核
	public static final String RECRUIT_RELEASE_REVIEWED = "R2";

	// 管理员向志愿者向用户邀请参加团队请求，推送至被邀请人 ，
	public static final String INVITE_USER_JOIN_TEAM_REVIEWING = "R3";

	// 被邀请参加团队 用户 ，将结果 反馈给 邀请者（管理员）
	public static final String INVITE_USER_JOIN_TEAM_REVIEWED = "R4";

	// 管理员向志愿者向用户邀请参加活动请求，推送至被邀请人 ，
	public static final String INVITE_USER_JOIN_ACTION_REVIEWING = "R5";

	// 被邀请参加活动 用户 ，将结果 反馈给 邀请者（管理员）
	public static final String INVITE_USER_JOIN_ACTION_REVIEWED = "R6";

	// 团队注册请求，信息推送至管理员，审核中
	public static final String TEAM_REGISTER_REVIEWING = "T1";

	// 管理员审核团队注册请求，作出审核，将消息推送至申请人，已审核
	public static final String TEAM_REGISTER_REVIEWED = "T2";

	// 志愿者申请加入团队，推送至管理员，审核中
	public static final String JOIN_TEAM_REVIEWING = "T3";
	// 管理员审核加入团队请求，作出审核，推送至申请人，已审核
	public static final String JOIN_TEAM_REVIEWED = "T4";
	//退团操作，将消息推送给志愿者
	public static final String LIVE_TEAM="TT2";
    
	//实践基地审核，推送至管理员，审核中
    public static final String BASE_STATION_REVIEWING="B1";
	//管理员审核实践基地请求，作出审核，推送至申请人，已审核
    public static final String BASE_STATION_REVIEWED="B2";
		
	//服务站点审核，推送至管理员，审核中
	public static final String ACT_SITE_REVIEWING="S1";
	//管理员审核服务站点申请，作出审核，推送至申请人，已审核
	public static final String  ACT_SITE_REVIEWED="S2";
		
	//志愿者心语发布，推送给管理员审核
	public static final String 	VOLUNTEER_REVIEWING="V1";
	//志愿者心语审核通过，推送给发布人
	public static final String 	VOLUNTEER_REVIEWED="V2";
		
	//志愿者上传文件，推送给系统管理员审核
	public static final String FILE_UPLOAD_REVIEWING="F1";
	//志愿者上传文件审核结束，推送给上传人
	public static final String FILE_UPLOAD_REVIEWED="F2";

	// 志愿者卡已发放
	public static final String CARD_REVIEWED = "C2";

	// 订单生成，推送给系统管理员
	public static final String STAR_AREA_REVIEWING = "SA1";
	// 系统管理员发货，推送信息给用户
	public static final String STAR_AREA_REVIEWED = "SA2";

}
