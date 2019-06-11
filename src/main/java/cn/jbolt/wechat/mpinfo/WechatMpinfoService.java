package cn.jbolt.wechat.mpinfo;

import java.util.Date;
import java.util.List;

import com.jfinal.aop.Inject;
import com.jfinal.kit.Kv;
import com.jfinal.kit.Ret;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;

import cn.jbolt.base.BaseService;
import cn.jbolt.common.config.Msg;
import cn.jbolt.common.model.SystemLog;
import cn.jbolt.common.model.WechatMpinfo;
import cn.jbolt.wechat.autoreply.WechatAutoReplyService;
import cn.jbolt.wechat.config.WechatConfigService;
import cn.jbolt.wechat.menu.WechatMenuService;

/**   
 * 微信公众平台管理
 * @ClassName:  WechatMpinfoService   
 * @author: JFinal学院-小木 QQ：909854136 
 * @date:   2019年5月7日 下午5:18:47   
 *    
 * 注意：本内容仅限于JFinal学院 JBolt平台VIP成员内部传阅，请尊重开发者劳动成果，不要外泄出去用于其它商业目的
 */
public class WechatMpinfoService extends BaseService<WechatMpinfo> {
	@Inject
	private WechatConfigService wechatConfigService;
	@Inject
	private WechatMenuService wechatMenuService;
	@Inject
	private WechatAutoReplyService wechatAutoReplyService;

	@Override
	protected WechatMpinfo dao() {
		return WechatMpinfo.dao;
	}
	/**
	 * 保存
	 * @param userId
	 * @param wechatMpinfo
	 * @return
	 */
	public Ret save(Integer userId, WechatMpinfo wechatMpinfo) {
		if(wechatMpinfo==null||isOk(wechatMpinfo.getId())||notOk(wechatMpinfo.getName())||notOk(wechatMpinfo.getType())){
			return fail(Msg.PARAM_ERROR);
		}
		String name=wechatMpinfo.getName().trim();
		if(existsName(name)){
			return fail(Msg.DATA_SAME_NAME_EXIST);
		}
		wechatMpinfo.setName(name);
		//添加的必须是false 因为启用这个是有操作的
		wechatMpinfo.setEnable(false);
		wechatMpinfo.setUserId(userId);
		wechatMpinfo.setCreateTime(new Date());
		wechatMpinfo.setUpdateUserId(userId);
		wechatMpinfo.setUpdateTime(new Date());
		boolean success=wechatMpinfo.save();
		if(success){
			//添加日志
			addSaveSystemLog(wechatMpinfo.getId(), userId,SystemLog.TARGETTYPE_WECHAT_MPINFO, wechatMpinfo.getName());
		}
		return success?success(Msg.SUCCESS):fail(Msg.FAIL);
	}
	/**
	 * 更新
	 * @param userId
	 * @param wechatMpinfo
	 * @return
	 */
	public Ret update(Integer userId, WechatMpinfo wechatMpinfo) {
		if(wechatMpinfo==null||notOk(wechatMpinfo.getId())||notOk(wechatMpinfo.getName())||notOk(wechatMpinfo.getType())){
			return fail(Msg.PARAM_ERROR);
		}
		//不能轻易修改enable
		wechatMpinfo.remove("enable");
		String name=wechatMpinfo.getName().trim();
		if(existsName(name, wechatMpinfo.getId())){
			return fail(Msg.DATA_SAME_NAME_EXIST);
		}
		wechatMpinfo.setName(name);
		wechatMpinfo.setUpdateUserId(userId);
		wechatMpinfo.setUpdateTime(new Date());
		boolean success=wechatMpinfo.update();
		if(success){
			//添加日志
			addUpdateSystemLog(wechatMpinfo.getId(), userId,SystemLog.TARGETTYPE_WECHAT_MPINFO, wechatMpinfo.getName());
		}
		return success?success(Msg.SUCCESS):fail(Msg.FAIL);
	}
	/**
	 * 删除
	 * @param userId
	 * @param id
	 * @return
	 */
	public Ret delete(Integer userId, Integer id) {
		Ret ret=deleteById(id, true);
		if(ret.isOk()){
			//添加日志
			WechatMpinfo wechatMpinfo=ret.getAs("data");
			addDeleteSystemLog(id, userId,SystemLog.TARGETTYPE_WECHAT_MPINFO, wechatMpinfo.getName());
		}
		return ret;
	}
	
	@Override
	public String checkInUse(WechatMpinfo wechatMpinfo) {
		boolean wechatConfig=wechatConfigService.checkWechatMpinfoInUse(wechatMpinfo.getId());
		if(wechatConfig){return "此公众平台已经存在相关配置信息，不能删除！";}
		boolean wechatMenu=wechatMenuService.checkWechatMpinfoInUse(wechatMpinfo.getId());
		if(wechatMenu){return "此公众平台已经存在菜单配置，不能删除！";}
		boolean wechatAutoReply=wechatAutoReplyService.checkWechatMpinfoInUse(wechatMpinfo.getId());
		if(wechatAutoReply){return "此公众平台已经存在自动回复规则配置，不能删除！";}
		return null;
	}
	/**
	 * 切换启动/禁用状态
	 * @param userId
	 * @param id
	 * @return
	 */
	public Ret toggleEnable(Integer userId, Integer id) {
		Ret ret=toggleBoolean(id, "enable");
		if(ret.isOk()){
			//添加日志
			WechatMpinfo wechatMpinfo=ret.getAs("data");
			if(wechatMpinfo.getEnable()){
				Ret cRet=wechatConfigService.configOneMpinfo(wechatMpinfo);
				if(cRet.isFail()){
					wechatMpinfo.setEnable(false);
					wechatMpinfo.update();
					return cRet;
				}
			}else{
				wechatConfigService.removeOneEnable(wechatMpinfo);
			}
			addUpdateSystemLog(id, userId,SystemLog.TARGETTYPE_WECHAT_MPINFO, wechatMpinfo.getName(),"的启用状态:"+wechatMpinfo.getEnable());
		}
		return ret;
	}
	 
	/**
	 * 切换认证状态
	 * @param userId
	 * @param id
	 * @return
	 */
	public Ret toggleAuthenticated(Integer userId, Integer id) {
		Ret ret=toggleBoolean(id, "isAuthenticated");
		if(ret.isOk()){
			//添加日志
			WechatMpinfo wechatMpinfo=ret.getAs("data");
			addUpdateSystemLog(id, userId,SystemLog.TARGETTYPE_WECHAT_MPINFO, wechatMpinfo.getName(),"的认证状态:"+wechatMpinfo.getIsAuthenticated());
			return success(Msg.SUCCESS);
		}
		return fail(Msg.FAIL);
	}
	/**
	 * 后台管理分页读取
	 * @param pageNumber
	 * @param pageSize
	 * @param keywords
	 * @param enable
	 * @param type
	 * @param subjectType
	 * @param isAuthenticated
	 * @return
	 */
	public Page<WechatMpinfo> paginateAdminList(Integer pageNumber, int pageSize, String keywords, Boolean enable,
			Integer type, Integer subjectType, Boolean isAuthenticated) {
		Kv paras=Kv.create();
		paras.setIfNotBlank("keywords", keywords);
		paras.setIfNotNull("enable", enable);
		paras.setIfNotNull("isAuthenticated", isAuthenticated);
		paras.setIfNotNull("type", type);
		paras.set("table",table());
		paras.setIfNotNull("subjectType", subjectType);
		return dao().paginate(pageNumber, pageSize, Db.getSqlPara("wechat.mpinfo.paginateAdminList", paras));
	}
	/**
	 * 根据类型获取名称
	 * @param type
	 * @return
	 */
	public static String typeName(int type){
		String name="未指定";
		switch (type) {
		case WechatMpinfo.TYPE_DYH:
			name="订阅号";
			break;
		case WechatMpinfo.TYPE_FWH:
			name="服务号";
			break;
		case WechatMpinfo.TYPE_QYWX:
			name="企业微信";
			break;
		case WechatMpinfo.TYPE_XCX:
			name="小程序";
			break;
		}
		return name;
	}
	/**
	 * 根据主体类型获取名称
	 * @param type
	 * @return
	 */
	public static String subjectTypeName(int type){
		String name="未指定";
		switch (type) {
		case WechatMpinfo.SUBJECT_TYPE_PERSONAL:
			name="个人";
			break;
		case WechatMpinfo.SUBJECT_TYPE_INDIVIDUAL_BUSINESS:
			name="个体工商户";
			break;
		case WechatMpinfo.SUBJECT_TYPE_COMPANY:
			name="企业";
			break;
		case WechatMpinfo.SUBJECT_TYPE_MEDIA:
			name="媒体";
			break;
		case WechatMpinfo.SUBJECT_TYPE_ORG:
			name="组织社团";
			break;
		case WechatMpinfo.SUBJECT_TYPE_GOV:
			name="政府机关";
			break;
		case WechatMpinfo.SUBJECT_TYPE_GOV_SPONSORED_INSTITUTION:
			name="政府机关";
			break;
		}
		return name;
	}
	/**
	 * 获取所有启用的公众号
	 * @return
	 */
	public List<WechatMpinfo> getAllEnableList() {
		return getCommonList(Kv.by("enable", true));
	}
	/**
	 * 清空配置
	 * @param userId
	 * @param mpId
	 * @return
	 */
	public Ret clearAllConfigs(Integer userId, Integer mpId) {
		//公众平台配置删除
		wechatConfigService.deleteByMpId(mpId);
		//删除菜单
		wechatMenuService.deleteByMpId(mpId);
		//删除自动回复设置
		wechatAutoReplyService.deleteByMpId(mpId);
		return success(Msg.SUCCESS);
	}

}
