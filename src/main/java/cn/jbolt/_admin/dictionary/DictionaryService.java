package cn.jbolt._admin.dictionary;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.jfinal.kit.Kv;
import com.jfinal.kit.Ret;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;

import cn.jbolt.base.BaseService;
import cn.jbolt.common.config.Msg;
import cn.jbolt.common.model.Dictionary;
import cn.jbolt.common.model.DictionaryType;
import cn.jbolt.common.model.SystemLog;
import cn.jbolt.common.util.ArrayUtil;
import cn.jbolt.common.util.CACHE;
import cn.jbolt.common.util.ListMap;

public class DictionaryService extends BaseService<Dictionary>{
	@Override
	protected Dictionary dao() {
		return Dictionary.dao;
	}
	/**
	 * 根据类型获得数据字典数据列表
	 * @param type
	 * @return
	 */
	public List<Dictionary> getListByType(Integer type) {
		return getListByType(type, true);
	}
	/**
	 * 根据类型获得数据字典数据列表
	 * @param type
	 * @param needSubItems
	 * @return
	 */
	public List<Dictionary> getListByType(Integer type,boolean needSubItems) {
		if(notOk(type)){
			return Collections.emptyList();
		}
		Kv paras=Kv.by("typeId", type);
		if(!needSubItems){
			//如果不需要下级 就只读取上级
			paras.set("pid",0);
		}
		List<Dictionary> dictionarys=getCommonList(paras);
		if(needSubItems){
			return processSubItems(dictionarys);
		}
		return dictionarys;
	}
	/**
	 * 处理父子关系
	 * @param dictionarys
	 * @return
	 */
	private List<Dictionary> processSubItems(List<Dictionary> dictionarys) {
		List<Dictionary> submitItems=new ArrayList<Dictionary>();
		for(Dictionary dictionary:dictionarys){
			if(notOk(dictionary.getPid())){
				submitItems.add(dictionary);
			}
		}
		if(submitItems.size()>0){
			ListMap<String, Dictionary> map=new ListMap<String, Dictionary>();
			for(Dictionary dictionary:dictionarys){
				if(isOk(dictionary.getPid())){
					map.addItem("dic_"+dictionary.getPid(), dictionary);
				}
			}
			for(Dictionary dictionary:submitItems){
				List<Dictionary> items=map.get("dic_"+dictionary.getId());
				if(items!=null&&items.size()>0){
					dictionary.put("items",items);
				}
			}
		}
		return submitItems;
	}
	/**
	 * 根据ID删除字典数据
	 * @param id
	 * @return
	 */
	public Ret deleteDictionaryById(Integer userId,Integer id) {
		Ret ret=deleteById(id,true);
		if(ret.isOk()){
			CACHE.me.removeDictionary(id);
			//增加系统日志
			Dictionary dictionary=ret.getAs("data");
			addDeleteSystemLog(id, userId, SystemLog.TARGETTYPE_DICTIONARY, dictionary.getName());
		}
		return ret;
	}
	

	/**
	 * 保存字典数据
	 * @param dictionary
	 * @return
	 */
	public Ret save(Integer userId,Dictionary dictionary) {
		if(dictionary==null){
			return fail(Msg.PARAM_ERROR);
		}
		if(notOk(dictionary.getTypeId())){
			return fail(Msg.PARAM_ERROR);
		}
		if (exists(-1,dictionary.getName(),dictionary.getTypeId())) {
			return fail("此名称已经存在，请输入其它名称");
		}
		dictionary.setName(dictionary.getName().trim());
		if(dictionary.getPid()==null){
			dictionary.setPid(0);
		}
		boolean success=dictionary.save();
		if(success){
			//增加系统日志
			addSaveSystemLog(dictionary.getId(), userId, SystemLog.TARGETTYPE_DICTIONARY, dictionary.getName());
		}
		return success?success(Msg.SUCCESS):fail(Msg.FAIL);
	}
	
	/**
	 * 修改字典数据
	 * @param dictionary
	 * @return
	 */
	public Ret update(Integer userId,Dictionary dictionary) {
		if(dictionary==null){
			return fail(Msg.PARAM_ERROR);
		}
		if(notOk(dictionary.getId())||notOk(dictionary.getTypeId())){
			return fail(Msg.PARAM_ERROR);
		}
		if (exists(dictionary.getId(),dictionary.getName(),dictionary.getTypeId())) {
			return fail("此名称已经存在，请输入其它名称");
		}
		dictionary.setName(dictionary.getName().trim());
		if(dictionary.getPid()==null){
			dictionary.setPid(0);
		}
		boolean success=dictionary.update();
		if(success){
			CACHE.me.removeDictionary(dictionary.getId());
			//增加系统日志
			addUpdateSystemLog(dictionary.getId(), userId, SystemLog.TARGETTYPE_DICTIONARY, dictionary.getName());
			
		}
		return success?success(Msg.SUCCESS):fail(Msg.FAIL);
	}
	
	
	/**
	 * 判断字典名是否存在
	 * @param id
	 * @param name
	 * @param type
	 * @return
	 */
	public boolean exists(Integer id,String name,Integer type) {
		name = name.toLowerCase().trim();
		String sql = "select id from "+table()+" where name = ? and typeId= ? and id!=?  limit 1";
		Integer existId = Db.queryInt(sql, name,type,id);
		return existId != null;
	}
	public int getCountByType(Integer type) {
		if(notOk(type)){return 0;}
		return Db.queryInt("select count(id) from "+table()+" where typeId=?",type);
	}

	/**
	 * 根据类型获得字典数据 
	 * @return
	 */
	public List<Dictionary> getOptionListByType(Integer type,boolean needAll) {
		List<Dictionary> dictionaries=getListByType(type);
		if(needAll){
			dictionaries.add(0, new Dictionary().set("id", 0).set("name", "全部"));
		}
		return dictionaries;
	}
	public String getCacheNames(String ids) {
		if(notOk(ids)){return "未设置";}
		String[] idsArray=ArrayUtil.from(ids, ",");
		if(idsArray==null||idsArray.length==0){return "未设置";}
		StringBuilder sb=new StringBuilder();
		int index=0;
		for(String id:idsArray){
			if(isOk(id)){
				Dictionary dictionary=CACHE.me.getDictionary(Integer.parseInt(id));
				if(dictionary!=null){
					if(isOk(dictionary.getPid())&&CACHE.me.getDictionaryTypeModeLevel(dictionary.getTypeId())==DictionaryType.MODE_LEVEL_2){
						String pname=CACHE.me.getDictionaryName(dictionary.getPid());
						sb.append((StrKit.notBlank(pname)?(pname+"-"):"")+dictionary.getName());
					}else{
						sb.append(dictionary.getName());
					}
					if(index<idsArray.length-1){
						sb.append("，");
					}
					
				}
				 
			}
			index++;
		}
		return sb.toString();
	}
	public boolean checkTypeInUse(Integer typeId) {
		int count=getCountByType(typeId);
		return count>0;
	}
	/**
	 * 根据类型获得字典数据 
	 * @return
	 */
	public List<Dictionary> getOptionListByType(String typeKey) {
		DictionaryType dictionaryType=CACHE.me.getDictionaryType(typeKey);
		if(dictionaryType==null){return Collections.emptyList();}
		return getListByType(dictionaryType.getId());
	}
	/**
	 * 检测是否被其他模块使用
	 */
	@Override
	public String checkInUse(Dictionary m) {
		//TODO 检测
		return null;
	}
 
	 
 

}
