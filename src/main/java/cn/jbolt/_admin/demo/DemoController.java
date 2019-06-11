package cn.jbolt._admin.demo;

import com.jfinal.aop.Inject;
import com.jfinal.kit.JsonKit;

import cn.jbolt._admin.dictionary.DictionaryService;
import cn.jbolt._admin.permission.CheckPermission;
import cn.jbolt._admin.permission.PermissionKey;
import cn.jbolt.base.BaseController;
import cn.jbolt.common.model.Demotable;
import cn.jbolt.common.util.ArrayUtil;
import cn.jbolt.common.util.DateUtil;

/**
* @author 小木 qq 909854136
* @version 创建时间：2019年3月19日 下午11:45:18
*/
@CheckPermission(PermissionKey.DEMO)
public class DemoController extends BaseController {
	@Inject
	private DictionaryService dictionaryService;
	
	public void index(){
		render("index.html");
	}
	
	public void temp(){
		if(isAjax()){
			renderJsonSuccess();
		}else{
			renderHtml("<h2>这是Demo临时测试界面</h2>");
		}
	
	}
	
	public void submit(){
		String[] aihao=getParaValues("aihao");
		if(aihao!=null&&aihao.length>0){
			set("aihao", ArrayUtil.join(aihao, ","));
		}
		String[] aihaoCheck=getParaValues("aihaoCheck");
		if(aihaoCheck!=null&&aihaoCheck.length>0){
			set("aihaoCheck", ArrayUtil.join(aihaoCheck, ","));
		}
		keepPara();
		render("index.html");
	}
	public void btn(){
		render("btn.html");
	}
	public void submitDateAndTime(){
		System.out.println(getDateTime("datetime"));
		String time=get("time");
		System.out.println(time);
		System.out.println(DateUtil.getTime(time));
		Demotable demotable=getModel(Demotable.class,"dt");
		System.out.println(JsonKit.toJson(demotable));
		keepModel(Demotable.class,"dt");
		keepPara();
		render("index.html");
	}
	
	public void submitLayDate(){
		keepPara();
		render("index.html");
	}
	/**
	 * 前端组件数据源Demo
	 */
	public void dictionary(){
		renderJsonData(dictionaryService.getOptionListByType(get("key")));
	}
	
}
