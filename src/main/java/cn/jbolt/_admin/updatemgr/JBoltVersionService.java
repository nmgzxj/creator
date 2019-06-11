package cn.jbolt._admin.updatemgr;

import java.util.Date;
import java.util.List;

import com.alibaba.fastjson.JSONObject;
import com.jfinal.aop.Inject;
import com.jfinal.kit.Kv;
import com.jfinal.kit.Ret;
import com.jfinal.plugin.activerecord.Db;

import cn.jbolt.base.BaseService;
import cn.jbolt.common.config.Msg;
import cn.jbolt.common.model.JboltVersion;
import cn.jbolt.common.model.JboltVersionFile;
import cn.jbolt.common.model.SystemLog;
import cn.jbolt.common.util.ArrayUtil;

/**
* @author 小木 qq:909854136
* @version 创建时间：2019年1月9日 下午2:50:26
*/
public class JBoltVersionService extends BaseService<JboltVersion> {
	@Inject
	private JboltVersionFileService jboltVersionFileService;
	@Override
	protected JboltVersion dao() {
		return JboltVersion.dao;
	}
	/**
	 * 保存
	 * @param userId
	 * @param jboltVersion
	 * @return
	 */
	public Ret save(Integer userId,JboltVersion jboltVersion) {
		if(userId==null||isOk(jboltVersion.getId())||notOk(jboltVersion.getVersion())||notOk(jboltVersion.getPublishTime())){return fail(Msg.PARAM_ERROR);}
		jboltVersion.setCreateTime(new Date());
		jboltVersion.setUserId(userId);
		jboltVersion.setIsNew(false);
		boolean success=jboltVersion.save();
		if(success){
			//添加日志
			addSaveSystemLog(jboltVersion.getId(), userId, SystemLog.TARGETTYPE_JBOLT_VERSION, jboltVersion.getVersion());
		}
		return success?success(Msg.SUCCESS):fail(Msg.FAIL);
	}
	/**
	 * 更新
	 * @param userId
	 * @param jboltVersion
	 * @return
	 */
	public Ret update(Integer userId,JboltVersion jboltVersion) {
		if(userId==null||notOk(jboltVersion.getId())||notOk(jboltVersion.getVersion())||notOk(jboltVersion.getPublishTime())){return fail(Msg.PARAM_ERROR);}
		boolean success=jboltVersion.update();
		if(success){
			//添加日志
			addUpdateSystemLog(jboltVersion.getId(), userId, SystemLog.TARGETTYPE_JBOLT_VERSION, jboltVersion.getVersion());
		}
		return success?success(Msg.SUCCESS):fail(Msg.FAIL);
	}
	/**
	 * 删除
	 * @param userId
	 * @param id
	 * @return
	 */
	public Ret delete(Integer userId,Integer id){
		Ret ret=deleteById(id);
		if(ret.isOk()){
			jboltVersionFileService.deleteByVersion(id);
			JboltVersion jboltVersion=ret.getAs("data");
			//日志
			addDeleteSystemLog(jboltVersion.getId(), userId, SystemLog.TARGETTYPE_JBOLT_VERSION, jboltVersion.getVersion());
			return success(Msg.SUCCESS);
		}
		return fail(Msg.FAIL);
	}
	
	
 
	/**
	 * 切换是否是最新版
	 * @param userId
	 * @param id
	 * @return
	 */
	public Ret doToggleIsNew(Integer userId, Integer id) {
		Ret ret=toggleBoolean(id, "isNew");
		if(ret.isOk()){
			JboltVersion jboltVersion=ret.getAs("data");
			//日志
			addUpdateSystemLog(jboltVersion.getId(), userId, SystemLog.TARGETTYPE_JBOLT_VERSION, jboltVersion.getVersion(),"的是否为最新版状态:"+jboltVersion.getIsNew());
			return success(Msg.SUCCESS);
		}
	
		return fail(Msg.FAIL);
	}
	@Override
	public String toggleExtra(JboltVersion jboltVersion, String column) {
		if(column.equals("isNew")){
			if(jboltVersion.getIsNew()){
				 changeAllIsNewFlase();
			}
		}
		return null;
	}
	/**
	 * 切换所有isNew=true的变为false
	 */
	private void changeAllIsNewFlase() {
		Db.update("update "+table()+" set isNew=false where isNew=true");
	}
 
	/**
	 * 获取主程序更新数据
	 * @return
	 */
	public String getMainUpdateDatas() {
		JboltVersion jboltVersion=getNewJboltVersion();
		if(jboltVersion==null){return null;}
		List<JboltVersionFile> files=jboltVersionFileService.getFilesByJboltVersionId(jboltVersion.getId());
		if(files==null||files.size()==0){return null;}
		JSONObject jsonObject=new JSONObject();
		jsonObject.put("version", jboltVersion.getVersion());
		jsonObject.put("datas", ArrayUtil.getStringArray(files,"url"));
		return jsonObject.toJSONString();
	}
	/**
	 * 得到最新版
	 * @return
	 */
	private JboltVersion getNewJboltVersion() {
		return findFirst(Kv.by("isNew", true));
	}

	


}
