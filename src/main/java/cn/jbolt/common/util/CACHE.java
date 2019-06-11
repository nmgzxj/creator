package cn.jbolt.common.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

import com.jfinal.aop.Aop;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.ehcache.CacheKit;
import com.jfinal.plugin.ehcache.IDataLoader;

import cn.jbolt._admin.dictionary.DictionaryService;
import cn.jbolt._admin.dictionary.DictionaryTypeService;
import cn.jbolt._admin.globalconfig.GlobalConfigService;
import cn.jbolt._admin.permission.PermissionService;
import cn.jbolt._admin.rolepermission.RolePermissionService;
import cn.jbolt._admin.user.UserService;
import cn.jbolt.common.config.GlobalConfigKey;
import cn.jbolt.common.model.Dictionary;
import cn.jbolt.common.model.DictionaryType;
import cn.jbolt.common.model.GlobalConfig;
import cn.jbolt.common.model.GoodsBackCategory;
import cn.jbolt.common.model.GoodsType;
import cn.jbolt.common.model.Permission;
import cn.jbolt.common.model.Role;
import cn.jbolt.common.model.User;

public class CACHE {
	public static final CACHE me = new CACHE();
	public static final String CACHE_NAME = "jbolt";
	private DictionaryService dictionaryService = Aop.get(DictionaryService.class);
	private DictionaryTypeService dictionaryTypeService = Aop.get(DictionaryTypeService.class);
	private RolePermissionService rolePermissionService = Aop.get(RolePermissionService.class);
	private UserService userService = Aop.get(UserService.class);
	private PermissionService permissionService = Aop.get(PermissionService.class);
	private GlobalConfigService globalConfigAdminService = Aop.get(GlobalConfigService.class);

	/**
	 * 缓存通过ID获得GoodsBackCategory数据
	 * 
	 * @param id
	 * @return
	 */
	public GoodsBackCategory getGoodsBackCategory(final Integer id) {
		if (id == null || id <= 0) {
			return null;
		}
		return CacheKit.get(CACHE_NAME, "em_mall_goods_back_category_" + id, new IDataLoader() {
			@Override
			public Object load() {
				return GoodsBackCategory.dao.findById(id);
			}
		});
	}
	/**
	 * cache中获取商品后台分类的名称
	 * @param id
	 * @return
	 */
	public String getGoodsBackCategoryName(final Integer id){
		GoodsBackCategory goodsBackCategory=getGoodsBackCategory(id);
		return goodsBackCategory==null?"":goodsBackCategory.getName();
	}
	/**
	 * cache中获取商品后台分类的唯一标识KEY
	 * @param id
	 * @return
	 */
	public String getGoodsBackCategoryKey(final Integer id){
		GoodsBackCategory goodsBackCategory=getGoodsBackCategory(id);
		return goodsBackCategory==null?null:goodsBackCategory.getCategoryKey();
	}
	/**
	 * 
	 * @param id
	 * @return
	 */
	public String getGoodsBackCategoryFullName(final Integer id){
		GoodsBackCategory goodsCategory=getGoodsBackCategory(id);
		if(goodsCategory==null){return "";}
		String keys=goodsCategory.getCategoryKey();
		if(StrKit.isBlank(keys)){return "";}
		if(keys.indexOf("_")==-1){
			return  goodsCategory.getName();
		}
		Integer[] ids=ArrayUtil.toInt(keys, "_");
		if(ids==null||ids.length==0){return "";}
		String fullName="";
		for(int i=0;i<ids.length;i++){
			String name=getGoodsBackCategoryName(ids[i]);
			if(StrKit.notBlank(name)){
				if(i==0){
					fullName=name;
				}else{
					fullName=fullName+" > "+name;
				}
			}
		}
		return fullName;
	}
	
	/**
	 * 删除GoodsBackCategory
	 * @param id
	 */
	public void removeGoodsBackCategory(Integer id) {
		if (id == null || id <= 0) {
			return;
		}
		CacheKit.remove(CACHE_NAME, "em_mall_goods_back_category_" + id);
	}
	/**
	 * 缓存通过ID获得角色Role数据
	 * 
	 * @param id
	 * @return
	 */
	public Role getRole(final Integer id) {
		if (id == null || id <= 0) {
			return null;
		}
		return CacheKit.get(CACHE_NAME, "em_role_" + id, new IDataLoader() {
			@Override
			public Object load() {
				return Role.dao.findById(id);
			}
		});
	}

	/**
	 * 删除角色
	 * 
	 * @param id
	 */
	public void removeRole(Integer id) {
		if (id == null || id <= 0) {
			return;
		}
		CacheKit.remove(CACHE_NAME, "em_role_" + id);
	}

	/**
	 * 缓存通过ID获得数据的
	 * 
	 * @param id
	 * @return
	 */
	public String getRoleName(Integer id) {
		Role role = getRole(id);
		return role == null ? "未分配" : role.getName();
	}

	/**
	 * 从缓存获取多个names
	 * 
	 * @param ids
	 * @return
	 */
	public List<Role> getRoles(String roles) {
		if (StrKit.isBlank(roles)) {
			return Collections.emptyList();
		}
		Integer[] roleIds = ArrayUtil.toDisInt(roles, ",");
		if (roleIds == null || roleIds.length == 0) {
			return Collections.emptyList();
		}
		List<Role> roleList = new ArrayList<Role>();
		Role role = null;
		for (Integer roleId : roleIds) {
			role = getRole(roleId);
			if (role != null) {
				roleList.add(role);
			}
		}

		return roleList;
	}
	/**
	 * 通过ID获得字典数据类型
	 * 
	 * @return
	 */
	public DictionaryType getDictionaryType(final Integer id) {
		if (id == null || id <= 0) {
			return null;
		}
		return CacheKit.get(CACHE_NAME, "em_dictionary_type_" + id, new IDataLoader() {
			@Override
			public Object load() {
				return dictionaryTypeService.findById(id);
			}
		});
	}
	/**
	 * 通过ID获得字典数据类型
	 * 
	 * @return
	 */
	public DictionaryType getDictionaryType(final String typeKey) {
		if (StrKit.isBlank(typeKey)) {
			return null;
		}
		return CacheKit.get(CACHE_NAME, "em_dictionary_type_key_" + typeKey, new IDataLoader() {
			@Override
			public Object load() {
				return dictionaryTypeService.findByTypeKey(typeKey);
			}
		});
	}
	
	/**
	 * 获得字典数据类型的标识Key
	 * 
	 * @param id
	 * @return
	 */
	public String getDictionaryTypeName(Integer id) {
		DictionaryType dictionaryType = getDictionaryType(id);
		return dictionaryType == null ? "" : dictionaryType.getName();
	}
	/**
	 * 获得字典数据类型的标识Key
	 * 
	 * @param id
	 * @return
	 */
	public String getDictionaryTypeKey(Integer id) {
		DictionaryType dictionaryType = getDictionaryType(id);
		return dictionaryType == null ? "" : dictionaryType.getTypeKey();
	}
	/**
	 * 获得字典数据类型的ModelLevel 层级模式
	 * 
	 * @param id
	 * @return
	 */
	public int getDictionaryTypeModeLevel(Integer id) {
		DictionaryType dictionaryType = getDictionaryType(id);
		return dictionaryType == null ? 0 : dictionaryType.getModeLevel();
	}


	/**
	 * 通过ID获得字典数据
	 * 
	 * @return
	 */
	public Dictionary getDictionary(final Integer id) {
		if (id == null || id <= 0) {
			return null;
		}
		return CacheKit.get(CACHE_NAME, "em_dictionary_" + id, new IDataLoader() {
			@Override
			public Object load() {
				return dictionaryService.findById(id);
			}
		});
	}

	
	/**
	 * 获得字典数据名称
	 * 
	 * @param id
	 * @return
	 */
	public String getDictionaryName(Integer id) {
		Dictionary dictionary = getDictionary(id);
		return dictionary == null ? "" : dictionary.getName();
	}
	
	/**
	 * 获得用户名称
	 * 
	 * @param id
	 * @return
	 */
	public String getUserName(Integer id) {
		User user = getUser(id);
		return user == null ? "" : user.getName();
	}

	/**
	 * 获得用户头像
	 * 
	 * @param id
	 * @return
	 */
	public String getUserAvatar(Integer id) {
		User user = getUser(id);
		return user == null ? "" : user.getAvatar();
	}
	/**
	 * 获得用户头像
	 * 
	 * @param id
	 * @return
	 */
	public String getUserRealAvatar(Integer id) {
		User user = getUser(id);
		return user == null ? "" : RealIamgeUtil.get(user.getAvatar(), "/assets/img/avatar.jpg");
	}

	/**
	 * 删除字典数据
	 * 
	 * @param id
	 */
	public void removeDictionary(Integer id) {
		if (id == null || id <= 0) {
			return;
		}
		CacheKit.remove(CACHE_NAME, "em_dictionary_" + id);
	}
	/**
	 * 删除字典数据类型
	 * 
	 * @param id
	 */
	public void removeDictionaryType(Integer id) {
		if (id == null || id <= 0) {
			return;
		}
		CacheKit.remove(CACHE_NAME, "em_dictionary_type_" + id);
	}
	/**
	 * 删除字典数据类型
	 * 
	 * @param id
	 */
	public void removeDictionaryType(String typeKey) {
		if (StrKit.isBlank(typeKey)) {
			return;
		}
		CacheKit.remove(CACHE_NAME, "em_dictionary_type_key_" + typeKey);
	}

	/**
	 * 通过ID获得User
	 * 
	 * @return
	 */
	public User getUser(final Integer id) {
		if (id == null || id <= 0) {
			return null;
		}
		return CacheKit.get(CACHE_NAME, "em_user_" + id, new IDataLoader() {
			@Override
			public Object load() {
				User user = userService.findById(id);
				if (user != null) {
					user.remove("password");
				}
				return user;
			}
		});
	}

	/**
	 * 删除User
	 * 
	 * @param id
	 */
	public void removeUser(Integer id) {
		if (id == null || id <= 0) {
			return;
		}
		CacheKit.remove(CACHE_NAME, "em_user_" + id);
	}

	/**
	 * 缓存通过ID获得角色Role的permissions
	 * 
	 * @param id
	 * @return
	 *//*
		 * public List<Permission> getRolePermissions(final Integer roleId) { if (roleId ==
		 * null || roleId <= 0) { return null; } return CacheKit.get(CACHE_NAME,
		 * "em_role_permissions_" + roleId, new IDataLoader() {
		 * 
		 * @Override public Object load() { return
		 * rolePermissionService.getPermissionsByRole(roleId); } }); }
		 */
	/**
	 * 缓存通过ID获得角色Role的permissions
	 * 
	 * @param id
	 * @return
	 */
	public List<Permission> getRolePermissions(final String roleIds) {
		if (StrKit.isBlank(roleIds)) {
			return null;
		}
		return CacheKit.get(CACHE_NAME, "em_roles_permissions_" + roleIds, new IDataLoader() {
			@Override
			public Object load() {
				return rolePermissionService.getPermissionsByRoles(roleIds);
			}
		});
	}

	/**
	 * 缓存通过ID获得角色Role的permissions
	 * 
	 * @param id
	 * @return
	 *//*
		 * public List<Permission> getRoleMenus(final Integer roleId) { if (roleId ==
		 * null || roleId <= 0) { return null; } return CacheKit.get(CACHE_NAME,
		 * "em_role_menus_" + roleId, new IDataLoader() {
		 * 
		 * @Override public Object load() { return
		 * rolePermissionService.getMenusByRole(roleId); } }); }
		 */
	/**
	 * 缓存通过User ID获得角色Role的permissions
	 * 
	 * @param id
	 * @return
	 */
	public List<Permission> getUserMenus(final Integer userId) {
		User user=getUser(userId);
		if(user==null){
			return Collections.emptyList();
		}
		return getRoleMenus(user.getRoles());
	}
	public List<Permission> getRoleMenus(final String roleIds) {
		if (StrKit.isBlank(roleIds)) {
			return null;
		}
		return CacheKit.get(CACHE_NAME, "em_roles_menus_" + roleIds, new IDataLoader() {
			@Override
			public Object load() {
				return rolePermissionService.getMenusByRoles(roleIds);
			}
		});

	}

	/**
	 * 通过ID获得Permissions
	 * 
	 * @return
	 */
	public Permission getPermission(final Integer id) {
		if (id == null || id <= 0) {
			return null;
		}
		return CacheKit.get(CACHE_NAME, "em_permission_" + id, new IDataLoader() {
			@Override
			public Object load() {
				return permissionService.findById(id);
			}
		});
	}
	
	/**
	 * 通过permissionKey获得Permissions
	 * 
	 * @return
	 */
	public Permission getPermission(final String permissionKey) {
		if (StrKit.isBlank(permissionKey)) {
			return null;
		}
		return CacheKit.get(CACHE_NAME, "em_permission_" + permissionKey, new IDataLoader() {
			@Override
			public Object load() {
				return permissionService.getByPermissionkey(permissionKey);
			}
		});
	}

	/**
	 * 删除permissions
	 * 
	 * @param id
	 */
	public void removePermission(Integer id) {
		if (id == null || id <= 0) {
			return;
		}
		CacheKit.remove(CACHE_NAME, "em_permission_" + id);
	}
	/**
	 * 删除permissions
	 * 
	 * @param id
	 */
	public void removePermission(String permissionKey) {
		if (StrKit.isBlank(permissionKey)) {
			return;
		}
		CacheKit.remove(CACHE_NAME, "em_permission_" + permissionKey);
	}
	
 
	
	public void removeMenusAndPermissionsByRoleGroups(){
		List<String> roleGroups=userService.getAllRoleGroups();
		if(roleGroups.size()>0){
			roleGroups.forEach(new Consumer<String>() {
				@Override
				public void accept(String roleIds) {
					removeRolesPermissions(roleIds);
					removeRolesMenus(roleIds);
				}
			});
		}
	}
 

/*	*//**
	 * 删除removeRolePermissions
	 * 
	 * @param roleId
	 *//*
	public void removeRolePermissions(Integer roleId) {
		if (roleId == null || roleId <= 0) {
			return;
		}
		CacheKit.remove(CACHE_NAME, "em_role_permissions_" + roleId);
	}*/

	/**
	 * 删除removeRolePermissions
	 * 
	 * @param roleIds
	 */
	public void removeRolesPermissions(String roleIds) {
		if (StrKit.isBlank(roleIds)) {
			return;
		}
		CacheKit.remove(CACHE_NAME, "em_roles_permissions_" + roleIds);
	}

	/**
	 * 删除removeRoleMenus
	 * 
	 * @param roleId
	 *//*
	public void removeRoleMenus(Integer roleId) {
		if (roleId == null || roleId <= 0) {
			return;
		}
		CacheKit.remove(CACHE_NAME, "em_role_menus_" + roleId);
	}*/

	/**
	 * 删除removeRoleMenus
	 * 
	 * @param roleIds
	 */
	public void removeRolesMenus(String roleIds) {
		if (StrKit.isBlank(roleIds)) {
			return;
		}
		CacheKit.remove(CACHE_NAME, "em_roles_menus_" + roleIds);
	}

	/**
	 * put
	 * 
	 * @param key
	 * @param value
	 */
	public void put(String key, Object value) {
		CacheKit.put(CACHE_NAME, key, value);
	}
	
	

	

	/**
	 * 缓存通过key获取全局配置
	 * 
	 * @param key
	 * @return
	 */
	public GlobalConfig getGlobalConfig(final String configKey) {
		if (StrKit.isBlank(configKey)) {
			return null;
		}
		return CacheKit.get(CACHE_NAME, "em_global_config_" + configKey, new IDataLoader() {
			@Override
			public Object load() {
				return globalConfigAdminService.findByConfigKey(configKey);
			}
		});
	}

	/**
	 * 删除全局配置
	 * 
	 * @param key
	 */
	public void removeGlobalConfig( String configKey) {
		if (StrKit.isBlank(configKey)) {
			return;
		}
		CacheKit.remove(CACHE_NAME, "em_global_config_" + configKey);
	}

	/**
	 * 缓存通过key获取配置的值
	 * 
	 * @param key
	 * @return
	 */
	public String getGlobalConfigValue( String configKey) {
		GlobalConfig globalConfig = getGlobalConfig(configKey);
		return globalConfig == null ? "" : globalConfig.getConfigValue();
	}
	/**
	 * 获取微信公众号服务器配置根地址URL
	 * @return
	 */
	public String getWechatMpServerDomainRootUrl(){
		return getGlobalConfigValue(GlobalConfigKey.CONFIG_KEY_WECHAT_MP_SERVER_DOMAIN);
	}
	/**
	 * 获取微信小程序服务器配置根地址URL
	 * @return
	 */
	public String getWechatWxaServerDomainRootUrl(){
		return getGlobalConfigValue(GlobalConfigKey.CONFIG_KEY_WECHAT_WXA_SERVER_DOMAIN);
	}
 
	
	
	/**
	 * 通过id获得goodsType
	 * 
	 * @return
	 */
	public Permission getGoodsType(final Integer id) {
		if (id==null||id<=0) {
			return null;
		}
		return CacheKit.get(CACHE_NAME, "em_goodstype_" + id, new IDataLoader() {
			@Override
			public Object load() {
				return GoodsType.dao.findById(id);
			}
		});
	}

	/**
	 * 删除goodsType
	 * 
	 * @param id
	 */
	public void removeGoodsType(Integer id) {
		if (id == null || id <= 0) {
			return;
		}
		CacheKit.remove(CACHE_NAME, "em_goodstype_" + id);
	}
	
	

}
