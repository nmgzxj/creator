package cn.jbolt.wechat.autoreply;

import com.jfinal.kit.Kv;

import cn.jbolt.base.BaseService;
import cn.jbolt.common.model.WechatKeywords;

/**  
 * 微信自定义菜单配置 
 * @ClassName:  WechatKeywordsService   
 * @author: JFinal学院-小木 QQ：909854136 
 * @date:   2019年5月14日 上午4:31:49   
 *    
 * 注意：本内容仅限于JFinal学院 JBolt平台VIP成员内部传阅，请尊重开发者劳动成果，不要外泄出去用于其它商业目的
 */
public class WechatKeywordsService extends BaseService<WechatKeywords> {

	@Override
	protected WechatKeywords dao() {
		return WechatKeywords.dao;
	}
	/**
	 * 删除指定公众平台的自定义菜单数据
	 * @param mpId
	 */
	public void deleteByMpId(Integer mpId) {
		deleteBy(Kv.by("mpId", mpId));
	}

}
