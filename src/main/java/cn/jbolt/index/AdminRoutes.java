package cn.jbolt.index;

import com.jfinal.config.Routes;

import cn.jbolt._admin.dictionary.DictionaryAdminController;
import cn.jbolt._admin.dictionary.DictionaryTypeAdminController;
import cn.jbolt._admin.globalconfig.GlobalConfigAdminController;
import cn.jbolt._admin.interceptor.AdminAuthInterceptor;
import cn.jbolt._admin.permission.PermissionAdminController;
import cn.jbolt._admin.role.RoleAdminCotroller;
import cn.jbolt._admin.rolepermission.RolePermissionAdminController;
import cn.jbolt._admin.systemlog.SystemLogAdminController;
import cn.jbolt._admin.updatemgr.DownloadLogAdminController;
import cn.jbolt._admin.updatemgr.JBoltVersionAdminController;
import cn.jbolt._admin.updatemgr.UpdateLibsAdminController;
import cn.jbolt._admin.user.UserAdminCotroller;
/**
 * admin后台的路由配置
 * @ClassName:  AdminRoutes   
 * @author: JFinal学院-小木 QQ：909854136 
 * @date:   2019年3月26日 下午12:25:20   
 *     
 * 注意：本内容仅限于JFinal学院 JBolt平台VIP成员内部传阅，请尊重开发者劳动成果，不要外泄出去用于其它商业目的
 */
public class AdminRoutes extends Routes {

	@Override
	public void config() {
		this.setBaseViewPath("/_view/_admin");
		this.addInterceptor(new PjaxInterceptor());
		this.addInterceptor(new AdminAuthInterceptor());
		this.add("/admin", AdminIndexController.class,"/index");
		this.add("/admin/user", UserAdminCotroller.class,"/user");
		this.add("/admin/role", RoleAdminCotroller.class,"/role");
		this.add("/admin/dictionarytype", DictionaryTypeAdminController.class,"/dictionary/type");
		this.add("/admin/dictionary", DictionaryAdminController.class,"/dictionary");
		this.add("/admin/permission", PermissionAdminController.class,"/permission");
		this.add("/admin/rolepermission", RolePermissionAdminController.class,"/rolepermission");
		this.add("/admin/systemlog", SystemLogAdminController.class,"/systemlog");
		this.add("/admin/globalconfig", GlobalConfigAdminController.class,"/globalconfig");
		this.add("/admin/jboltversion", JBoltVersionAdminController.class,"/jboltversion");
		this.add("/admin/updatelibs", UpdateLibsAdminController.class,"/updatelibs");
		this.add("/admin/downloadlog", DownloadLogAdminController.class,"/downloadlog");
	}

}
