package cn.jbolt.wechat.autoreply;

import com.jfinal.weixin.sdk.jfinal.MsgControllerAdapter;
import com.jfinal.weixin.sdk.msg.in.InTextMsg;
import com.jfinal.weixin.sdk.msg.in.event.InFollowEvent;
import com.jfinal.weixin.sdk.msg.in.event.InMenuEvent;

/**   
 * 微信公众号被动消息处理
 * @ClassName:  WechatMsgController   
 * @author: JFinal学院-小木 QQ：909854136 
 * @date:   2019年5月14日 上午5:39:56   
 *    
 * 注意：本内容仅限于JFinal学院 JBolt平台VIP成员内部传阅，请尊重开发者劳动成果，不要外泄出去用于其它商业目的
 */
public class WechatMsgController extends MsgControllerAdapter {

	@Override
	protected void processInFollowEvent(InFollowEvent inFollowEvent) {
		if(inFollowEvent.getEvent().equals(InFollowEvent.EVENT_INFOLLOW_SUBSCRIBE)){
			renderOutTextMsg("感谢关注");
		}
	}
 

	@Override
	protected void processInTextMsg(InTextMsg inTextMsg) {
		renderOutTextMsg("文字");
	}

	@Override
	protected void processInMenuEvent(InMenuEvent inMenuEvent) {
		System.out.println("Event:"+inMenuEvent.getEvent());
		System.out.println("EventKey:"+inMenuEvent.getEventKey());
		System.out.println("公众号原始ID:"+inMenuEvent.getFromUserName());
		System.out.println("Event:"+inMenuEvent.getMsgType());
		renderOutTextMsg("菜单inMenuEvent.getEvent()");
	}
	

}
