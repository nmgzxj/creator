package cn.jbolt._admin.user;

import java.util.Date;
import java.util.List;

import com.jfinal.aop.Inject;
import com.jfinal.kit.HashKit;
import com.jfinal.kit.Kv;
import com.jfinal.kit.Ret;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;

import cn.jbolt._admin.systemlog.SystemLogService;
import cn.jbolt.base.BaseService;
import cn.jbolt.common.config.Msg;
import cn.jbolt.common.model.SystemLog;
import cn.jbolt.common.model.User;
import cn.jbolt.common.util.ArrayUtil;
import cn.jbolt.common.util.CACHE;
import cn.jbolt.common.util.PinYinUtil;

public class UserService extends BaseService<User> {
	@Inject
	private SystemLogService systeLogService;
	@Override
	protected User dao() {
		return User.dao;
	}
	
	/**
	 * 保存
	 * @param user
	 * @return
	 */
	public Ret save(Integer userId,User user) {
		if(user==null||isOk(user.getId())){
			return fail(Msg.PARAM_ERROR);
		}
		if(notOk(user.getSex())){
			user.setSex("男");
		}
		if(notOk(user.getRoles())){return fail("必须设置用户角色");}
		if (existsName(user.getName())) {
			return fail("用户【"+user.getName()+"】已经存在，请输入其它名称");
		}
		if (exists("username",user.getUsername())) {
			return fail("用户名【"+user.getUsername()+"】已经存在，请输入其它用户名");
		}
		user.setName(user.getName().trim());
		user.setUsername(user.getUsername().trim());
		user.setEnable(true);
		if(notOk(user.getAvatar())){
			user.setAvatar("/assets/img/"+(user.getSex().equals("男")?"nan.png":"nv.png"));
		}
		user.setPinyin(PinYinUtil.getSpells(user.getName()));
		user.setPassword(HashKit.md5(user.getPassword()));
		user.setCreateTime(new Date());
		boolean success=user.save();
		if(success){
			addSaveSystemLog(user.getId(), userId, SystemLog.TARGETTYPE_USER, user.getName());
		}
		return success?success(Msg.SUCCESS):fail(Msg.FAIL);
	}
	
	
	/**
	 * 更新
	 * @param user
	 * @return
	 */
	public Ret update(Integer userId,User user) {
		if(user==null){
			return fail(Msg.PARAM_ERROR);
		}
		if(notOk(user.getSex())){
			user.setSex("男");
		}
		if(notOk(user.getId())){
			return fail(Msg.PARAM_ERROR);
		}
		if (existsName(user.getName(),user.getId())) {
			return fail("用户【"+user.getName()+"】已经存在，请输入其它名称");
		}
		if (exists("username",user.getUsername(),user.getId())) {
			return fail("用户名【"+user.getUsername()+"】已经存在，请输入其它名称");
		}
		if(notOk(user.getAvatar())){
			user.setAvatar("/assets/img/"+(user.getSex().equals("男")?"nan.png":"nv.png"));
		}
		user.setName(user.getName().trim());
		user.setUsername(user.getUsername().trim());
		user.setPinyin(PinYinUtil.getSpells(user.getName()));
		boolean success=user.update();
		if(success){
			CACHE.me.removeUser(user.getId());
			addUpdateSystemLog(user.getId(), userId, SystemLog.TARGETTYPE_USER, user.getName());
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
		if(notOk(id)){return fail(Msg.PARAM_ERROR);}
		if(userId.intValue()==id.intValue()){
			return fail("自己不能删除自己");
		}
		//执行删除
		User user=findById(id);
		if(user==null){return fail("用户信息不存在或已被删除");}
		boolean success=user.delete();
		if(success){
			//更新缓存
			CACHE.me.removeUser(id);
			addDeleteSystemLog(user.getId(), userId, SystemLog.TARGETTYPE_USER, user.getName());
		}
		return success?success(Msg.SUCCESS):fail(Msg.FAIL);
	}
	/**
	 * 检测一个角色是否被用户使用了
	 * @param id
	 * @return
	 */
	public boolean checkRoleInUse(Integer id) {
		User user=dao().findFirst("select * from "+table() +" where find_in_set('"+id+"',roles)>0");
		return user!=null;
	}
	/**
	 * 切换Enable状态
	 * @param id
	 * @return
	 */
	public Ret toggleEnable(Integer userId,Integer id) {
		if(userId.intValue()==id.intValue()){
			return fail("自己不能操作自己的用户状态");
		}
		Ret ret=toggleBoolean(Kv.by("userId", userId), id, "enable");
		if(ret.isOk()){
			CACHE.me.removeUser(id);
			User user=ret.getAs("data");
			addUpdateSystemLog(id, userId, SystemLog.TARGETTYPE_USER, user.getName(), "的状态为["+(user.getEnable()?"启用]":"禁用]"));
			return success(Msg.SUCCESS);
		}
		
		return fail(Msg.FAIL);
	}
	/**
	 * 通过用户名密码获取用户信息
	 * @param userName
	 * @param password
	 * @return
	 */
	public User getUser(String userName, String password) {
		if(notOk(userName)||notOk(password)){
			return null;
		}
		return findFirst(Kv.by("userName", userName).set("password",HashKit.md5(password)));
	}
	/**
	 * 更新密码
	 * @param userId
	 * @param oldPwd
	 * @param newPwd
	 * @return
	 */
	public Ret doUpdatePwd(Integer userId, String oldPwd, String newPwd) {
		User cacheUser=CACHE.me.getUser(userId);
		if(cacheUser==null){return fail("用户信息异常");}
		User user=getUser(cacheUser.getUsername(), oldPwd);
		if(user==null){
			return fail("原密码输入不正确");
		}
		if(user.getId().intValue()!=userId.intValue()){
			return fail("原密码输入不正确");
		}
		user.setPassword(HashKit.md5(newPwd));
		boolean success=user.update();
		return success?success(Msg.SUCCESS):fail(Msg.FAIL);
	}
	/**
	 * 修改密码
	 * @param sessionUserId
	 * @param userId
	 * @param newPwd
	 * @return
	 */
	public Ret submitPwd(Integer sessionUserId,Integer userId, String newPwd) {
		User user=findById(userId);
		if(user==null){return fail("用户不存在");}
		user.setPassword(HashKit.md5(newPwd));
		boolean success=user.update();
		if(success){
			addUpdateSystemLog(userId, sessionUserId, SystemLog.TARGETTYPE_USER, user.getName(), "的[密码]");
		}
		return success?success(Msg.SUCCESS):fail(Msg.FAIL);
	}
	/**
	 * 获取所有的角色组
	 * @return
	 */
	public List<String> getAllRoleGroups() {
		return Db.query("select distinct roles from "+table());
	}
	/**
	 * 检测用户分配了指定的所有角色
	 * @param userId
	 * @param roleIds
	 * @return
	 */
	public boolean checkUserHasRole(Integer userId, Integer[] roleIds) {
		if(roleIds==null||roleIds.length==0){return false;}
		User user=CACHE.me.getUser(userId);
		if(user==null){return false;}
		String roles=user.getRoles();
		if(StrKit.isBlank(roles)){return false;}
		String[] roleArray=ArrayUtil.from(roles, ",");
		if(roleArray==null||roleArray.length==0){return false;}
		boolean hasRole=true;
		for(Integer roleId:roleIds){
			if(ArrayUtil.contains(roleArray, roleId)==false){
				hasRole=false;
				break;
			}
		}
		return hasRole;
	}



}
