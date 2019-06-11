package cn.jbolt._admin.dictionary;

import com.jfinal.aop.Inject;

import cn.jbolt._admin.permission.CheckPermission;
import cn.jbolt._admin.permission.PermissionKey;
import cn.jbolt.base.BaseController;
import cn.jbolt.common.model.DictionaryType;

/**
 * 数据字典类型管理
* @author 小木 qq:909854136
* @version 创建时间：2019年3月15日 下午3:50:14
*/
@CheckPermission({PermissionKey.DICTIONARY})
public class DictionaryTypeAdminController extends BaseController {
	@Inject
	private DictionaryTypeService service;
	/**
	 * 加载管理portal
	 */
	public void mgr(){
		set("dictionaryTypes",service.findAll());
		render("mgrportal.html");
	}
	
	public void add(){
		render("add.html");
	}
	public void edit(){
		Integer id=getInt(0);
		if(notOk(id)){
			renderFormError("数据不存在");
			return;
		}
		DictionaryType type=service.findById(id);
		if(type==null){
			if(notOk(id)){
				renderFormError("数据不存在");
				return;
			}
		}
		set("dictionaryType",type);
		render("edit.html");
	}
	public void save(){
		renderJson(service.save(getSessionAdminUserId(),getModel(DictionaryType.class, "dictionaryType")));
	}
	public void update(){
		renderJson(service.update(getSessionAdminUserId(),getModel(DictionaryType.class, "dictionaryType")));
	}
	
	public void delete(){
		renderJson(service.delete(getSessionAdminUserId(),getInt(0)));
	}
}
