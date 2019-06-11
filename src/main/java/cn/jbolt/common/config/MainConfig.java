package cn.jbolt.common.config;

import java.io.File;
import java.sql.Timestamp;
import java.util.UUID;

import cn.jbolt.index.*;
import com.alibaba.druid.filter.stat.StatFilter;
import com.alibaba.druid.wall.WallFilter;
import com.jfinal.aop.Aop;
import com.jfinal.config.Constants;
import com.jfinal.config.Handlers;
import com.jfinal.config.Interceptors;
import com.jfinal.config.JFinalConfig;
import com.jfinal.config.Plugins;
import com.jfinal.config.Routes;
import com.jfinal.core.converter.TypeConverter;
import com.jfinal.ext.interceptor.SessionInViewInterceptor;
import com.jfinal.json.FastJsonFactory;
import com.jfinal.kit.FileKit;
import com.jfinal.kit.Prop;
import com.jfinal.kit.PropKit;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.ActiveRecordPlugin;
import com.jfinal.plugin.activerecord.dialect.MysqlDialect;
import com.jfinal.plugin.druid.DruidPlugin;
import com.jfinal.plugin.ehcache.EhCachePlugin;
import com.jfinal.render.ViewType;
import com.jfinal.server.undertow.UndertowServer;
import com.jfinal.template.Engine;
import com.jfinal.template.source.ClassPathSourceFactory;
import com.jfinal.upload.OreillyCos;
import com.jfinal.weixin.sdk.api.ApiConfigKit;
import com.jfinal.wxaapp.WxaConfigKit;
import com.oreilly.servlet.multipart.FileRenamePolicy;

import cn.hutool.core.io.FileUtil;
import cn.jbolt._admin.permission.PermissionKey;
import cn.jbolt._admin.user.UserAuthKit;
import cn.jbolt.base.BaseHandler;
import cn.jbolt.common.directive.AjaxPortalDirective;
import cn.jbolt.common.directive.DateTimeDirective;
import cn.jbolt.common.directive.GlobalConfigDirective;
import cn.jbolt.common.directive.PermissionDirective;
import cn.jbolt.common.directive.RealImageDirective;
import cn.jbolt.common.directive.SqlValueDirective;
import cn.jbolt.common.model._MappingKit;
import cn.jbolt.common.safe.XssHandler;
import cn.jbolt.common.util.CACHE;
import cn.jbolt.common.util.DateUtil;
import cn.jbolt.wechat.config.WechatConfigService;

public class MainConfig extends JFinalConfig {
	//定义项目部署环境是不是始终保持https
	public static Boolean NEED_ALWAYS_HTTPS=false;
	//上传文件保存路径的前缀 默认为空 项目下存 线上 可能会存到项目之外的目录里
	public static String BASEUPLOADPATH_PRE=null;
	/**
	 * 配置JFinal常量
	 */
	@Override
	public void configConstant(Constants me) {
		//读取数据库配置文件
		PropKit.use("config.properties").appendIfExists("config-pro.properties");
		NEED_ALWAYS_HTTPS=PropKit.getBoolean("need_always_https",false);
		//设置当前是否为开发模式
		me.setDevMode(PropKit.getBoolean("devMode"));
		//设置默认上传文件保存路径 getFile等使用
		me.setBaseUploadPath(PropKit.get("baseuploadpath"));
		BASEUPLOADPATH_PRE=PropKit.get("baseuploadpath_pre");
		//设置上传最大限制尺寸
		me.setMaxPostSize(1024*1024*20);
		//设置是否对超类进行注入
		me.setInjectSuperClass(true);
		//设置默认下载文件路径 renderFile使用
		me.setBaseDownloadPath("download");
		//设置默认视图类型
		me.setViewType(ViewType.JFINAL_TEMPLATE);
		//设置404渲染视图
		me.setError404View("/_view/_admin/common/msg/404.html");
		//开启自动注入
		me.setInjectDependency(true);
		//设置json工厂
		me.setJsonFactory(FastJsonFactory.me());
		OreillyCos.setFileRenamePolicy(new FileRenamePolicy() {
			@Override
			public File rename(File file) {
				String path=file.getPath();
				String ext=FileKit.getFileExtension(path);
				String name=FileUtil.getName(path);
				if(StrKit.isBlank(ext)&&name.equals("blob")){
					ext="png";
				}
				return new File(file.getParent(), UUID.randomUUID()+"."+ext);
			}
		});
		
		//单独处理数据库内字段是datetime类型的时候 页面使用了Html5组件的时间选择组件 type="datetime-local"的
		TypeConverter.me().regist(Timestamp.class, new JBoltTimestampConverter());
	}
	
	/**
	 * 配置JFinal路由映射
	 */
	@Override
	public void configRoute(Routes me) {
		//后台管理 主模块路由配置
		me.add(new AdminRoutes());
		//后台管理 电商模块路由配置
		me.add(new MallAdminRoutes());
		//后台管理 微信模块路由配置
		me.add(new WechatAdminRoutes());
		//微信服务器与本服务通讯使用的前端路由
		me.add(new WechatRoutes());
		me.add(new WebRoutes());
		//demo使用 正式上线请删掉
		me.add(new DemoRoutes());
		//data使用
		me.add(new DataRoutes());
	}
	/**
	 * 配置JFinal插件
	 * 数据库连接池
	 * ORM
	 * 缓存等插件
	 * 自定义插件
	 */
	@Override
	public void configPlugin(Plugins me) {
		//配置数据库连接池插件
		DruidPlugin dbPlugin=new DruidPlugin(PropKit.get("jdbcUrl"), PropKit.get("user"), PropKit.get("password"));
		WallFilter wallFilter = new WallFilter();              // 加强数据库安全
	    wallFilter.setDbType("mysql");
	    dbPlugin.addFilter(wallFilter);
	    dbPlugin.addFilter(new StatFilter());    // 添加 StatFilter 才会有统计数据
	    me.add(dbPlugin);
		//orm映射 配置ActiveRecord插件
		ActiveRecordPlugin arp=new ActiveRecordPlugin(dbPlugin);
		arp.setShowSql(PropKit.getBoolean("devMode"));
		arp.setDialect(new MysqlDialect());
		/********在此添加数据库 表-Model 映射*********/
		_MappingKit.mapping(arp);

		arp.getEngine().setSourceFactory(new ClassPathSourceFactory());
		arp.getEngine().addDirective("sqlValue", SqlValueDirective.class);
        arp.addSqlTemplate("/sql/all_sqls.sql");
        arp.getEngine().setDevMode(PropKit.getBoolean("dbsqlengine_devmode", false));
		//添加到插件列表中
		me.add(dbPlugin);
		me.add(arp);
		me.add(new EhCachePlugin());
		
		//调度
	    /*Cron4jPlugin cron4jPlugin = new Cron4jPlugin();
	    cron4jPlugin.addTask("0-59/30 * * * *", new RobotTask());
	    cron4jPlugin.addTask("0-59/1 * * * *", new ElasticTask());
	    me.add(cron4jPlugin);*/
	}
	/**
	 * 配置全局拦截器
	 */
	@Override
	public void configInterceptor(Interceptors me) {
		me.addGlobalActionInterceptor(new SessionInViewInterceptor());
	}
	/**
	 * 配置全局处理器
	 */
	@Override
	public void configHandler(Handlers me) {
		me.add(new BaseHandler());
		//配置xss攻击 处理器
		me.add(new XssHandler());
	}
	
	/**
	 * 配置模板引擎 
	 */
	@Override
	public void configEngine(Engine me) {
		//这里只有选择JFinal TPL的时候才用
		//配置共享函数模板
		me.addDirective("ajaxPortal", AjaxPortalDirective.class);
		me.addDirective("realImage", RealImageDirective.class);
		me.addDirective("datetime", DateTimeDirective.class);
		me.addDirective("permission", PermissionDirective.class);
		me.addDirective("globalConfig", GlobalConfigDirective.class);		
		//后台主pjax加载结构layout
		me.addSharedFunction("/_view/_admin/common/__admin_layout.html");
		//后台所有Dialog的表单和列表管理类 都是用这个layout
		me.addSharedFunction("/_view/_admin/common/__admin_dialog_layout.html");
		me.addSharedObject("DateUtil", new DateUtil());
		//添加CACHE访问
		me.addSharedObject("CACHE", CACHE.me);
		//添加角色、权限 静态方法
		me.addSharedStaticMethod(UserAuthKit.class);
		//添加sessionKey的访问
		me.addSharedObject("SessionKey", new SessionKey());
		//添加GlobalConfig的访问
		me.addSharedObject("GlobalConfigKey", new GlobalConfigKey());
		//添加PermissionKey的访问
		me.addSharedObject("PermissionKey", new PermissionKey());
		
	}
	
	public static void main(String[] args) {
		UndertowServer.create(MainConfig.class,"undertow.properties").start();
	}

	@Override
	public void onStart() {
		//配置微信公众平台
		configWechat();
	}
	/**
	 * 配置微信公众平台
	 */
	private void configWechat() {
		ApiConfigKit.setDevMode(PropKit.getBoolean("wechat_devMode",false));
		WxaConfigKit.setDevMode(PropKit.getBoolean("wechat_devMode",false));
		WechatConfigService wechatConfigService=Aop.get(WechatConfigService.class);
		wechatConfigService.configAllEnable();
	}


	private static Prop p = PropKit.use("config.properties").appendIfExists("config-pro.properties");

	public static DruidPlugin getDruidPlugin() {
		return new DruidPlugin(p.get("jdbcUrl"), p.get("user"), p.get("password").trim());
	}

}
