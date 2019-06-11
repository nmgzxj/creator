package cn.jbolt._admin.dictionary;


import java.util.List;

import com.jfinal.aop.Inject;

import cn.jbolt._admin.permission.CheckPermission;
import cn.jbolt._admin.permission.PermissionKey;
import cn.jbolt._admin.permission.UnCheck;
import cn.jbolt.base.BaseController;
import cn.jbolt.common.model.Dictionary;
import cn.jbolt.common.model.DictionaryType;
@CheckPermission({PermissionKey.DICTIONARY})
public class DictionaryAdminController extends BaseController {
	@Inject
	private DictionaryService service;
	@Inject
	private DictionaryTypeService dictionaryTypeService;
	/**
	 * options
	 */
	@UnCheck
	public void options(){
		renderJsonData(service.getOptionListByType(get("key")));
	}
	/**
	 * 加载管理portal
	 */
	public void mgr(){
		Integer typeId=getInt(0);
		if(notOk(typeId)){
			renderErrorPortal("选择左侧分类查询数据");
			return;
		}else{
			DictionaryType type=dictionaryTypeService.findById(typeId);
			if(type==null){
				renderErrorPortal("参数异常，加载失败");
				return;
			}
			initMgr(type);
		}
		
		render("mgrportal.html");
	}
	
	private void initMgr(DictionaryType type){
		set("dictionaryType", type);
		List<Dictionary> dictionaries=service.getListByType(type.getId());
		set("dictionaries",dictionaries);
		if(type.getModeLevel()==DictionaryType.MODE_LEVEL_2){
			set("dataTotalCount", service.getCountByType(type.getId()));
		}else{
			set("dataTotalCount", dictionaries.size());
		}
	}
	/**
	 * 管理首页
	 */
	public void index(){
		render("index.html");
	}
	/**
	 * 从日志过来的显示一个
	 */
	public void show(){
		Integer dictionaryId=getInt(0);
		if(notOk(dictionaryId)){
			renderErrorPjax("数据不存在或已被删除");
		}else{
			Dictionary dictionary=service.findById(dictionaryId);
			if(dictionary==null){
				renderErrorPjax("数据不存在或已被删除");
			}else{
				Integer typeId=dictionary.getTypeId();
				set("typeId", typeId);
				set("dataList",service.getListByType(typeId));
				set("dataTotalCount", service.getCountByType(typeId));
				set("showId", dictionaryId);
				//TODO #mmm 前端页面实现show效果
				render("index.html");
			}
			
		}
	}
	/**
	 * 为下拉select提供数据源
	 */
	@UnCheck
	public void select(){
		renderJsonData(service.getListByType(getInt(),false));
	}
	
	/**
	 * 新增
	 */
	public void add(){
		set("typeId", getInt(0));
		render("add.html");
	}
	/**
	 * 新增
	 */
	public void addItem(){
		set("typeId", getInt(0));
		set("pid", getInt(1));
		set("needPidSelect",true);
		render("add.html");
	}
	/**
	 * 编辑
	 */
	public void edit(){
		set("typeId", getInt(0));
		Dictionary dictionary=service.findById(getInt(1));
		set("dictionary",dictionary );
		set("pid", dictionary.getPid());
		render("edit.html");
	}
	/**
	 * 编辑
	 */
	public void editItem(){
		set("typeId", getInt(0));
		Dictionary dictionary=service.findById(getInt(1));
		set("dictionary",dictionary );
		set("needPidSelect",true );
		set("pid", dictionary.getPid());
		render("edit.html");
	}
	/**
	 * 保存
	 */
	public void save(){
		Dictionary dictionary=getModel(Dictionary.class, "dictionary");
		renderJson(service.save(getSessionAdminUserId(),dictionary));
	}
	/**
	 * 更新
	 */
	public void update(){
		Dictionary dictionary=getModel(Dictionary.class, "dictionary");
		renderJson(service.update(getSessionAdminUserId(),dictionary));
	}
	/**
	 * 删除
	 */
	public void delete(){
		renderJson(service.deleteDictionaryById(getSessionAdminUserId(),getInt()));
	}
}
