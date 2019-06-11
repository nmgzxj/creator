package cn.jbolt.index;

import com.jfinal.config.Routes;

import cn.jbolt._admin.interceptor.AdminAuthInterceptor;
import cn.jbolt.wechat.autoreply.WechatMsgController;
import cn.jbolt.wechat.config.WechatConfigAdminController;
import cn.jbolt.wechat.menu.WechatMenuAdminController;
import cn.jbolt.wechat.mpinfo.WechatMpinfoAdminController;
/**
 * admin后台 微信管理模块相关 的路由配置
 * @ClassName:  WechatAdminRoutes   
 * @author: JFinal学院-小木 QQ：909854136 
 * @date:   2019年5月8日15:03:24   
 *     
 * 注意：本内容仅限于JFinal学院 JBolt平台VIP成员内部传阅，请尊重开发者劳动成果，不要外泄出去用于其它商业目的
 */
public class WechatAdminRoutes extends Routes {

	@Override
	public void config() {
		this.setBaseViewPath("/_view/_admin/_wechat");
		this.addInterceptor(new PjaxInterceptor());
		this.addInterceptor(new AdminAuthInterceptor());
		this.add("/admin/wechat/mpinfo", WechatMpinfoAdminController.class,"/mpinfo");
		this.add("/admin/wechat/config", WechatConfigAdminController.class,"/config");
		this.add("/admin/wechat/menu", WechatMenuAdminController.class,"/menu");
	}

}
