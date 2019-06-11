package cn.jbolt._admin.role;

import com.jfinal.aop.Inject;
import com.jfinal.kit.Ret;

import cn.jbolt._admin.rolepermission.RolePermissionService;
import cn.jbolt._admin.systemlog.SystemLogService;
import cn.jbolt._admin.user.UserService;
import cn.jbolt.base.BaseService;
import cn.jbolt.common.config.Msg;
import cn.jbolt.common.model.Role;
import cn.jbolt.common.model.SystemLog;
import cn.jbolt.common.util.ArrayUtil;
import cn.jbolt.common.util.CACHE;
/**
 * 角色管理Service
 * @ClassName:  RoleService   
 * @author: JFinal学院-小木 QQ：909854136 
 * @date:   2019年3月27日 上午11:54:25   
 *     
 * 注意：本内容仅限于JFinal学院 JBolt平台VIP成员内部传阅，请尊重开发者劳动成果，不要外泄出去用于其它商业目的
 */
public class RoleService extends BaseService<Role> {
	@Inject
	private UserService userService;
	@Inject
	private SystemLogService systeLogService;
	@Inject
	private RolePermissionService rolePermissionService;
	@Override
	protected Role dao() {
		return Role.dao;
	}
	
	/**
	 * 保存role数据
	 * @param user
	 * @return
	 */
	public Ret save(Integer userId,Role role) {
		if(role==null||isOk(role.getId())||notOk(role.getName())){
			return fail(Msg.PARAM_ERROR);
		}
		String name=role.getName().trim();
		if(name.indexOf(" ")!=-1){
			return saveAll(userId,ArrayUtil.from2(name, " "));
		}
		if (existsName(name)) {
			return fail(Msg.DATA_SAME_NAME_EXIST+":["+name+"]");
		}
		role.setName(name);
		boolean success=role.save();
		if(success){
			addSaveSystemLog(role.getId(), userId, SystemLog.TARGETTYPE_ROLE,role.getName());
		}
		return success?success(Msg.SUCCESS):fail(Msg.FAIL);
	}
	/**
	 * 添加多个
	 * @param userId
	 * @param from2
	 * @return
	 */
	private Ret saveAll(Integer userId, String[] names) {
		Ret ret=null; 
		for(String name:names){
			ret=save(userId, new Role().setName(name));
			if(ret.isFail()){
				return ret;
			}
		 }
		return success(Msg.SUCCESS);
	}

	
	
	/**
	 * 修改role数据
	 * @param user
	 * @return
	 */
	public Ret update(Integer userId,Role role) {
		if(role==null||notOk(role.getId())||notOk(role.getName())){
			return fail(Msg.PARAM_ERROR);
		}
		String name=role.getName().trim();
		if (existsName(name,role.getId())) {
			return fail(Msg.DATA_SAME_NAME_EXIST+":["+name+"]");
		}
		role.setName(name);
		boolean success=role.update();
		if(success){
			CACHE.me.removeRole(role.getId());
			addUpdateSystemLog(role.getId(), userId, SystemLog.TARGETTYPE_ROLE,role.getName());
		}
		return success?success(Msg.SUCCESS):fail(Msg.FAIL);
	}
	/**
	 * 删除
	 * @param userId
	 * @param id
	 * @return
	 */
	public Ret delete(Integer userId,Integer id) {
		//调用底层删除
		Ret ret=deleteById(id, true);
		if(ret.isOk()){
			//更新缓存
			CACHE.me.removeRole(id);
			// 删除rolePermissions绑定
			rolePermissionService.deleteRolePermission(id);
			//添加日志
			Role role=ret.getAs("data");
			addDeleteSystemLog(id, userId, SystemLog.TARGETTYPE_ROLE,role.getName());
		}
		return ret;
	}

	@Override
	public String checkInUse(Role role) {
		boolean inUse=userService.checkRoleInUse(role.getId());
		return inUse?"此角色已被用户分配使用，不能删除":null;
	}
	

}
