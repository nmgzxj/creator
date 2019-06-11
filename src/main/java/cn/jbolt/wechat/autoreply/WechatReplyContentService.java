package cn.jbolt.wechat.autoreply;

import com.jfinal.kit.Kv;

import cn.jbolt.base.BaseService;
import cn.jbolt.common.model.WechatReplyContent;

/**   
 * 自动回复内容管理Service
 * @ClassName:  WechatReplyContnet   
 * @author: JFinal学院-小木 QQ：909854136 
 * @date:   2019年5月14日 上午4:49:53   
 *    
 * 注意：本内容仅限于JFinal学院 JBolt平台VIP成员内部传阅，请尊重开发者劳动成果，不要外泄出去用于其它商业目的
 */
public class WechatReplyContentService extends BaseService<WechatReplyContent> {

	@Override
	protected WechatReplyContent dao() {
		return WechatReplyContent.dao;
	}

	public void deleteByMpId(Integer mpId) {
		deleteBy(Kv.by("mpId", mpId));
	}

}
