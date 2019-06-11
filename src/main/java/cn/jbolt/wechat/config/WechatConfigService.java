package cn.jbolt.wechat.config;

import java.util.ArrayList;
import java.util.List;

import com.jfinal.aop.Inject;
import com.jfinal.kit.Kv;
import com.jfinal.kit.Ret;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.weixin.sdk.api.ApiConfig;
import com.jfinal.weixin.sdk.api.ApiConfigKit;
import com.jfinal.wxaapp.WxaConfig;
import com.jfinal.wxaapp.WxaConfigKit;

import cn.jbolt.base.BaseService;
import cn.jbolt.common.config.Msg;
import cn.jbolt.common.model.WechatConfig;
import cn.jbolt.common.model.WechatMpinfo;
import cn.jbolt.common.util.CACHE;
import cn.jbolt.wechat.mpinfo.WechatMpinfoService;

/**   
 * 微信配置管理Service
 * @ClassName:  WechatConfigService   
 * @author: JFinal学院-小木 QQ：909854136 
 * @date:   2019年5月8日 下午11:09:34   
 *    
 * 注意：本内容仅限于JFinal学院 JBolt平台VIP成员内部传阅，请尊重开发者劳动成果，不要外泄出去用于其它商业目的
 */
public class WechatConfigService extends BaseService<WechatConfig> {
	@Inject
	private WechatMpinfoService wechatMpinfoService;

	@Override
	protected WechatConfig dao() {
		return WechatConfig.dao;
	}
	/**
	 * 检测指定公众平台是否已经有配置了
	 * @param mpId
	 * @return
	 */
	public boolean checkWechatMpinfoInUse(Integer mpId) {
		return exists("mpId", mpId);
	}
	/**
	 * 根据配置类型 得到一个公众平台的配置
	 * @param mpId
	 * @param type
	 * @return
	 */
	public List<WechatConfig> getConfigList(Integer mpId,int type) {
		return getCommonList(Kv.by("mpId", mpId).set("type",type));
	}
	/**
	 * 得到一个公众平台的基础配置
	 * @param mpId
	 * @return
	 */
	public List<WechatConfig> getBaseConfigList(Integer mpId) {
		return getConfigList(mpId,WechatConfig.TYPE_BASE);
	}
	/**
	 * 得到一个公众平台的微信支付配置
	 * @param mpId
	 * @return
	 */
	public List<WechatConfig> getPayConfigList(Integer mpId) {
		return getConfigList(mpId,WechatConfig.TYPE_PAY);
	}
	/**
	 * 如果没有就初始化得到指定类型配置列表
	 * @param mpId
	 * @param type
	 * @return
	 */
	public List<WechatConfig> checkAndInitConfig(Integer mpId,int type) {
		WechatMpinfo mpinfo=wechatMpinfoService.findById(mpId);
		boolean isWxa=mpinfo.getType().intValue()==WechatMpinfo.TYPE_XCX;
		List<String> configKeys= getConfigKeyList(mpId,type);
		String[] keys=WechatConfigKey.getConfigKeys(type,isWxa);
		String[] names=WechatConfigKey.getConfigNames(type,isWxa);
		List<WechatConfig> configs=null;
		if(configKeys==null||configKeys.size()==0){
			configs=initConfigs(mpId,keys,names,type);
		}else{
			processConfigs(mpId,configKeys,keys,names,type,isWxa);
			configs=getConfigList(mpId, type);
		}
		return configs;
	}
	/**
	 * 获取公众平台指定类型的配置keys
	 * @param mpId
	 * @param type
	 * @return
	 */
	private List<String> getConfigKeyList(Integer mpId, int type) {
		return Db.query("select configKey from "+table()+" where mpId=? and type=?", mpId,type);
	}
	/**
	 * 已经设置的 和全部的对比出相差的数据后初始化
	 * @param configs
	 * @param keys
	 * @param names
	 * @return 
	 */
	private void processConfigs(Integer mpId,List<String> oldKeys, String[] keys, String[] names,int type,boolean isWxa) {
		for(int i=0;i<keys.length;i++){
			processOneConfig(mpId, names[i], keys[i],type, null);
		}
		boolean existAppId=oldKeys.contains(WechatConfigKey.APP_ID);
		boolean existDomainUrl=oldKeys.contains(WechatConfigKey.SERVER_DOMAIN_URL);
		if(existAppId&&existDomainUrl){
			updateConfigServerDomainUrl(mpId, type,isWxa);
		}
		
	}
	/**
	 * 更新一个公众平台的服务器URL配置
	 * @param mpId
	 * @param type
	 * @param isWxa
	 */
	private void updateConfigServerDomainUrl(Integer mpId, int type,boolean isWxa) {
		WechatConfig appIdConfig=findFirst(Kv.by("mpId", mpId).set("type",type).set("configKey",WechatConfigKey.APP_ID));
		WechatConfig urlConfig=findFirst(Kv.by("mpId", mpId).set("type",type).set("configKey",WechatConfigKey.SERVER_DOMAIN_URL));
		String url=null;
		if(isWxa){
			url=CACHE.me.getWechatWxaServerDomainRootUrl();
		}else{
			url=CACHE.me.getWechatMpServerDomainRootUrl();
		}
		if(StrKit.notBlank(url)&&StrKit.notBlank(appIdConfig.getConfigValue())){
			String result=url+"?appId="+appIdConfig.getConfigValue();
			if(result.equals(urlConfig.getConfigValue())==false){
				urlConfig.setConfigValue(result);
				urlConfig.update();
			}
		}
		
	}
	/**
	 * 初始化单个配置
	 * @param mpId
	 * @param name
	 * @param configKey
	 * @param type 
	 * @param value
	 */
	private WechatConfig processOneConfig(Integer mpId,String name,String configKey,int type, String value){
		WechatConfig config=findFirst(Kv.by("mpId", mpId).set("type",type).set("configKey",configKey));
		if(config==null){
			config=new WechatConfig();
			config.setConfigKey(configKey);
			if(isOk(value)){
				config.setConfigValue(value);
			}
			config.setMpId(mpId);
			config.setName(name);
			config.setType(type);
			config.save();
		}else{
			if(isOk(value)){
				config.setConfigValue(value);
				config.update();
			}
		}
		return config;
	}
	/**
	 * 初始化一个公众平台的配置里的所有项目
	 * @param mpId
	 * @return
	 */
	private List<WechatConfig> initConfigs(Integer mpId,String[] keys,String[] names,int type) {
		List<WechatConfig> configs=new ArrayList<WechatConfig>();
		WechatConfig config;
		for(int i=0;i<keys.length;i++){
			config=processOneConfig(mpId, names[i], keys[i],type, null);
			configs.add(config);
		}
		return configs;
	}
	/**
	 * 配置所有的公众平台 进入JFinal启动后的配置流程
	 */
	public void configAllEnable() {
		System.out.println("开始启动公众平台配置... start");
		List<WechatMpinfo> mps=wechatMpinfoService.getAllEnableList();
		if(mps!=null&&mps.size()>0){
			for(WechatMpinfo mp:mps){
				configOneMpinfo(mp);
			}
		}else{
			System.out.println("Warning:没有可用的公众平台配置");
		}
		System.out.println("所有公众平台启动完成... finish");
	}
	
	/**
	 * 启动单个
	 * @param mp
	 */
	public Ret configOneMpinfo(WechatMpinfo mp) {
		System.out.println("正在启动公众平台:"+mp.getName());
		boolean isWxa=mp.getType().intValue()==WechatMpinfo.TYPE_XCX;
		Kv kv=getMpinfoConfig(mp.getId(),WechatConfig.TYPE_BASE);
		if(kv==null){
			System.out.println("Warning:公众平台:"+mp.getName()+"--基础配置异常");
			return fail("Warning:公众平台:"+mp.getName()+"--基础配置异常");
		}
		if(kv.containsKey(WechatConfigKey.APP_ID)==false){
			System.out.println("Warning:公众平台:"+mp.getName()+"--基础配置-appId 异常");
			return fail("Warning:公众平台:"+mp.getName()+"--基础配置-appId 异常");
		}else{
			String app_id=kv.getStr(WechatConfigKey.APP_ID);
			if(StrKit.isBlank(app_id)){
				System.out.println("Warning:公众平台:"+mp.getName()+"--基础配置-appId null");
				return fail("Warning:公众平台:"+mp.getName()+"--基础配置-appId 异常");
			}
		}
		if(kv.containsKey(WechatConfigKey.APP_SECRET)==false){
			System.out.println("Warning:公众平台:"+mp.getName()+"--基础配置-appSecret 异常");
			return fail("Warning:公众平台:"+mp.getName()+"--基础配置-appSecret 异常");
		}else{
			String app_secret=kv.getStr(WechatConfigKey.APP_SECRET);
			if(StrKit.isBlank(app_secret)){
				System.out.println("Warning:公众平台:"+mp.getName()+"--基础配置-appSecret null");
				return fail("Warning:公众平台:"+mp.getName()+"--基础配置-appSecret 异常");
			}
		}
		if(kv.containsKey(WechatConfigKey.APP_GHID)==false){
			System.out.println("Warning:公众平台:"+mp.getName()+"--基础配置-原始ID  异常");
			return fail("Warning:公众平台:"+mp.getName()+"--基础配置-原始ID  异常");
		}else{
			String ghID=kv.getStr(WechatConfigKey.APP_GHID);
			if(StrKit.isBlank(ghID)){
				System.out.println("Warning:公众平台:"+mp.getName()+"--基础配置-原始ID  null");
				return fail("Warning:公众平台:"+mp.getName()+"--基础配置-原始ID  异常");
			}else if(ghID.indexOf("gh_")==-1){
				System.out.println("Warning:公众平台:"+mp.getName()+"--基础配置-原始ID  的格式 异常");
				return fail("Warning:公众平台:"+mp.getName()+"--基础配置-原始ID 的格式 异常");
			}
		}
		
		if(kv.containsKey(WechatConfigKey.SERVER_TOKEN)==false){
			System.out.println("Warning:公众平台:"+mp.getName()+"--基础配置-token 异常");
			return fail("Warning:公众平台:"+mp.getName()+"--基础配置-token 异常");
		}else{
			String server_token=kv.getStr(WechatConfigKey.SERVER_TOKEN);
			if(StrKit.isBlank(server_token)){
				System.out.println("Warning:公众平台:"+mp.getName()+"--基础配置-token null");
				return fail("Warning:公众平台:"+mp.getName()+"--基础配置-token 异常");
			}
		}
		String appId=kv.getStr(WechatConfigKey.APP_ID);
		String appSecret=kv.getStr(WechatConfigKey.APP_SECRET);
		String token=kv.getStr(WechatConfigKey.SERVER_TOKEN);
		String msgEncryptType=kv.getStr(WechatConfigKey.SERVER_MSG_ENCRYPT_TYPE);
		String aesKey=kv.getStr(WechatConfigKey.SERVER_ENCODINGAESKEY);
		if(isWxa){
			//小程序配置
			String format=kv.getStr(WechatConfigKey.SERVER_DATA_FORMAT);
			WxaConfig wxaConfig=new WxaConfig();
			// 配置微信 API 相关参数
			wxaConfig.setAppId(appId);
			wxaConfig.setAppSecret(appSecret);
			wxaConfig.setToken(token);

	        /**
	         *  是否对消息进行加密，对应于微信平台的消息加解密方式：
	         *  1：true进行加密且必须配置 encodingAesKey
	         *  2：false采用明文模式，同时也支持混合模式
	         */
	        
	        if(StrKit.notBlank(aesKey)){
	        	wxaConfig.setEncodingAesKey(aesKey);
	        }
	        if(msgEncryptType==null||msgEncryptType.equals("3")==false){
	        	wxaConfig.setMessageEncrypt(false);
	        }else if(msgEncryptType!=null&&msgEncryptType.equals("3")){
	        	wxaConfig.setMessageEncrypt(true);
	        }
	        
	        if(StrKit.notBlank(format)&&format.equals(WxaConfig.FORMAT_JSON)){
	        	wxaConfig.setFormat(WxaConfig.FORMAT_JSON);
	        }
	        
	        WxaConfigKit.putWxaConfig(wxaConfig);
			
		}else{
			ApiConfig ac = new ApiConfig();
	        // 配置微信 API 相关参数
	        ac.setAppId(appId);
	        ac.setAppSecret(appSecret);
	        ac.setToken(token);

	        /**
	         *  是否对消息进行加密，对应于微信平台的消息加解密方式：
	         *  1：true进行加密且必须配置 encodingAesKey
	         *  2：false采用明文模式，同时也支持混合模式
	         */
	        
	        if(StrKit.notBlank(aesKey)){
	        	ac.setEncodingAesKey(aesKey);
	        }
	        if(msgEncryptType==null||msgEncryptType.equals("3")==false){
	        	ac.setEncryptMessage(false);
	        }else if(msgEncryptType!=null&&msgEncryptType.equals("3")){
	        	ac.setEncryptMessage(true);
	        }
	        
	        ApiConfigKit.putApiConfig(ac);
		}
		
		System.out.println("公众平台:"+mp.getName()+"--启动完成");
		return success(Msg.SUCCESS);
	}
	/**
	 * 获取一个公众平台的配置信息
	 * @param id
	 * @param type
	 * @return
	 */
	private Kv getMpinfoConfig(Integer mpId,int type) {
		List<WechatConfig> configs=getConfigList(mpId, type);
		if(configs==null||configs.size()==0){return null;}
		Kv kv=Kv.create();
		for(WechatConfig config:configs){
			kv.set(config.getConfigKey(), config.getConfigValue());
		}
		return kv;
	}
	/**
	 * 更新配置信息
	 * @param userId
	 * @param mpId
	 * @param type
	 * @param configs
	 * @return
	 */
	public Ret updateConfigs(Integer userId, Integer mpId,int type, Boolean isWxa,List<WechatConfig> configs) {
		boolean checkSuccess=checkConfigs(mpId,type,configs);
		if(checkSuccess){
			Db.batchUpdate(configs, configs.size());
			if(type==WechatConfig.TYPE_BASE){
				updateConfigServerDomainUrl(mpId, type,isWxa);
			}
		}
		
		return checkSuccess?success(Msg.SUCCESS):fail(Msg.FAIL);
	}
	 
	private boolean checkConfigs(Integer mpId,int type, List<WechatConfig> configs) {
		for(WechatConfig config:configs){
			boolean exist=exist(mpId, type, config.getConfigKey(),config.getId());
			if(!exist){
				return false;
			}
		}
		return true;
	}
	
	public boolean exist(Integer mpId,int type,String configKey,Integer id){
		Integer idResult=Db.queryInt("select id from "+table()+" where mpId=? and type=? and configKey=?",mpId,type,configKey);
		return isOk(idResult)&&idResult.intValue()==id.intValue();
	}
	/**
	 * 删除一个公众平台的配置信息
	 * @param mpId
	 */
	public void deleteByMpId(Integer mpId) {
		deleteBy(Kv.by("mpId", mpId));
		
	}
	/**
	 * 删掉一个公众平台的配置
	 * @param wechatMpinfo
	 */
	public void removeOneEnable(WechatMpinfo wechatMpinfo) {
		WechatConfig appIdConfig=findFirst(Kv.by("mpId", wechatMpinfo.getId()).set("type",WechatConfig.TYPE_BASE).set("configKey",WechatConfigKey.APP_ID));
		if(appIdConfig!=null){
			ApiConfigKit.removeApiConfig(appIdConfig.getConfigValue());
		}
		
	}
	/**
	 * 获取一个公众平台的APPID
	 * @param mpId
	 * @return
	 */
	public String getWechatConfigAppId(Integer mpId) {
		WechatConfig appIdConfig=findFirst(Kv.by("mpId", mpId).set("type",WechatConfig.TYPE_BASE).set("configKey",WechatConfigKey.APP_ID));
		return appIdConfig==null?null:appIdConfig.getConfigValue();
	}
	 
	

}
