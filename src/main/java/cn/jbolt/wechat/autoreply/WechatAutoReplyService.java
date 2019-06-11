package cn.jbolt.wechat.autoreply;

import com.jfinal.aop.Inject;
import com.jfinal.kit.Kv;

import cn.jbolt.base.BaseService;
import cn.jbolt.common.model.WechatAutoreply;
import cn.jbolt.common.model.WechatKeywords;

/**   
 * 微信公众号回复规则
 * @ClassName:  WechatAutoReplyService   
 * @author: JFinal学院-小木 QQ：909854136 
 * @date:   2019年5月8日 下午11:58:43   
 *    
 * 注意：本内容仅限于JFinal学院 JBolt平台VIP成员内部传阅，请尊重开发者劳动成果，不要外泄出去用于其它商业目的
 */
public class WechatAutoReplyService extends BaseService<WechatAutoreply> {
	@Inject
	private WechatKeywordsService wechatKeywordsService;
	@Inject
	private WechatReplyContentService wechatReplyContentService;

	@Override
	protected WechatAutoreply dao() {
		return WechatAutoreply.dao;
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
	 * 删除指定公众平台的自动回复模块的配置信息
	 * @param mpId
	 */
	public void deleteByMpId(Integer mpId) {
		//关键词
		wechatKeywordsService.deleteByMpId(mpId);
		//回复
		wechatReplyContentService.deleteByMpId(mpId);
		//自身
		deleteBy(Kv.by("mpId", mpId));
		
	}

}
