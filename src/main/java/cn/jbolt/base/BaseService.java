package cn.jbolt.base;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import com.jfinal.aop.Inject;
import com.jfinal.kit.Kv;
import com.jfinal.kit.Ret;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.ActiveRecordException;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.SqlPara;
import com.jfinal.plugin.activerecord.Table;
import com.jfinal.plugin.activerecord.TableMapping;

import cn.hutool.core.util.StrUtil;
import cn.jbolt._admin.systemlog.SystemLogService;
import cn.jbolt.common.config.Msg;
import cn.jbolt.common.model.SystemLog;
import cn.jbolt.common.util.ArrayUtil;
import cn.jbolt.common.util.CACHE;


public abstract class BaseService<M extends Model<M>> {
	@Inject
	private SystemLogService systeLogService;
	public static final Ret SUCCESS=Ret.ok();
	public Ret fail(String msg){
		return Ret.fail("msg", msg);
	}
	public Ret success(String msg){
		return Ret.ok("msg", msg);
	}
	public Ret success(Object data,String msg){
		return Ret.ok("msg", msg).set("data", data);
	}
	public boolean isOk(Integer param){
		return param!=null&&param>0;
	}
	public boolean isOk(Date param){
		return param!=null;
	}
	/**
	 * 得到新数据的排序Rank值 默认从1开始 不带任何查询条件
	 * @return
	 */
	public int getNextSortRank(){
		return getNextSortRank(null, false);
	}
	/**
	 * 得到新数据的排序Rank值 从0开始 不带任何查询条件
	 * @return
	 */
	public int getNextRankFromZero(){
		return getNextSortRank(null, true);
	}
	/**
	 * 得到新数据的排序Rank值 从0开始 带查询条件
	 * @param fromZero
	 * @return
	 */
	public int getNextRankFromZero(Kv paras){
		return getNextSortRank(paras, true);
	}
	/**
	 * 得到新数据的排序Rank值 自带简单条件查询默认从1开始
	 * @param paras
	 * @return
	 */
	public int getNextSortRank(Kv paras){
		return getNextSortRank(paras, false);
	}
	/**
	 * 得到新数据的排序Rank值 自带简单条件查询 可以自定义是否从零开始
	 * @param paras
	 * @param fromZero
	 * @return
	 */
	public int getNextSortRank(Kv paras,boolean  fromZero){
		int count=getCount(paras);
		if(fromZero){
			return count;
		}
		return count+1;
	}
	/**
	 * 得到新数据的排序Rank值 自带简单条件查询 可以自定义是否从零开始
	 * 条件可定制版
	 * @param kv
	 * @param fromZero
	 * @return
	 */
	public int getNextSortRank(Kv paras,Boolean customCompare,boolean  fromZero){
		int count=getCount(paras, customCompare);
		if(fromZero){
			return count;
		}
		return count+1;
	}
	
	public boolean notOk(Integer param){
		return param==null||param<=0;
	}
	public boolean notOk(Date param){
		return param==null;
	}
	public boolean notOk(Boolean param){
		return param==null;
	}
	public boolean isOk(Boolean param){
		return param!=null;
	}
	public boolean isOk(String param){
		return StrKit.notBlank(param);
	}
	public boolean isOk(Serializable[] param){
		return ArrayUtil.notEmpty(param);
	}
	public boolean notOk(Serializable[] param){
		return ArrayUtil.isEmpty(param);
	}
	public boolean notOk(String param){
		return StrKit.isBlank(param);
	}
	private String tableName;
	/**
	 * 获取表名
	 * @return
	 */
	public String table(){
		if(tableName==null){
			tableName=TableMapping.me().getTable(dao().getClass()).getName();
		}
		return tableName;
	}
	/**
	 * 得到所有数据
	 * @return
	 */
	 public List<M> findAll(){
		 return dao().findAll();
	 }
	/**
	 * 抽象方法定义 dao 让调用者自己实现
	 * @return
	 */
	protected abstract M dao();
	/**
	 * 常用的得到列表数据的方法
	 * 不分页版
	 * 不能排序
	 * 自定义参数compare
	 * @param columns
	 * @param paras
	 * @param customCompare
	 * @return
	 */
	public List<M> getCommonList(String columns,Kv paras,boolean customCompare){
		Kv conf=Kv.by("table", table()).set("myparas", paras).set("customCompare",customCompare);
		if(isOk(columns)){
			conf.set("columns",columns);
		}
		return dao().find(Db.getSqlPara("common.list",conf));
	}
	/**
	 * 常用的得到列表数据的方法
	 * 不分页版
	 * 不能排序
	 * 可以自定义参数compare 默认=
	 * @param paras
	 * @return
	 */
	public List<M> getCommonList(Kv paras,boolean customCompare){
		return getCommonList("*",paras, customCompare);
	}
	/**
	 * 得到下拉列表数据
	 * @param textColumn
	 * @param valueColumn
	 * @param paras
	 * @return
	 */
	public List<Record> getOptionList(String textColumn,String valueColumn,Kv paras){
		Kv conf=Kv.by("table", table()).set("value",valueColumn).set("text",textColumn).set("myparas", paras).set("customCompare",false);
		return Db.find(Db.getSqlPara("common.optionlist",conf));
	}
	/**
	 * 得到下拉列表数据
	 * @param textColumn
	 * @param valueColumn
	 * @return
	 */
	public List<Record> getOptionList(String textColumn,String valueColumn){
		Kv conf=Kv.by("table", table()).set("value",valueColumn).set("text",textColumn).set("customCompare",false);
		return Db.find(Db.getSqlPara("common.optionlist",conf));
	}
	/**
	 * 得到下拉列表数据
	 * @return
	 */
	public List<Record> getOptionList(){
		Kv conf=Kv.by("table", table()).set("value","id").set("text","name").set("customCompare",false);
		return Db.find(Db.getSqlPara("common.optionlist",conf));
	}
	/**
	 * 常用的得到列表数据的方法
	 * 不分页版
	 * 不能排序
	 * 不能自定义参数compare 默认=
	 * @param paras
	 * @return
	 */
	public List<M> getCommonList(Kv paras){
		return getCommonList("*",paras, false);
	}
	/**
	 * 常用的得到列表数据的方法
	 * 不分页版
	 * 不能排序
	 * 不能自定义参数compare 默认=
	 * @param columns 指定查询的列
	 * @param paras
	 * @return
	 */
	public List<M> getCommonList(String columns,Kv paras){
		return getCommonList(columns,paras,false);
	}
	/**
	 * 常用的得到列表数据的方法
	 * 不分页版
	 * 默认正序 
	 * 不能自定义参数compare 默认=
	 * @param paras
	 * @param orderColums
	 * @return
	 */
	public List<M> getCommonList(Kv paras,String orderColums){
		int count=StrUtil.count(orderColums, ",");
		String orderTypes="";
		for(int i=0;i<=count;i++){
			if(i==0){
				orderTypes="asc";
			}else{
				orderTypes=orderTypes+","+"asc";
			}
		}
		return getCommonList("*",paras, orderColums, orderTypes, false);
	}
	/**
	 * 常用的得到列表数据的方法
	 * 不分页版
	 * 可以排序
	 * 不能自定义参数compare 默认=
	 * @param paras
	 * @param orderColumns
	 * @param orderTypes
	 * @return
	 */
	public List<M> getCommonList(Kv paras,String orderColumns,String orderTypes){
		return getCommonList("*",paras, orderColumns, orderTypes, false);
	}
	/**
	 * 常用的得到列表数据的方法
	 * 不分页版
	 * 可以排序
	 * 不能自定义参数compare 默认=
	 * @param columns
	 * @param paras
	 * @param sort
	 * @param orderType
	 * @return
	 */
	public List<M> getCommonList(String columns,Kv paras,String orderColumns,String orderTypes){
		return getCommonList(columns,paras, orderColumns, orderTypes, false);
	}
	/**
	 * 常用的得到列表数据的方法
	 * 不分页版
	 * 可以排序
	 * 自定义参数compare
	 * @param columns
	 * @param paras
	 * @param orderColumn
	 * @param orderType
	 * @param customCompare
	 * @return
	 */
	public List<M> getCommonList(String columns,Kv paras,String orderColumns,String orderTypes,boolean customCompare){
		Kv conf=Kv.by("table", table()).set("myparas", paras).set("customCompare",customCompare);
		if(isOk(columns)){
			conf.set("columns",columns);
		}
		if(isOk(orderColumns)){
			conf.set("orderColumns",ArrayUtil.from(orderColumns, ","));
		}
		if(isOk(orderTypes)){
			conf.set("orderTypes",ArrayUtil.from(orderTypes, ","));
		}
		return dao().find(Db.getSqlPara("common.list",conf));
	}
	/**
	 * 常用的得到分页列表数据的方法
	 * 可以排序
	 * 自定义参数compare
	 * 分页查询
	 * @param columns
	 * @param paras
	 * @param orderColumns
	 * @param orderTypes
	 * @param pageNumber
	 * @param pageSize
	 * @param customCompare
	 * @param or
	 * @return
	 */
	public Page<M> paginate(String columns,Kv paras,String orderColumns,String orderTypes,int pageNumber, int pageSize,boolean customCompare,boolean or){
		Kv conf=Kv.by("table", table()).set("myparas", paras).set("customCompare",customCompare);
		conf.set("or",or);
		if(isOk(columns)){
			conf.set("columns",columns);
		}
		if(isOk(orderColumns)){
			conf.set("orderColumns",ArrayUtil.from(orderColumns, ","));
		}
		if(isOk(orderTypes)){
			conf.set("orderTypes",ArrayUtil.from(orderTypes, ","));
		}
		return dao().paginate(pageNumber,pageSize,Db.getSqlPara("common.list",conf));
	}
	
	/**
	 * 常用的得到分页列表数据的方法
	 * 可以排序
	 * 条件都是等于
	 * 分页查询
	 * @param columns
	 * @param paras
	 * @param orderColumns
	 * @param orderTypes
	 * @param pageNumber
	 * @param pageSize
	 * @return
	 */
	public Page<M> paginate(String columns,Kv paras,String orderColumns,String orderTypes,int pageNumber, int pageSize){
		return paginate(columns, paras, orderColumns, orderTypes, pageNumber, pageSize, false,false);
	}
	/**
	 * 常用的得到分页列表数据的方法
	 * 可以排序
	 * 条件都是等于
	 * 分页查询
	 * @param paras
	 * @param orderColumns
	 * @param orderTypes
	 * @param pageNumber
	 * @param pageSize
	 * @param customCompare
	 * @param or
	 * @return
	 */
	public Page<M> paginate(Kv paras,String orderColumns,String orderTypes,int pageNumber, int pageSize,boolean customCompare,boolean or){
		return paginate("*", paras, orderColumns, orderTypes, pageNumber, pageSize, customCompare,or);
	}
	/**
	 * 常用的得到分页列表数据的方法
	 * 可以排序
	 * 条件都是等于
	 * 分页查询
	 * @param paras
	 * @param orderColumns
	 * @param orderType
	 * @param pageNumber
	 * @param pageSize
	 * @param customCompare
	 * @return
	 */
	public Page<M> paginate(Kv paras,String orderColumns,String orderTypes,int pageNumber, int pageSize,boolean customCompare){
		return paginate("*", paras, orderColumns, orderTypes, pageNumber, pageSize, customCompare,false);
	}
	/**
	 * 常用的得到分页列表数据的方法
	 * 可以排序
	 * 条件都是等于
	 * 分页查询
	 * @param paras
	 * @param orderColumns
	 * @param orderTypes
	 * @param pageNumber
	 * @param pageSize
	 * @return
	 */
	public Page<M> paginate(Kv paras,String orderColumns,String orderTypes,int pageNumber, int pageSize){
		return paginate("*", paras, orderColumns, orderTypes, pageNumber, pageSize, false,false);
	}
	/**
	 * 常用的得到分页列表数据的方法
	 * 可以排序
	 * 条件都是等于
	 * 分页查询
	 * @param orderColumns
	 * @param orderTypes
	 * @param pageNumber
	 * @param pageSize
	 * @return
	 */
	public Page<M> paginate(String orderColumns,String orderTypes,int pageNumber, int pageSize){
		return paginate("*", null, orderColumns, orderTypes, pageNumber, pageSize, false,false);
	}
	/**
	 * 常用的得到分页列表数据的方法
	 * 不尅一可以排序
	 * 条件自定义 customCompare
	 * 分页查询
	 * @param columns
	 * @param paras
	 * @param pageNumber
	 * @param pageSize
	 * @param customCompare
	 * @return
	 */
	public Page<M> paginate(String columns,Kv paras,int pageNumber, int pageSize,boolean customCompare){
		Kv conf=Kv.by("table", table()).set("myparas", paras).set("customCompare",customCompare);
		if(isOk(columns)){
			conf.set("columns",columns);
		}
		return dao().paginate(pageNumber,pageSize,Db.getSqlPara("common.list",conf));
	}
	/**
	 * 常用的得到分页列表数据的方法
	 * 不可以排序
	 * 条件自定义 customCompare
	 * 分页查询
	 * @param columns
	 * @param paras
	 * @param pageNumber
	 * @param pageSize
	 * @param customCompare
	 * @return
	 */
	public Page<M> paginate(Kv paras,int pageNumber, int pageSize,boolean customCompare){
		return paginate("*", paras, pageNumber, pageSize, customCompare);
	}
	/**
	 * 常用的得到分页列表数据的方法
	 * 不可以排序
	 * 分页查询
	 * @param columns
	 * @param paras
	 * @param pageNumber
	 * @param pageSize
	 * @param customCompare
	 * @return
	 */
	public Page<M> paginate(Kv paras,int pageNumber, int pageSize){
		return paginate("*", paras, pageNumber, pageSize, false);
	}
	/**
	 * 常用的得到分页列表数据的方法
	 * 不可以排序
	 * 分页查询
	 * @param columns
	 * @param paras
	 * @param pageNumber
	 * @param pageSize
	 * @param customCompare
	 * @return
	 */
	public Page<M> paginate(Kv paras,int pageSize,boolean customCompare){
		return paginate("*", paras, 1, pageSize, customCompare);
	}
	/**
	 * 常用的得到分页列表数据的方法
	 * 不可以排序
	 * 分页查询
	 * @param pageNumber
	 * @param pageSize
	 * @return
	 */
	public Page<M> paginate(int pageNumber,int pageSize){
		return paginate("*", null, pageNumber, pageSize, false);
	}
	/**
	 * 常用的得到分页列表数据的方法
	 * 不可以排序
	 * 分页查询
	 * @param paras
	 * @param pageSize
	 * @return
	 */
	public Page<M> paginate(Kv paras,int pageSize){
		return paginate("*", paras, 1, pageSize, false);
	}
	/**
	 * 常用的得到分页列表数据的方法
	 * 不可以排序
	 * 分页查询
	 * @param pageSize
	 * @return
	 */
	public Page<M> paginate(int pageSize){
		return paginate("*", null, 1, pageSize, false);
	}
	
	
	/**
	 * 通用根据ID删除数据
	 * @param id
	 * @return
	 */
	public Ret deleteById(Integer id){
		return deleteById(id, false);
	}
	
	/**
	 * 通用根据ID删除数据 需要先检查是否被其他地方使用
	 * @param id
	 * @param checkInUse
	 * @return
	 */
	public Ret deleteById(Integer id,boolean checkInUse){
		if(notOk(id)){
			return fail(Msg.PARAM_ERROR);
		}
		M m=dao().findById(id);
		if(m==null){
			return fail(Msg.DATA_NOT_EXIST);
		}
		if(checkInUse){
			String msg=checkInUse(m);
			if(StrKit.notBlank(msg)){return fail(msg);}
		}
		
		boolean success=m.deleteById(id);
		return success?success(m,Msg.SUCCESS):fail(Msg.FAIL);
	}
	/**
	 * 检测数据是否被其它数据外键引用
	 * @param model
	 * @return
	 */
	public String checkInUse(M model){
		return null;
	}
	/**
	 * 检测数据是否字段是否可以执行切换true false
	 * @param kv
	 * @param model
	 * @param column
	 * @return
	 */
	public String checkCanToggle(Kv kv,M model,String column){
		return null;
	}
	/**
	 * 检测数据是否字段是否可以执行切换true false
	 * @param model
	 * @param column
	 * @return
	 */
	public String checkCanToggle(M model,String column){
		return null;
	}
	/**
	 * 额外需要处理toggle操作
	 * @param kv
	 * @param model
	 * @param column
	 * @return
	 */
	public String toggleExtra(Kv kv,M model,String column){
		return null;
	}
	
	/**
	 * 额外需要处理toggle操作
	 * @param model
	 * @param column
	 * @return
	 */
	public String toggleExtra(M model,String column){
		return null;
	}
	
	/**
	 * 判断字典名是否存在 排除指定ID
	 * @param name
	 * @param id
	 * @return
	 */
	public boolean existsName(String name,Integer id) {
		name = name.toLowerCase().trim();
		String sql = "select id from "+table()+" where name = ? and id != ? limit 1";
		Integer existId = Db.queryInt(sql, name, id);
		return isOk(existId);
	}
	/**
	 * 判断是否存在
	 * @param columnName
	 * @param value
	 * @param id
	 * @return
	 */
	public boolean exists(String columnName,Serializable value,Integer id) {
		value = value.toString().toLowerCase().trim();
		String sql = "select id from "+table()+" where "+columnName+" = ? and id!=? limit 1";
		Integer existId = Db.queryInt(sql, value,id);
		return isOk(existId);
	}
	/**
	 * 判断是否存在
	 * @param columnName
	 * @param value
	 * @return
	 */
	public boolean exists(String columnName,Serializable value) {
		return exists(columnName, value, -1);
	}
	/**
	 * 判断字典名是否存在
	 * @param name
	 * @return
	 */
	public boolean existsName(String name) {
		return existsName(name, -1);
	}
	/**
	 * 根据ID获得一条数据
	 * @param id
	 * @return
	 */
	public M findById(Integer id) {
		if(notOk(id)){return null;}
		return dao().findById(id);
	}
	/**
	 * 得到符合条件的第一个
	 * @param paras
	 * @return
	 */
	public M findFirst(Kv paras) {
		return findFirst(paras,false);
	}
	/**
	 * 得到符合条件的第一个
	 * @return
	 */
	public M findFirst() {
		return findFirst(null,false);
	}
	/**
	 * 得到符合条件的第一个
	 * @param paras
	 * @param customCompare
	 * @return
	 */
	public M findFirst(Kv paras,boolean customCompare) {
		Kv conf=Kv.by("table", table());
		if(paras!=null){
			conf.set("myparas", paras);
		}
		conf.set("customCompare",customCompare);
		SqlPara sqlPara=Db.getSqlPara("common.first", conf);
		return dao().findFirst(sqlPara);
	}
	/**
	 * 根据条件删除数据
	 * @param paras
	 * @param customCompare
	 * @return
	 */
	public void deleteBy(Kv paras,boolean customCompare) {
		Kv conf=Kv.by("table", table()).set("myparas", paras).set("customCompare",customCompare);
		SqlPara sqlPara=Db.getSqlPara("common.delete", conf);
		Db.delete(sqlPara.getSql(), sqlPara.getPara());
	}
	/**
	 * 根据条件删除数据
	 * @param paras
	 * @return
	 */
	public void deleteBy(Kv paras) {
		deleteBy(paras, false);
	}
	
	/**
	 * 常用的得到列表数据数量
	 * 自定义参数compare
	 * @param paras
	 * @param customCompare
	 * @return
	 */
	public int getCount(Kv paras,boolean customCompare){
		Kv conf=Kv.by("table", table()).set("myparas", paras).set("customCompare",customCompare);
		SqlPara sql=Db.getSqlPara("common.count",conf);
		return Db.queryInt(sql.getSql());
	}
	/**
	 * 常用的得到列表数据数量
	 * @param paras
	 * @return
	 */
	public int getCount(Kv paras){
		return getCount(paras, false);
	}
	/**
	 * 常用的得到列表数据数量
	 * @return
	 */
	public int getCount(){
		return getCount(null, false);
	}
	/**
	 * 添加拼接like
	 * @param column
	 */
	public String columnLike(String column){
		return " like '%"+column+"%'";
	}
	/**
	 * 切换Boolean类型字段
	 * @param id 需要切换的数据ID
	 * @param columns 需要切换的字段列表
	 * @return
	 */
	public Ret toggleBoolean(Integer id,String... columns) {
		return toggleBoolean(null, id, columns);
	}
	/**
	 * 切换Boolean类型字段值
	 * @param kv 额外传入的参数 用于 toggleExtra里用
	 * @param id 需要切换的数据ID
	 * @param columns 需要切换的字段列表
	 * @return
	 */
	public Ret toggleBoolean(Kv kv,Integer id,String... columns) {
		if(notOk(id)){
			return fail(Msg.PARAM_ERROR);
		}
		M model=findById(id);
		if(model==null){
			return fail(Msg.DATA_NOT_EXIST);
		}
		
		Table table =TableMapping.me().getTable(dao().getClass());
		if (table != null) {
			for(String column:columns){
				if(!table.hasColumnLabel(column)){
					throw new ActiveRecordException("The attribute name does not exist: \"" + column + "\"");
				}
			}
			Boolean value;
			for(String column:columns){
				String msg=checkCanToggle(kv,model,column);
				if(StrKit.notBlank(msg)){
					return fail(msg);
				}
				value=model.getBoolean(column);
				model.set(column, value==null?true:!value);
				//处理完指定这个字段 还需要额外处理什么？
				if(kv==null){
					msg=toggleExtra(model,column);
				}else{
					msg=toggleExtra(kv,model,column);
				}
				if(StrKit.notBlank(msg)){
					return fail(msg);
				}
			}
			boolean success=model.update();
			return success?success(model,Msg.SUCCESS):fail(Msg.FAIL);
		}
		
		return fail(Msg.FAIL);
	}
	
	/**
	 * 调用日志服务 添加日志信息 操作类型是Save
	 * @param targetId 关联操作目标数据的ID
	 * @param userId 操作人
	 * @param targetType 操作目标的ID
	 * @param modelName  操作目标的具体数据的名字
	 */
	public void addSaveSystemLog(Integer targetId, Integer userId,int targetType,String modelName) {
		addSystemLog(targetId, userId, SystemLog.TYPE_SAVE, targetType, modelName, null);
	}
	/**
	 * 调用日志服务 添加日志信息 操作类型是Update
	 * @param targetId 关联操作目标数据的ID
	 * @param userId 操作人
	 * @param targetType 操作目标的ID
	 * @param modelName  操作目标的具体数据的名字
	 */
	public void addUpdateSystemLog(Integer targetId, Integer userId,int targetType,String modelName) {
		addSystemLog(targetId, userId, SystemLog.TYPE_UPDATE, targetType, modelName, null);
	}
	/**
	 * 调用日志服务 添加日志信息 操作类型是Update
	 * @param targetId 关联操作目标数据的ID
	 * @param userId 操作人
	 * @param targetType 操作目标的ID
	 * @param modelName  操作目标的具体数据的名字
	 * @param append  额外信息
	 */
	public void addUpdateSystemLog(Integer targetId, Integer userId,int targetType,String modelName,String append) {
		addSystemLog(targetId, userId, SystemLog.TYPE_UPDATE, targetType, modelName, append);
	}
	/**
	 * 调用日志服务 添加日志信息 操作类型是Delete
	 * @param targetId 关联操作目标数据的ID
	 * @param userId 操作人
	 * @param targetType 操作目标的ID
	 * @param modelName  操作目标的具体数据的名字
	 */
	public void addDeleteSystemLog(Integer targetId, Integer userId,int targetType,String modelName) {
		addSystemLog(targetId, userId, SystemLog.TYPE_DELETE, targetType, modelName, null);
	}
	/**
	 * 调用日志服务 添加日志信息
	 * @param targetId 关联操作目标数据的ID
	 * @param userId 操作人
	 * @param type   操作类型
	 * @param targetType 操作目标的ID
	 * @param modelName  操作目标的具体数据的名字
	 */
	public void addSystemLog(Integer targetId, Integer userId, int type,int targetType,String modelName) {
		addSystemLog(targetId, userId, type, targetType, modelName, null);
	}
	/**
	 * 调用日志服务 添加日志信息
	 * @param targetId 关联操作目标数据的ID
	 * @param userId 操作人
	 * @param type   操作类型
	 * @param targetType 操作目标的ID
	 * @param modelName  操作目标的具体数据的名字
	 * @param append  额外信息
	 */
	public void addSystemLog(Integer targetId, Integer userId, int type,int targetType,String modelName,String append) {
		String userName=CACHE.me.getUserName(userId);
		StringBuilder title=new StringBuilder();
		title.append("<span class='text-danger'>[").append(userName).append("]</span>")
		.append(SystemLogService.typeName(type))
		.append(SystemLogService.targetTypeName(targetType))
		.append("<span class='text-danger'>[").append(modelName).append("]</span>");
		if(StrKit.notBlank(append)){
			title.append(append);
		}
		//调用底层方法
		addSystemLogWithTitle(targetId, userId, type, targetType, title.toString());
	}
	/**
	 * 调用日志服务 添加日志信息
	 * @param targetId
	 * @param userId
	 * @param type
	 * @param targetType
	 * @param title
	 */
	public void addSystemLogWithTitle(Integer targetId, Integer userId, int type,int targetType,String title) {
		String userName=CACHE.me.getUserName(userId);
		systeLogService.saveLog(type, targetType, targetId, title.toString(), 0, userId,userName);
	}
}
