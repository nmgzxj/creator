package cn.jbolt._admin.rolepermission;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.swing.text.StyledEditorKit.BoldAction;

import com.jfinal.aop.Inject;
import com.jfinal.kit.Kv;
import com.jfinal.kit.Ret;
import com.jfinal.plugin.activerecord.Db;

import cn.jbolt._admin.permission.PermissionService;
import cn.jbolt._admin.role.RoleService;
import cn.jbolt._admin.systemlog.SystemLogService;
import cn.jbolt.base.BaseService;
import cn.jbolt.common.config.Msg;
import cn.jbolt.common.model.Permission;
import cn.jbolt.common.model.Role;
import cn.jbolt.common.model.RolePermission;
import cn.jbolt.common.model.SystemLog;
import cn.jbolt.common.model.User;
import cn.jbolt.common.util.ArrayUtil;
import cn.jbolt.common.util.CACHE;

public class RolePermissionService extends BaseService<RolePermission> {
	@Inject
	private PermissionService permissionService;
	@Inject
	private RoleService roleService;
	@Inject
	private SystemLogService systeLogService;
	@Override
	protected RolePermission dao() {
		return RolePermission.dao;
	}
	/**
	 * 删除一个角色下的权限资源
	 * @param roleId
	 * @return
	 */
	public Ret deleteRolePermission(Integer roleId) {
		if(notOk(roleId)){
			return fail("");
		}
		deleteBy(Kv.by("roleId", roleId));
		//删除缓存
		CACHE.me.removeMenusAndPermissionsByRoleGroups();
		return success(Msg.SUCCESS);
	}
	/**
	 * 处理角色变动资源
	 * @param userId
	 * @param roleId
	 * @param permissionStr
	 * @return
	 */
	public Ret doSubmit(Integer userId,Integer roleId, String permissionStr) {
		if(notOk(roleId)||notOk(permissionStr)){
			return fail("参数有误");
		}
		Role role=roleService.findById(roleId);
		if(role==null){
			return fail("角色信息不存在");
		}
		String ids[]=ArrayUtil.from(permissionStr, ",");
		if(ids==null||ids.length==0){
			return fail("请选择分配的权限");
		}
		//先删除以前的
		deleteRolePermission(roleId);
		//添加现在的
		saveRolePermissions(roleId,ids);
		//添加日志
		addUpdateSystemLog(roleId, userId, SystemLog.TARGETTYPE_ROLE, role.getName(), "的可用权限设置");
		return SUCCESS;
	}
	
	/**
	 * 保存一个角色分配的所有资源信息
	 * @param roleId
	 * @param ids
	 */
	private void saveRolePermissions(Integer roleId, String[] ids) {
		List<RolePermission> permissions=new ArrayList<RolePermission>();
		RolePermission rolePermission=null;
		for(String id:ids){
			rolePermission=new RolePermission();
			rolePermission.setRoleId(roleId);
			rolePermission.setPermissionId(Integer.parseInt(id));
			permissions.add(rolePermission);
		}
		Db.batchSave(permissions, permissions.size());
	}
	/**
	 * 获取指定多个角色的所有权限资源
	 * @param roleIds
	 * @return
	 */
	public List<Permission> getPermissionsByRoles(String roleIds) {
		List<RolePermission> rolePermissions=getListByRoles(roleIds);
		if(rolePermissions==null||rolePermissions.size()==0){
			return null;
		}
		List<Permission> permissions=new ArrayList<Permission>();
		for(RolePermission rf:rolePermissions){
			Permission f=CACHE.me.getPermission(rf.getPermissionId());
			if(f!=null){
				permissions.add(f);
			}
		}
		return permissions;
	}
	/**
	 * 获取一个角色下的分配的权限资源数据
	 * @param roleId
	 * @return
	 */
	public List<RolePermission> getListByRole(Integer roleId) {
		return getCommonList(Kv.by("roleId", roleId));
	}
	
	/**
	 * 获取多个角色下分配的权限资源数据
	 * @param roleIds
	 * @return
	 */
	public List<RolePermission> getListByRoles(String roleIds) {
		return getCommonList("distinct permissionId", Kv.by("roleId in("+roleIds+")",""),true);
	}
	
	
	/**
	 * 根据角色获取左侧菜单导航
	 * @param roleId
	 * @return
	 */
	public List<Permission> getMenusByRole(Integer roleId) {
		List<RolePermission> rolePermissions=getListByRole(roleId);
		if(rolePermissions==null||rolePermissions.size()==0){
			return null;
		}
		
		return processMenusByPermissions(rolePermissions);
	
	}
	
	
	/**
	 * 根据指定的多个角色，返回合并后的所有菜单资源
	 * @param roleIds
	 * @return
	 */
	public List<Permission> getMenusByRoles(String roleIds) {
		List<RolePermission> rolePermissions=getListByRoles(roleIds);
		if(rolePermissions==null||rolePermissions.size()==0){
			return null;
		}
		
		return processMenusByPermissions(rolePermissions);
	
	}
	/**
	 * 处理抽取左侧导航菜单部分的权限
	 * @param rolePermissions
	 * @return
	 */
	private List<Permission> processMenusByPermissions(List<RolePermission> rolePermissions) {
		List<Permission> level1permissions=new ArrayList<>();
		List<Permission> level2permissions=new ArrayList<>();
		for(RolePermission rf:rolePermissions){
			Permission f=CACHE.me.getPermission(rf.getPermissionId());
			if(f!=null&&f.getIsMenu()){
				if(f.getLevel()==Permission.LEVEL_1){
					level1permissions.add(f);
				}else if(f.getLevel()==Permission.LEVEL_2){
					level2permissions.add(f);
				}
			}
		}
		Collections.sort(level1permissions, new Comparator<Permission>() {
			
			@Override
			public int compare(Permission o1, Permission o2) {
				return o1.getSortRank()-o2.getSortRank();
			}
		});
		Collections.sort(level2permissions, new Comparator<Permission>() {

			@Override
			public int compare(Permission o1, Permission o2) {
				return o1.getSortRank()-o2.getSortRank();
			}
		});
		
		for(Permission l1:level1permissions){
			l1.putItems(processSonlist(l1.getId(),level2permissions));
		}
		return level1permissions;
	}
	
	/**
	 * 处理sonlist
	 * @param pid
	 * @param level2permissions
	 * @return
	 */
	private List<Permission> processSonlist(Integer pid, List<Permission> level2permissions) {
		List<Permission> son=new ArrayList<>();
		for(Permission l2:level2permissions){
			if(l2.getPid().intValue()==pid.intValue()){
				son.add(l2);
			}
		}
		return son;
	}
	
	/**
	 * 检测一个角色是否包含指定的权限
	 * @param roleId
	 * @param permissionKey
	 * @return
	 */
	public boolean checkRoleHasPermission(Integer roleId, String permissionKey) {
		Permission permission=permissionService.getByPermissionkey(permissionKey);
		if(permission==null){
			return false;
		}
		RolePermission rolePermission=findFirst(Kv.by("roleId", roleId).set("permissionId",permission.getId()));
		return (rolePermission!=null);
	}
	
	
	/**
	 * 检测一个用户是否包含指定的权限
	 * @param userId
	 * @param permissionKeys
	 * @return
	 */
	public boolean checkUserHasPermission(Integer userId, String... permissionKeys) {
		if(permissionKeys==null||permissionKeys.length==0){return false;}
		User user=CACHE.me.getUser(userId);
		if(user==null){return false;}
		String roles=user.getRoles();
		if(notOk(roles)){
			return false;
		}
		boolean success=false;
		for(String permissionKey:permissionKeys){
			boolean oneSuccess=checkHasOnePermission(roles, permissionKey);
			success=success&&oneSuccess;
		}
		
		return success;
		
	}
	
	private boolean checkHasOnePermission(String roles ,String  permissionKey){
		//判断permissionKey有效性
		Permission permission=CACHE.me.getPermission(permissionKey);
		if(permission==null){
			return false;
		}
		
		/*//1、从数据库直接查 去查询至少存在一个有效的角色和资源对应关系
		Kv conf=Kv.by("permissionId",permission.getId()).set("roleArray",ArrayUtil.from(roles, ","));
		RolePermission rolePermission=dao().findFirst(Db.getSqlPara("user.auth.hasPermission",conf));
		return (rolePermission!=null);*/
		
		//2、通过从缓存数据里查询
		List<Permission> permissions = CACHE.me.getRolePermissions(roles);
		boolean hasPermission=false;
		for (Permission f : permissions) {
			if (permissionKey.equals(f.getPermissionKey())) {
				hasPermission = true;
				break;
			}
		}
		
		return hasPermission;
	}
	
	
	/**
	 * 删除关联一个资源的所有role和资源的绑定数据
	 * @param permissionId
	 */
	public void deleteByPermission(Integer permissionId) {
		deleteBy(Kv.by("permissionId", permissionId));
	}


}
