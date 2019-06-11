package cn.jbolt._admin.permission;

import java.util.List;

import com.jfinal.aop.Inject;
import com.jfinal.kit.Kv;
import com.jfinal.kit.Ret;
import com.jfinal.plugin.activerecord.Db;

import cn.jbolt._admin.role.RoleService;
import cn.jbolt._admin.rolepermission.RolePermissionService;
import cn.jbolt._admin.systemlog.SystemLogService;
import cn.jbolt.base.BaseService;
import cn.jbolt.common.config.Msg;
import cn.jbolt.common.model.Permission;
import cn.jbolt.common.model.SystemLog;
import cn.jbolt.common.util.CACHE;
/**
 * 系统权限资源Service
 * @author 小木
 *
 */
public class PermissionService extends BaseService<Permission> {
	@Inject
	private RoleService roleService;
	@Inject
	private SystemLogService systeLogService;
	@Inject
	private RolePermissionService rolePermissionService;
	@Override
	protected Permission dao() {
		return Permission.dao;
	}
	/**
	 * 得到所有的一级permission
	 * @return
	 */
	public List<Permission> getAllParentPermissions() {
		return getCommonList(Kv.by("pid", 0).set("level",Permission.LEVEL_1),"sortRank");
	}
	/**
	 * 得到指定父级的下级permissions
	 * @return
	 */
	public List<Permission> getSonPermissions(Integer parentId) {
		return getCommonList(Kv.by("pid", parentId),"sortRank");
	}
	/**
	 * 得到所有的一级permission
	 * @return
	 */
	public List<Permission> getAllParentPermissionsOrderById() {
		return getCommonList(Kv.by("pid", 0),"id");
		
	}
	/**
	 * 得到指定父级的下移permissions
	 * @return
	 */
	public List<Permission> getSonPermissionsOrderById(Integer parentId) {
		return getCommonList(Kv.by("pid", parentId),"id");
	}
	/**
	 * 得到所有permission 通过级别处理
	 * @return
	 */
	public List<Permission> getAllPermissionsWithLevel() {
		List<Permission> permissions=getAllParentPermissions();
		for(Permission f:permissions){
			List<Permission> sons=getSonPermissions(f.getId());
			for(Permission son:sons){
				son.putItems(getSonPermissions(son.getId()));
			}
			f.putItems(sons);
		}
		return permissions;
	}
	/**
	 * 得到所有permission 通过级别处理
	 * @return
	 */
	public List<Permission> getTwoLevelPermissions() {
		List<Permission> permissions=getAllParentPermissions();
		for(Permission f:permissions){
			f.putItems(getSonPermissions(f.getId()));
		}
		return permissions;
	}
	/**
	 * 保存数据
	 * @param permission
	 * @return
	 */
	public Ret save(Integer userId,Permission permission) {
		if(permission==null||isOk(permission.getId())){
			return fail(Msg.PARAM_ERROR);
		}
		if(notOk(permission.getPermissionKey())){
			return fail("请设置权限KEY");
		}
		
		if (exists("title",permission.getTitle())) {
			return fail("资源【"+permission.getTitle()+"】已经存在，请更换");
		}
		if (exists("permissionKey",permission.getPermissionKey())) {
			return fail("资源权限KEY【"+permission.getPermissionKey()+"】已经存在，请更换");
		}
		if(notOk(permission.getPid())){
			permission.setPid(0);
		}
		
		permission.setTitle(permission.getTitle().trim());
		permission.setSortRank(getNextSortRankWithPid(permission.getPid()));
		boolean success=permission.save();
		if(success){
			//添加日志
			addSaveSystemLog(permission.getId(), userId, SystemLog.TARGETTYPE_PERMISSION, permission.getTitle());
		}
		return success?success(Msg.SUCCESS):fail(Msg.FAIL);
	}
/*	*//**
	 * 添加操作日志
	 * @param permission
	 * @param userId
	 * @param type
	 * @param sort
	 *//*
	private void addSystemLog(Permission permission, Integer userId, int type,boolean sort) {
		String userName=CACHE.me.getUserName(userId);
		StringBuilder title=new StringBuilder();
		title.append("<span class='text-danger'>[").append(userName).append("]</span>");
		switch (type) {
		case SystemLog.TYPE_SAVE:
			title.append("新增了");
			break;
		case SystemLog.TYPE_UPDATE:
			title.append("更新了");
			break;
		case SystemLog.TYPE_DELETE:
			title.append("删除了");
			break;
		}
		title.append("权限数据")
		.append("<span class='text-danger'>[").append(permission.getTitle()).append("]</span>");
		if(sort){
			title.append("的顺序");
		}
		systeLogService.saveLog(type, SystemLog.TARGETTYPE_PERMISSION, permission.getId(), title.toString(), 0, userId,userName);
		
	}*/
	/**
	 * 得到最新的next rank
	 * @param pid
	 * @return
	 */
	private int getNextSortRankWithPid(Integer pid) {
		if(pid==null){
			pid=0;
		}
		return getNextSortRank(Kv.by("pid", pid));
	}
	
	/**
	 * 修改数据
	 * @param permission
	 * @return
	 */
	public Ret update(Integer userId,Permission permission) {
		if(permission==null||notOk(permission.getId())){
			return fail(Msg.PARAM_ERROR);
		}
		if (exists("title",permission.getTitle(),permission.getId())) {
			return fail("资源【"+permission.getTitle()+"】已经存在，请输入其它名称");
		}
		if (exists("permissionKey",permission.getPermissionKey(),permission.getId())) {
			return fail("资源权限KEY【"+permission.getPermissionKey()+"】已经存在，请更换");
		}
		if(notOk(permission.getPid())){
			permission.setPid(0);
		}
		permission.setTitle(permission.getTitle().trim());
		Boolean sort=permission.getBoolean("sort");
		if(sort==null){
			processPermissionNewLevel(permission);
		}
		boolean success=permission.update();
		if(success){
			CACHE.me.removePermission(permission.getId());
			CACHE.me.removePermission(permission.getPermissionKey());
			if(permission.getIsMenu()){
				CACHE.me.removeMenusAndPermissionsByRoleGroups();
			}
			if(sort!=null){
				addUpdateSystemLog(permission.getId(), userId, SystemLog.TARGETTYPE_PERMISSION, permission.getTitle(),"的顺序");
			}else{
				addUpdateSystemLog(permission.getId(), userId, SystemLog.TARGETTYPE_PERMISSION, permission.getTitle());
			}
			
		}
		return success?success(Msg.SUCCESS):fail(Msg.FAIL);
	}
	
	private void processPermissionNewLevel(Permission permission) {
		if(notOk(permission.getPid())){
			permission.setLevel(1);
		}else{
			int level=getParentLevel(permission.getPid());
			permission.setLevel(level+1);
		}
	}
	private int getParentLevel(Integer pid) {
		Permission permission=findById(pid);
		return permission.getLevel();
	}
	/**
	 * 删除
	 * @param id
	 * @return
	 */
	public Ret delPermissionById(Integer userId,Integer id,boolean processRoleCache) {
		Permission permission=findById(id);
		if(permission==null){return fail("资源不存在或已被删除");}
		boolean success=permission.delete();
		if(success){
			CACHE.me.removePermission(permission.getId());
			CACHE.me.removePermission(permission.getPermissionKey());
			//根据被删除的permission去删掉给role上的数据
			rolePermissionService.deleteByPermission(permission.getId());
			//删除子节点
			if(permission.getLevel()!=Permission.LEVEL_3){
				deleteByPid(userId,permission.getId());
			}
			if(processRoleCache){
				CACHE.me.removeMenusAndPermissionsByRoleGroups();
			}
			//添加日志
			addDeleteSystemLog(permission.getId(), userId, SystemLog.TARGETTYPE_PERMISSION, permission.getTitle());
			
		}
		return success?success(Msg.SUCCESS):fail(Msg.FAIL);
	}
	 /**
	  * 删除子节点
	  * @param userId
	  * @param pid
	  */
	private void deleteByPid(Integer userId,Integer pid) {
		List<Permission> permissions=getSonPermissions(pid);
		for(Permission permission:permissions){
			delPermissionById(userId, permission.getId(),false);
		}
	}
	 
	/**
	 * 上移
	 * @param id
	 * @return
	 */
	public Ret doUp(Integer userId,Integer id) {
		Permission permission=findById(id);
		if(permission==null){
			return fail("数据不存在或已被删除");
		}
		Integer rank=permission.getSortRank();
		if(rank==null||rank<=0){
			return fail("顺序需要初始化");
		}
		if(rank==1){
			return fail("已经是第一个");
		}
		Permission upPermission=findFirst(Kv.by("sortRank", rank-1).set("pid", permission.getPid()));
		if(upPermission==null){
			return fail("顺序需要初始化");
		}
		upPermission.setSortRank(rank);
		permission.setSortRank(rank-1);
		upPermission.put("sort", true);
		permission.put("sort", true);
		update(userId,upPermission);
		update(userId,permission);
	/*	CACHE.me.removePermission(upPermission.getId());
		CACHE.me.removePermission(permission.getId());
		if(permission.getIsMenu()){
			CACHE.me.removeMenusAndPermissionsByRoleGroups();
		}*/
		return SUCCESS;
	}
 
	
	
	/**
	 * 下移
	 * @param id
	 * @return
	 */
	public Ret doDown(Integer userId,Integer id) {
		Permission permission=findById(id);
		if(permission==null){
			return fail("数据不存在或已被删除");
		}
		Integer rank=permission.getSortRank();
		if(rank==null||rank<=0){
			return fail("顺序需要初始化");
		}
		int max=getCount(Kv.by("pid",permission.getPid()));
		if(rank==max){
			return fail("已经是最后已一个");
		}
		Permission upPermissions=findFirst(Kv.by("sortRank", rank+1).set("pid", permission.getPid()));
		if(upPermissions==null){
			return fail("顺序需要初始化");
		}
		upPermissions.setSortRank(rank);
		permission.setSortRank(rank+1);
		upPermissions.put("sort", true);
		permission.put("sort", true);
		update(userId,upPermissions);
		update(userId,permission);
		/*CACHE.me.removePermission(upPermissions.getId());
		CACHE.me.removePermission(permission.getId());
		if(permission.getIsMenu()){
			CACHE.me.removeMenusAndPermissionsByRoleGroups();
		}*/
		return SUCCESS;
	}
	
	
	/**
	 * 初始化排序
	 */
	public Ret doInitRank(){
		List<Permission> parents=getAllParentPermissionsOrderById();
		if(parents.size()>0){
			for(int i=0;i<parents.size();i++){
				parents.get(i).setSortRank(i+1);
			}
			Db.batchUpdate(parents, parents.size());
			for(Permission f:parents){
				CACHE.me.removePermission(f.getId());
				List<Permission> permissions=getSonPermissionsOrderById(f.getId());
				if(permissions.size()>0){
					for(int i=0;i<permissions.size();i++){
						permissions.get(i).setSortRank(i+1);
					}
					Db.batchUpdate(permissions, permissions.size());
					for(Permission s2:permissions){
						CACHE.me.removePermission(s2.getId());
						CACHE.me.removePermission(s2.getPermissionKey());
						List<Permission> gsons=getSonPermissionsOrderById(s2.getId());
						if(gsons.size()>0){
							for(int i=0;i<gsons.size();i++){
								gsons.get(i).setSortRank(i+1);
							}
							Db.batchUpdate(gsons, gsons.size());
							for(Permission s3:gsons){
								CACHE.me.removePermission(s3.getId());
								CACHE.me.removePermission(s3.getPermissionKey());
							}
						}
						
					}
					
					
				}
				
			}
		}
		CACHE.me.removeMenusAndPermissionsByRoleGroups();
		return SUCCESS;
		
	}
	/**
	 * 通过权限资源的KEY获取数据
	 * @param permissionKey
	 * @return
	 */
	public Permission getByPermissionkey(String permissionKey) {
		return findFirst(Kv.by("permissionKey", permissionKey));
	}
	
	
}