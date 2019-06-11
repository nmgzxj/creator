package cn.jbolt._admin.globalconfig;

import java.util.Date;

import com.jfinal.kit.Kv;
import com.jfinal.kit.PropKit;
import com.jfinal.kit.Ret;

import cn.jbolt.base.BaseService;
import cn.jbolt.common.config.GlobalConfigKey;
import cn.jbolt.common.config.Msg;
import cn.jbolt.common.model.GlobalConfig;
import cn.jbolt.common.model.SystemLog;
import cn.jbolt.common.util.CACHE;

/**
 * 全局配置 service
 * 
 * @author 小木 qq:909854136
 * @version 创建时间：2018年12月25日 下午11:18:26
 */
public class GlobalConfigService extends BaseService<GlobalConfig> {

	@Override
	protected GlobalConfig dao() {
		return GlobalConfig.dao;
	}
	
	/**
	 * 更新
	 * @param userId
	 * @param globalConfig
	 * @return
	 */
	public Ret update(Integer userId, GlobalConfig globalConfig) {
		if (globalConfig == null || notOk(globalConfig.getId()) || notOk(globalConfig.getConfigKey())
				|| notOk(globalConfig.getConfigValue())) {
			return fail(Msg.PARAM_ERROR);
		}
		GlobalConfig db=findById(globalConfig.getId());
		if(db==null){return fail(Msg.DATA_NOT_EXIST);}
		db.setUpdateTime(new Date());
		db.setUpdateUserId(userId);
		db.setConfigValue(globalConfig.getConfigValue());
		boolean success = db.update();
		if (success) {
			//增加日志
			CACHE.me.removeGlobalConfig(db.getConfigKey());
			addUpdateSystemLog(db.getId(), userId, SystemLog.TARGETTYPE_GLOBAL_CONFIG, db.getName());
		}
		return success ? success(Msg.SUCCESS) : fail(Msg.FAIL);
	}
	/**
	 * 删除配置
	 * @param userId
	 * @param id
	 * @return
	 */
	public Ret deleteById(Integer userId, Integer id) {
		Ret ret=deleteById(id, true);
		if (ret.isOk()) {
			GlobalConfig globalConfig=ret.getAs("data");
			//增加日志
			CACHE.me.removeGlobalConfig(globalConfig.getConfigKey());
			addDeleteSystemLog(globalConfig.getId(), userId, SystemLog.TARGETTYPE_GLOBAL_CONFIG, globalConfig.getName());
		}
		return ret;
	}

 
/*
	private Ret save(Integer sessionAdminUserId, GlobalConfig globalConfig) {
		if (globalConfig == null || notOk(globalConfig.getId()) || notOk(globalConfig.getConfigKey())
				|| notOk(globalConfig.getConfigValue())) {
			return fail(Msg.PARAM_ERROR);
		}
		globalConfig.setCreateTime(new Date());
		globalConfig.setUserId(sessionAdminUserId);
		globalConfig.setUpdateTime(new Date());
		globalConfig.setUpdateUserId(sessionAdminUserId);
		boolean success = globalConfig.save();
		if (success) {
			// TODO 增加日志
		}
		return success ? success(Msg.SUCCESS) : fail(Msg.FAIL);
	}*/

	/**
	 * 检测和初始化配置
	 */
	public void checkAndInit(Integer userId) {
		checkAndInitConfig(userId, GlobalConfigKey.CONFIG_KEY_REWARD);
		checkAndInitConfig(userId, GlobalConfigKey.CONFIG_KEY_WECHAT_MP_SERVER_DOMAIN);
		checkAndInitConfig(userId, GlobalConfigKey.CONFIG_KEY_WECHAT_WXA_SERVER_DOMAIN);
		checkAndInitConfig(userId, GlobalConfigKey.CONFIG_KEY_SYSTEM_NAME);
		checkAndInitConfig(userId, GlobalConfigKey.CONFIG_KEY_SYSTEM_ADMIN_LOGO);
		checkAndInitConfig(userId, GlobalConfigKey.CONFIG_KEY_SYSTEM_COPYRIGHT_COMPANY);
		checkAndInitConfig(userId, GlobalConfigKey.CONFIG_KEY_SYSTEM_COPYRIGHT_LINK);
	}
	/**
	 * 检查并初始化全局配置表数据
	 * @param userId
	 * @param configKey
	 */
	private void checkAndInitConfig(Integer userId, String configKey) {
		boolean checkExist = exists("configKey", configKey);
		if (checkExist == false) {
			GlobalConfig config = new GlobalConfig();
			config.setConfigKey(configKey);
			switch (configKey) {
			case GlobalConfigKey.CONFIG_KEY_REWARD:
				config.setName("捐助金额");
				config.setConfigValue("10");
				break;
			case GlobalConfigKey.CONFIG_KEY_WECHAT_MP_SERVER_DOMAIN:
				config.setName("微信公众号_服务器配置_根URL");
				config.setConfigValue(PropKit.get("domain")+"/wx/msg");
				break;
			case GlobalConfigKey.CONFIG_KEY_WECHAT_WXA_SERVER_DOMAIN:
				config.setName("微信小程序_客服消息推送配置_根URL");
				config.setConfigValue(PropKit.get("domain")+"/wxa/msg");
				break;
			case GlobalConfigKey.CONFIG_KEY_SYSTEM_NAME:
				config.setName("系统名称");
				config.setConfigValue("JBolt开发平台");
				break;
			case GlobalConfigKey.CONFIG_KEY_SYSTEM_ADMIN_LOGO:
				config.setName("系统后台主页LOGO");
				config.setConfigValue("/assets/img/logo.png");
				break;
			case GlobalConfigKey.CONFIG_KEY_SYSTEM_COPYRIGHT_COMPANY:
				config.setName("系统版权所有人");
				config.setConfigValue("©JBolt(JBOLT.CN)");
				break;
			case GlobalConfigKey.CONFIG_KEY_SYSTEM_COPYRIGHT_LINK:
				config.setName("系统版权所有人的网址链接");
				config.setConfigValue("http://jbolt.cn");
				break;
			}
			config.setCreateTime(new Date());
			config.setUserId(userId);
			config.setUpdateTime(new Date());
			config.setUpdateUserId(userId);
			config.save();
		}

	}
	/**
	 * 根据configKey获取全局配置
	 * @param configKey
	 * @return
	 */
	public GlobalConfig findByConfigKey(String configKey) {
		return findFirst(Kv.by("configKey", configKey));
	}


	
	
}
