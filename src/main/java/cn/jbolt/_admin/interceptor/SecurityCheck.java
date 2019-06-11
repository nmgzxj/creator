package cn.jbolt._admin.interceptor;

import java.lang.reflect.Method;

import com.jfinal.core.Controller;

import cn.jbolt._admin.permission.CheckPermission;
import cn.jbolt._admin.permission.UnCheck;

/**
 * 权限检查
 * 
 * @author 小木
 * 
 */
public class SecurityCheck {
    
    public static boolean isNoAnnotation(Method method){
        if (method.getAnnotations()==null||method.getAnnotations().length==0) {
            return true;
        }
        return false;
    }
    public static boolean isUncheck(Method method){
        if (method.isAnnotationPresent(UnCheck.class)) {
            return true;
        }
        return false;
    }
    
 
    public static boolean isPermissionCheck(Method method){
    	if (method.isAnnotationPresent(CheckPermission.class)) {
    		return true;
    	}
    	return false;
    }
    public static boolean isPermissionCheck(Controller controller){
        if (controller.getClass().isAnnotationPresent(CheckPermission.class)) {
            return true;
        }
        return false;
    }
   
    

}
