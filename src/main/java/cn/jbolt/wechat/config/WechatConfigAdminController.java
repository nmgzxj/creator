package cn.jbolt.wechat.config;

import java.util.ArrayList;
import java.util.List;

import com.jfinal.aop.Inject;

import cn.jbolt._admin.permission.CheckPermission;
import cn.jbolt._admin.permission.PermissionKey;
import cn.jbolt.base.BaseController;
import cn.jbolt.common.config.Msg;
import cn.jbolt.common.model.WechatConfig;
import cn.jbolt.common.model.WechatMpinfo;
import cn.jbolt.wechat.mpinfo.WechatMpinfoService;

/**   
 * 微信公众平台配置
 * @ClassName:  WechatConfigAdminController   
 * @author: JFinal学院-小木 QQ：909854136 
 * @date:   2019年5月12日 下午8:35:23   
 *    
 * 注意：本内容仅限于JFinal学院 JBolt平台VIP成员内部传阅，请尊重开发者劳动成果，不要外泄出去用于其它商业目的
 */

public class WechatConfigAdminController extends BaseController {
	@Inject
	private WechatMpinfoService wechatMpinfoService;
	@Inject
	private WechatConfigService service;
	@CheckPermission(PermissionKey.WECHAT_CONFIG_BASEMGR)
	public void baseMgr(){
		Integer mpId=getInt(0);
		List<WechatConfig> configs=service.checkAndInitConfig(mpId,WechatConfig.TYPE_BASE);
		set("configList",configs);
		set("configCount",configs.size());
		set("mpId", mpId);
		render("basemgr.html");
	}
	@CheckPermission(PermissionKey.WECHAT_CONFIG_BASEMGR)
	public void submitBaseConfig(){
		submitConfig(WechatConfig.TYPE_BASE);
		
	}
	@CheckPermission(PermissionKey.WECHAT_CONFIG_PAYMGR)
	public void payMgr(){
		Integer mpId=getInt(0);
		List<WechatConfig> configs=service.checkAndInitConfig(mpId,WechatConfig.TYPE_PAY);
		set("configList",configs);
		set("configCount",configs.size());
		set("mpId", mpId);
		render("paymgr.html");
	}
	private void submitConfig(int type){
		Integer configCount=getInt("configCount");
		if(notOk(configCount)){
			renderJsonFail(Msg.PARAM_ERROR);
		}else{
			Integer mpId=getInt("mpId");
			WechatMpinfo mpinfo=wechatMpinfoService.findById(mpId);
			if(mpinfo==null){
				renderJsonFail("微信公众平台信息不存在");
				return;
			}
			if(mpinfo.getEnable()!=null&&mpinfo.getEnable()==true){
				renderJsonFail("微信公众平台启用时无法更新配置信息，请在非启用状态下操作");
				return;
			}
			boolean isWxa=mpinfo.getType()==WechatMpinfo.TYPE_XCX;
			List<WechatConfig> configs=new ArrayList<WechatConfig>();
			for(int i=0;i<configCount;i++){
				configs.add(getModel(WechatConfig.class,"config["+i+"]"));
			}
			renderJson(service.updateConfigs(getSessionAdminUserId(),mpId,type,isWxa,configs));
		}
	}
	@CheckPermission(PermissionKey.WECHAT_CONFIG_PAYMGR)
	public void submitPayConfig(){
		submitConfig(WechatConfig.TYPE_PAY);
	}
}
