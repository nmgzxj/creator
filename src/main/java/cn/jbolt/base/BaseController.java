package cn.jbolt.base;

import java.util.Date;

import com.jfinal.core.Controller;
import com.jfinal.core.NotAction;
import com.jfinal.kit.Ret;
import com.jfinal.kit.StrKit;
import com.jfinal.upload.UploadFile;

import cn.jbolt.common.config.JBoltTimestampConverter;
import cn.jbolt.common.config.SessionKey;
import cn.jbolt.common.model.User;
import cn.jbolt.common.util.CACHE;
/**
 * 封装controller层简化用法 和常用方法
 * @author 小木
 *
 */
public class BaseController extends Controller {
	private static final Ret jsonSuccess=Ret.ok();
	protected boolean isOk(Integer param){
		return param!=null&&param>0;
	}
	 
	protected boolean notOk(Integer param){
		return param==null||param<=0;
	}
	protected boolean notImg(UploadFile file){
		String contentType=file.getContentType();
		return (contentType==null||contentType.indexOf("image/")==-1);
	}
	protected boolean isOk(String param){
		return StrKit.notBlank(param);
	}
	protected boolean notOk(String param){
		return StrKit.isBlank(param);
	}
	@NotAction
	public void renderDialogError(String msg) {
		setMsg(msg);
		render("/_view/_admin/common/msg/formerror.html");
	}
	@NotAction
	public void renderFormError(String msg) {
		setMsg(msg);
		render("/_view/_admin/common/msg/formerror.html");
	}
	@NotAction
	public void renderErrorPjax(String msg) {
		setMsg(msg);
		render("/_view/_admin/common/msg/errorpjax.html");
	}
	@NotAction
	public void renderErrorPjax(String msg,String backUrl) {
		setMsg(msg);
		set("backUrl", backUrl);
		render("/_view/_admin/common/msg/errorpjax.html");
	}
	protected void renderErrorPortal(String msg) {
		setMsg(msg);
		render("/_view/_admin/common/msg/errorportal.html");
	}
	protected void renderSuccessPjax(String msg) {
		setMsg(msg);
		render("/_view/_admin/common/msg/successpjax.html");
	}
	protected void renderPjaxRet(Ret ret) {
		if(ret.isOk()){
			renderSuccessPjax(ret.getStr("msg"));
		}else{
			renderErrorPjax(ret.getStr("msg"));
		}
	}
 
	/**
	 * 获取Html5中type="datetime-local"的数据 返回Date
	 * @param name
	 * @return
	 */
	protected Date getDateTime(String name){
		return JBoltTimestampConverter.doConvertDateTime(get(name));
	}
	/**
	 * 判断是否是Ajax请求
	 * @return
	 */
	@NotAction
	public boolean isAjax(){
		String xrequestedwith= getRequest().getHeader("x-requested-with");
		boolean isAjax = false;
		if(xrequestedwith==null||xrequestedwith.equalsIgnoreCase("XMLHttpRequest")==false){
			isAjax=false;
		}else if(xrequestedwith.equalsIgnoreCase("XMLHttpRequest")){
			isAjax=true;
		}
		return isAjax;
	}
	/**
	 * 返回经过格式包装的JSON数据
	 * @param data
	 */
	protected void renderJsonData(Object data){
		renderJson(Ret.ok("data",data));
	}
	/**
	 * 返回经过包装的JSON成功信息
	 */
	protected void renderJsonSuccess(){
		renderJson(jsonSuccess);
	}
	/**
	 * 设置返回的错误信息内容
	 */
	protected Ret setFailMsg(String msg){
		return Ret.fail("msg", msg);
	}
	/**
	 * 返回经过格式包装的错误信息
	 * @param msg
	 */
	@NotAction
	public void renderJsonFail(String msg){
		renderJson(setFailMsg(msg));
	}
	/**
	 * 设置返回信息内容
	 * @param msg
	 */
	protected void setMsg(String msg){
		set("msg", msg);
	}
	
	/**
	 * 获取type参数
	 * @return
	 */
	protected Integer getType() {
		return getInt("type");
	}
	/**
	 * 设置type属性
	 * @return
	 */
	protected void setType(Integer type) {
		set("type", type);
	}
	/**
	 * 常用关键词搜索 获得关键词参数
	 * @return
	 */
	protected String getKeywords() {
		return get("keywords");
	}
	/**
	 * 常用关键词搜索 获得关键词参数
	 * @return
	 */
	protected void setKeywords(String keywords) {
		set("keywords", keywords);
	}
	/**
	 * 常用关键词搜索 获得关键词参数
	 * @return
	 */
	protected String getKeywords(String key) {
		return get(key);
	}
	/**
	 * 获得enable
	 * @return
	 */
	protected Boolean getEnable() {
		return getBoolean("enable");
	}
	/**
	 * 常用获得state状态参数
	 * @return
	 */
	protected Integer getState() {
		return getInt("state");
	}
	/**
	 * 常用获得state状态参数
	 * @return
	 */
	protected Integer getState(Integer defaultValue) {
		return getInt("state",defaultValue);
	}
	/**
	 * 从参数中获得分页 当前要查询第几页的pageNumber
	 * @return
	 */
	protected Integer getPageNumber() {
		return getInt("page", 1);
	}
	/**
	 * 得到后台管理用户登录session UserId
	 * @return
	 */
	protected Integer getSessionAdminUserId(){
		return getSessionAttr(SessionKey.ADMIN_USER_ID);
	}
	/**
	 * 得到后台管理用户登录session roleId
	 * @return
	 */
	@NotAction
	public String getSessionAdminRoleIds(){
		Integer userId=getSessionAdminUserId();
		if(notOk(userId)){return null;}
		User user=CACHE.me.getUser(userId);
		if(user==null){return null;}
		return user.getRoles();
	}
	/**
	 * 设置后台管理用户登录session userId
	 * @return
	 */
	protected void setSessionAdminUserId(Integer userId){
		setSessionAttr(SessionKey.ADMIN_USER_ID,userId);
	}
	
	/**
	 * 得到后台管理用户是否登录
	 * @return
	 */
	@NotAction
	public boolean isAdminLogin(){
		return isOk(getSessionAdminUserId())&&isOk(getSessionAdminRoleIds());
	}
	/**
	 * 判断请求是否是Pjax请求
	 * @return
	 */
	@NotAction
	public boolean isPjax(){
		return getAttr("isPjax",false);
	}
	/**
	 * 设置请求为Pjax请求
	 * @param isPjax
	 */
	@NotAction
	public void setIsPjax(boolean isPjax){
		set("isPjax",isPjax);
	}
	/**
	 * 跳转到登录页面
	 */
	@NotAction
	public void toLogin() {
		redirect("/admin/tologin");
	}
	
}
