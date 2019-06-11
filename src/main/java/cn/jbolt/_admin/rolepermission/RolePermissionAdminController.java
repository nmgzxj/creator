package cn.jbolt._admin.rolepermission;

import java.util.List;

import com.jfinal.aop.Inject;

import cn.jbolt._admin.permission.CheckPermission;
import cn.jbolt._admin.permission.PermissionKey;
import cn.jbolt._admin.permission.PermissionService;
import cn.jbolt.base.BaseController;
import cn.jbolt.common.model.Permission;
@CheckPermission(PermissionKey.ROLE)
public class RolePermissionAdminController extends BaseController {
	@Inject
	private RolePermissionService service;
	@Inject
	private PermissionService permissionService;
	/**
	 * 进入角色分配资源的界面
	 */
	public void setting(){
		Integer roleId=getInt(0);
		List<Permission> permissions=permissionService.getAllPermissionsWithLevel();
		set("dataList", permissions);
		set("roleId", roleId);
		render("setting.html");
	}
	/**
	 * 提交角色分配资源变更
	 */
	public void submit(){
		Integer roleId=getInt("roleId");
		String permissionStr=get("permissions");
		renderJson(service.doSubmit(getSessionAdminUserId(),roleId,permissionStr));
	}
	/**
	 * 删除一个角色的所有绑定资源
	 */
	public void deleteRolePermissions(){
		renderJson(service.deleteRolePermission(getInt(0)));
	}
	/**
	 *  获取角色已经设置的资源
	 */
	public void getCheckeds(){
		renderJsonData(service.getListByRole(getInt(0)));
	}
}
