package cn.jbolt.index;

import javax.servlet.http.HttpSession;

import com.jfinal.aop.Clear;
import com.jfinal.aop.Inject;
import com.jfinal.kit.Kv;

import cn.jbolt._admin.permission.CheckPermission;
import cn.jbolt._admin.permission.UnCheck;
import cn.jbolt._admin.updatemgr.DownloadLogService;
import cn.jbolt._admin.user.UserService;
import cn.jbolt.base.BaseController;
import cn.jbolt.common.config.SessionKey;
import cn.jbolt.common.model.DownloadLog;
import cn.jbolt.common.model.User;
import cn.jbolt.common.util.CACHE;

public class AdminIndexController extends BaseController {
	@Inject
	private UserService userService;
	@Inject
	private DownloadLogService downloadLogService;
	@UnCheck
	public void index(){
		render("index.html");
	}
	@UnCheck
	public void menu(){
		set("leftMenus", CACHE.me.getRoleMenus(getSessionAdminRoleIds()));
		render("menu.html");
	}
	@CheckPermission("dashboard")
	public void dashboard(){
		int mainUpdateCount=downloadLogService.getCount(Kv.by("downloadType", DownloadLog.DOWNLOADTYPE_MAINUPDATE));
		int libsUpdateCount=downloadLogService.getCount(Kv.by("downloadType", DownloadLog.DOWNLOADTYPE_LIBSUPDATE));
		int ipCount=downloadLogService.getIpCount();
		int totalCount=mainUpdateCount+libsUpdateCount;
		set("mainUpdateCount", mainUpdateCount);
		set("libsUpdateCount", libsUpdateCount);
		set("totalUpdateCount", totalCount);
		set("ipCount", ipCount);
		/*set("investInstitutionCount", investInstitutionService.getCount());
		set("investorCount", investorService.getCount());
		set("industryFigureCount", industryFigureService.getCount());
		set("projects", projectService.getTopNewProjects());
		set("industryFigures", industryFigureService.getTopNewFigures());
		set("investInstitutions", investInstitutionService.getTopNewInvestInstitutions());
		set("investors", investorService.getTopNewInvestors());*/
		render("dashboard.html");
	}
	@Clear
	public void tologin(){
		render("login.html");
	}
	@Clear
	public void logout(){
		HttpSession session=getSession();
		session.removeAttribute(SessionKey.ADMIN_USER_ID);
		session.invalidate();
		render("login.html");
	}
	@Clear
	public void login(){
		if(validateCaptcha("captcha")){
			User user=userService.getUser(get("username"),get("password"));
			if(user==null){
				renderJsonFail("用户名或密码不正确");
			}else if(user.getEnable()==null||user.getEnable()==false){
				renderJsonFail("用户已被禁用");
			}else if(notOk(user.getRoles())){
				renderJsonFail("用户未设置可登录权限");
			}else{
				setSessionAdminUserId(user.getId());
				renderJsonSuccess();
			}
		}else{
			renderJsonFail("验证码输入错误");
		}
		
	}
	@Clear
	public void captcha(){
		renderCaptcha();
	}
}
