package cn.jbolt._admin.interceptor;

import java.lang.reflect.Method;
import java.util.List;

import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.jfinal.kit.StrKit;

import cn.jbolt._admin.permission.CheckPermission;
import cn.jbolt.base.BaseController;
import cn.jbolt.common.model.Permission;
import cn.jbolt.common.util.CACHE;



public class AdminAuthInterceptor implements Interceptor {
	private static final ThreadLocal<Integer> threadLocal = new ThreadLocal<Integer>();
	public static Integer getThreadLocalAdminUserId() {
		return threadLocal.get();
	}
	@Override
	public void intercept(Invocation inv) {
		BaseController controller = (BaseController) inv.getController();
		boolean isAdminLogin = controller.isAdminLogin();
		if (!isAdminLogin) {
			if (controller.isPjax()) {
				controller.renderErrorPjax("尚未登录");
			} else if (controller.isAjax()) {
				controller.renderJsonFail("尚未登录");
			} else {
				// 判断如果没有登录 需要跳转到登录页面
				controller.toLogin();
			}
			return;
		}
		if(SecurityCheck.isUncheck(inv.getMethod())){
			inv.invoke();
			return;
		}
		//拿到登录用户所分配的角色
		String roleIds = controller.getSessionAdminRoleIds();
		//从cache中找到这些角色对应的权限绑定集合
		List<Permission> permissions = CACHE.me.getRolePermissions(roleIds);
		if (permissions == null || permissions.isEmpty()) {
			// 如果没有权限 返回错误信息
			renderInterceptorErrorInfo(controller,"尚未分配任何权限");
			return;
		}
		// 获取controllerKey 然后拿到本人的role对应的permissions
		String[] permissionKeys = getPermissionKeys(controller, inv.getMethod());
		if(permissionKeys==null||permissionKeys.length==0){
			// 如果没有权限 返回错误信息
			renderInterceptorErrorInfo(controller,"开发未设置校验权限");
			return;
		}
		//检测拦截到正在访问的controller+action上需要校验的权限资源 拿到后去跟缓存里当前用户所在的角色下的所有资源区对比
		boolean exist = checkAuth(permissions, permissionKeys);
		if (!exist) {
			// 如果没有权限 返回错误信息
			renderInterceptorErrorInfo(controller,"无权访问");
			return;
		}
		// 最后执行action
		inv.invoke();

	}

	private void renderInterceptorErrorInfo(BaseController controller,String msg) {
		if (controller.isPjax()) {
			controller.renderErrorPjax(msg);
		} else if (controller.isAjax()) {
			controller.renderJsonFail(msg);
		} else {
			controller.renderFormError(msg);
		}
		
	}

	private String[] getPermissionKeys(BaseController controller, Method method) {
		boolean mc=SecurityCheck.isPermissionCheck(method);
		boolean cc=SecurityCheck.isPermissionCheck(controller);
		if(!mc&&!cc){
			return null;
		}
		String[] temps=null;
		if(mc){
			CheckPermission per = method.getAnnotation(CheckPermission.class);
			String[] values = per.value();
			if (values == null || values.length == 0) {
				return null;
			}
			temps=values;
		}
		if(cc&&temps==null){
			CheckPermission per = controller.getClass().getAnnotation(CheckPermission.class);
			String[] values = per.value();
			if (values == null || values.length == 0) {
				return null;
			}
			temps=values;
		}
		
		return temps;
	}

	private boolean checkAuth(List<Permission> permissions, String[] keys) {
		boolean success = false;
		for (String key : keys) {
		/*	if (key.equals(Permission.ONLYLOGIN)) {
				success = true;
				break;
			}*/
			for (Permission f : permissions) {
				if (StrKit.notBlank(f.getPermissionKey())&&f.getPermissionKey().equals(key)) {
					success = true;
					break;
				}
			}
		}
		return success;
	}

}
