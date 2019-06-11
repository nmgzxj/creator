package cn.jbolt._admin.role;

import com.jfinal.aop.Inject;

import cn.jbolt._admin.permission.CheckPermission;
import cn.jbolt._admin.permission.PermissionKey;
import cn.jbolt._admin.permission.UnCheck;
import cn.jbolt.base.BaseController;
import cn.jbolt.common.model.Role;
@CheckPermission(PermissionKey.ROLE)
public class RoleAdminCotroller extends BaseController {
	@Inject
	private RoleService service;
	/**
	 * 管理首页
	 */
	public void index(){
		set("dataList", service.findAll());
		render("index.html");
	}
	@UnCheck
	public void select(){
		renderJsonData(service.getOptionList());
	}
	
	
	/**
	 * 新增
	 */
	public void add(){
		render("add.html");
	}
	/**
	 * 编辑
	 */
	public void edit(){
		set("role", service.findById(getInt(0)));
		render("edit.html");
	}
	/**
	 * 保存
	 */
	public void save(){
		renderJson(service.save(getSessionAdminUserId(),getModel(Role.class, "role")));
	}
	/**
	 * 更新
	 */
	public void update(){
		renderJson(service.update(getSessionAdminUserId(),getModel(Role.class, "role")));
	}
	/**
	 * 删除
	 */
	public void delete(){
		renderJson(service.delete(getSessionAdminUserId(),getInt()));
	}
}
