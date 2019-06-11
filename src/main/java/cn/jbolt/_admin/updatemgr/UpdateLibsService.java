package cn.jbolt._admin.updatemgr;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jfinal.aop.Inject;
import com.jfinal.kit.Kv;
import com.jfinal.kit.Ret;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;

import cn.hutool.http.HttpUtil;
import cn.jbolt._admin.systemlog.SystemLogService;
import cn.jbolt.base.BaseService;
import cn.jbolt.common.config.Msg;
import cn.jbolt.common.model.SystemLog;
import cn.jbolt.common.model.UpdateLibs;

/**
 * @author 小木 qq:909854136
 * @version 创建时间：2019年1月11日 下午3:21:30 类说明
 */
public class UpdateLibsService extends BaseService<UpdateLibs> {
	@Inject
	private SystemLogService systeLogService;

	@Override
	protected UpdateLibs dao() {
		return UpdateLibs.dao;
	}
	/**
	 * 获取到更新的Lib的json数据
	 * @return
	 */
	public String getUpdateLibs() {
		List<UpdateLibs> list = dao().find("select url,target,must,deleteAll from " + table());
		if (list == null || list.size() == 0) {
			return null;
		}
		JSONObject datas = new JSONObject();
		datas.put("datas", list);
		return datas.toJSONString(datas);
	}
	/**
	 * 后台分页查询
	 * @param keywords
	 * @param pageNumber
	 * @param pageSize
	 * @return
	 */
	public Page<UpdateLibs> paginateAdminList(String keywords, Integer pageNumber, int pageSize) {
		if (notOk(keywords)) {
			return paginate(pageNumber, pageSize);
		}
		return paginate(Kv.by("url", columnLike(keywords)), pageNumber, pageSize, true);
	}
	/**
	 * 初始化数据 一般用不上 慎重
	 * @param url
	 * @return
	 */
	public Ret doInitDatas(String url) {
		if (notOk(url)) {
			return fail("请指定url");
		}
		String string = HttpUtil.get(url);
		if (StrKit.isBlank(string)) {
			return fail("数据源异常");
		}
		JSONObject jsonObject = JSONObject.parseObject(string);
		if (jsonObject == null || jsonObject.containsKey("datas") == false) {
			return fail("数据源格式异常");
		}
		JSONArray jsonArray = jsonObject.getJSONArray("datas");
		if (jsonArray == null || jsonArray.size() == 0) {
			return fail("数据源为空");
		}
		Db.delete("delete from " + table());
		UpdateLibs updateLibs = null;
		List<UpdateLibs> updateLibList = new ArrayList<UpdateLibs>();
		for (int i = 0; i < jsonArray.size(); i++) {
			updateLibs = new UpdateLibs();
			if (jsonArray.getJSONObject(i).getBoolean("deleteAll") == null) {
				updateLibs.setDeleteAll(true);
			} else {
				updateLibs.setDeleteAll(jsonArray.getJSONObject(i).getBoolean("deleteAll"));
			}
			if (jsonArray.getJSONObject(i).getBoolean("must") == null) {
				updateLibs.setMust(false);
			} else {
				updateLibs.setMust(jsonArray.getJSONObject(i).getBoolean("must"));
			}
			updateLibs.setTarget(jsonArray.getJSONObject(i).getString("target"));
			updateLibs.setUrl(jsonArray.getJSONObject(i).getString("url"));
			updateLibList.add(updateLibs);
		}
		Db.batchSave(updateLibList, updateLibList.size());
		return SUCCESS;
	}
	/**
	 * 保存
	 * @param userId
	 * @param updateLibs
	 * @return
	 */
	public Ret save(Integer userId, UpdateLibs updateLibs) {
		if (userId == null || isOk(updateLibs.getId()) || notOk(updateLibs.getUrl()) || notOk(updateLibs.getTarget())
				|| notOk(updateLibs.getMust()) || notOk(updateLibs.getDeleteAll())) {
			return fail(Msg.PARAM_ERROR);
		}
		boolean success = updateLibs.save();
		if (success) {
			// 添加日志
			addSaveSystemLog(updateLibs.getId(), userId, SystemLog.TARGETTYPE_JBOLT_UPDATE_LIBS, updateLibs.getUrl());
		}
		return success ? success(Msg.SUCCESS) : fail(Msg.FAIL);
	}
	/**
	 * 更新
	 * @param userId
	 * @param updateLibs
	 * @return
	 */
	public Ret update(Integer userId, UpdateLibs updateLibs) {
		if (userId == null || notOk(updateLibs.getId()) || notOk(updateLibs.getUrl()) || notOk(updateLibs.getTarget())
				|| notOk(updateLibs.getMust()) || notOk(updateLibs.getDeleteAll())) {
			return fail(Msg.PARAM_ERROR);
		}
		boolean success = updateLibs.update();
		if (success) {
			// 添加日志
			addUpdateSystemLog(updateLibs.getId(), userId, SystemLog.TARGETTYPE_JBOLT_UPDATE_LIBS, updateLibs.getUrl());
		}
		return success ? success(Msg.SUCCESS) : fail(Msg.FAIL);
	}
	/**
	 * 删除
	 * @param userId
	 * @param id
	 * @return
	 */
	public Ret delete(Integer userId, Integer id) {
		Ret ret=deleteById(id);
		if (ret.isOk()) {
			// 日志
			UpdateLibs updateLibs=ret.getAs("data");
			addDeleteSystemLog(updateLibs.getId(), userId, SystemLog.TARGETTYPE_JBOLT_UPDATE_LIBS, updateLibs.getUrl());
			return success(Msg.SUCCESS);
		}
		return fail(Msg.FAIL);
	}


	/**
	 * 切换是否每次强制更新
	 * @param userId
	 * @param id
	 * @return
	 */
	public Ret toggleMust(Integer userId, Integer id) {
		Ret ret=toggleBoolean(id, "must");
		if(ret.isOk()){
			//添加日志
			UpdateLibs updateLibs=ret.getAs("data");
			addUpdateSystemLog(id, userId, SystemLog.TARGETTYPE_JBOLT_UPDATE_LIBS, updateLibs.getUrl(), "的状态-是否强制每次都更新:"+updateLibs.getMust());
			return success(Msg.SUCCESS);
		}
		return  fail(Msg.FAIL);
	}
	/**
	 * 切换更新是必须先清空target文件夹
	 * @param userId
	 * @param id
	 * @return
	 */
	public Ret toggleDeleteAll(Integer userId, Integer id) {
		Ret ret=toggleBoolean(id, "deleteAll");
		if(ret.isOk()){
			//添加日志
			UpdateLibs updateLibs=ret.getAs("data");
			addUpdateSystemLog(id, userId, SystemLog.TARGETTYPE_JBOLT_UPDATE_LIBS, updateLibs.getUrl(), "的状态-是否清空目标文件夹:"+updateLibs.getDeleteAll());
			return success(Msg.SUCCESS);
		}
		return  fail(Msg.FAIL);
	}


}
