package cn.jbolt.data.rental;

import com.jfinal.core.Controller;
import com.jfinal.validate.Validator;

public class RentalValidator extends Validator {

    @Override
    protected void validate(Controller c) {
        setShortCircuit(true);

        // TODO Auto-generated method stub
        validateRequiredString("rental.companyName", "msg", "公司不能为空");

//		validateRequiredString("area.maxid", "msg", "消息号不能为空");
        //validateRequiredString("字段", "msg", "消息名称不能为空");
//		checkSensitiveWords(c.getPara("project.name"), "项目名称 name包含敏感词");
//		checkSensitiveWords(c.getPara("project.title"), "项目标题 title 包含敏感词");
//		checkSensitiveWords(c.getPara("project.content"), "项目内容 content 名包含敏感词");
//
//		validateString("project.name", 3, 20, "msg", "项目名称长度要求在3到20个字符");
//
//		String projectName = c.getPara("project.name");
//		// 创建项目
//		if ("save".equals(getActionMethod().getName())) {
//			if (MyProjectService.me.isProjectNameExists(projectName)) {
//				addError("msg", "项目名称已经存在，请使用其她名称");
//			}
//		}
//		// 修改项目
//		else if ("update".equals(getActionMethod().getName())) {
//			int projectId = c.getParaToInt("project.id");
//			if (MyProjectService.me.isProjectNameExists(projectId, projectName)) {
//				addError("msg", "项目名称已经存在，请使用其她名称");
//			}
//		} else {
//			addError("msg", "MyProjectValidator 只能用于 save、update 方法");
//		}
//
//		validateString("project.title", 3, 100, "msg", "标题长度要求在3到100个字符");
//		validateString("project.content", 19, 65536, "msg", "正文内容太少啦，多写点哈");
    }

    @Override
    protected void handleError(Controller c) {
        // TODO Auto-generated method stub
        c.renderJson();
    }

}
