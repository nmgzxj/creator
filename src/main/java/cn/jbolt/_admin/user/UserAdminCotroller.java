package cn.jbolt._admin.user;

import java.util.ArrayList;
import java.util.List;

import com.jfinal.aop.Inject;
import com.jfinal.core.JFinal;
import com.jfinal.upload.UploadFile;

import cn.jbolt._admin.permission.CheckPermission;
import cn.jbolt._admin.permission.PermissionKey;
import cn.jbolt._admin.role.RoleService;
import cn.jbolt.base.BaseController;
import cn.jbolt.common.bean.Option;
import cn.jbolt.common.bean.OptionBean;
import cn.jbolt.common.config.Msg;
import cn.jbolt.common.model.User;
@CheckPermission(PermissionKey.USER)
public class UserAdminCotroller extends BaseController {
	@Inject
	private UserService service;
	@Inject
	private RoleService roleService;
	/**
	 * 管理首页
	 */
	public void index(){
		set("dataList", service.findAll());
		render("index.html");
	}
	/**
	 * 上传头像
	 */
	public void uploadAvatar(){
		UploadFile file=getFile("file","user/avatar");
		if(notImg(file)){
			renderJsonFail("请上传图片类型文件");
			return;
		}
		renderJsonData(JFinal.me().getConstants().getBaseUploadPath()+"/user/avatar/"+file.getFileName());
	}
	
	public void sexSelect(){
		List<Option> options=new ArrayList<Option>();
		options.add(new OptionBean("男","男"));
		options.add(new OptionBean("女","女"));
		renderJsonData(options);
	}
	/**
	 * 进入自身密码修改界面
	 */
	@CheckPermission(PermissionKey.USERPWD)
	public void pwd(){
		render("pwd.html");
	}
	/**
	 * 进入重置用户密码界面
	 */
	@CheckPermission(PermissionKey.USER)
	public void editpwd(){
		set("userId", getInt(0));
		render("editpwd.html");
	}
	/**
	 * 重置用户密码
	 */
	@CheckPermission(PermissionKey.USER)
	public void submitpwd(){
		Integer userId=getInt("userId");
		String newPwd=get("newPwd");
		String reNewPwd=get("reNewPwd");
		if(notOk(newPwd)||notOk(reNewPwd)){ 
			renderJsonFail(Msg.PARAM_ERROR);
			return;
		}
		if(newPwd.equals(reNewPwd)==false){
			renderJsonFail("两次新密码输入不一致");
			return;
		}
		renderJson(service.submitPwd(getSessionAdminUserId(),userId,newPwd));
	}
	
	/**
	 * 修改用户自己的密码
	 */
	@CheckPermission(PermissionKey.USERPWD)
	public void updatepwd(){
		String oldPwd=get("oldPwd");
		String newPwd=get("newPwd");
		String reNewPwd=get("reNewPwd");
		if(notOk(oldPwd)||notOk(newPwd)||notOk(reNewPwd)){ 
			renderJsonFail(Msg.PARAM_ERROR);
			return;
			}
		if(newPwd.equals(reNewPwd)==false){
			renderJsonFail("两次新密码输入不一致");
			return;
		}
		renderJson(service.doUpdatePwd(getSessionAdminUserId(),oldPwd,newPwd));
	}
	
	
	/**
	 * 新增
	 */
	public void add(){
		set("roles", roleService.findAll());
		render("add.html");
	}
	/**
	 * 编辑
	 */
	public void edit(){
		set("user", service.findById(getInt(0)));
		set("roles", roleService.findAll());
		render("edit.html");
	}
	/**
	 * 保存
	 */
	public void save(){
		renderJson(service.save(getSessionAdminUserId(),getModel(User.class, "user")));
	}
	/**
	 * 更新
	 */
	public void update(){
		renderJson(service.update(getSessionAdminUserId(),getModel(User.class, "user")));
	}
	/**
	 * 删除
	 */
	public void delete(){
		renderJson(service.delete(getSessionAdminUserId(),getInt(0)));
	}
	/**
	 * 切换启用状态
	 */
	public void toggleEnable(){
		renderJson(service.toggleEnable(getSessionAdminUserId(),getInt(0)));
	}
}
