package cn.jbolt.common.gen;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.List;

import javax.sql.DataSource;

import com.jfinal.aop.Aop;
import com.jfinal.kit.Kv;
import com.jfinal.kit.Prop;
import com.jfinal.kit.PropKit;
import com.jfinal.plugin.activerecord.ActiveRecordPlugin;
import com.jfinal.plugin.druid.DruidPlugin;
import com.jfinal.template.Engine;
import com.jfinal.template.Template;

import cn.hutool.core.io.FileUtil;
import cn.jbolt._admin.permission.PermissionService;
import cn.jbolt.common.model.Permission;

/**
 * 本系统中资源权限表里定义的资源 
 * 快捷生成静态常量到PermissionKey.java文件中，
 * 方便其他地方统一调用
 */
public class PermissionKeyGen {
	/**
	 * PermissionKey.java的绝对路径
	 */
	private static final String TARGET="//Users//zhangxianjin//IdeaProjects//creator//src//main//java//cn//jbolt//_admin//permission//PermissionKey.java";
	private static final String TPL="//Users//zhangxianjin//IdeaProjects//creator//src//main//java//cn//jbolt//common//gen//permissionkey.tpl";

	public static DataSource getDataSource() {
		Prop p = PropKit.use("config.properties").appendIfExists("config-pro.properties");
		DruidPlugin druidPlugin = new DruidPlugin(p.get("jdbcUrl"), p.get("user"), p.get("password"));
		druidPlugin.start();
		return druidPlugin.getDataSource();
	}

	public static void main(String[] args) {
		ActiveRecordPlugin activeRecordPlugin = new ActiveRecordPlugin(getDataSource());
		activeRecordPlugin.addMapping("permission", Permission.class);
		activeRecordPlugin.start();
		PermissionService service = Aop.get(PermissionService.class);
		List<Permission> permissions = service.findAll();
		Template template=Engine.use().getTemplate(TPL);
		BufferedWriter writer=FileUtil.getWriter(TARGET, "utf-8", false);
		try {
			writer.write(template.renderToString(Kv.by("permissions", permissions)));
			writer.flush();
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}finally {
			if(writer!=null){
				try {
					writer.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				}
		}
	}
}
