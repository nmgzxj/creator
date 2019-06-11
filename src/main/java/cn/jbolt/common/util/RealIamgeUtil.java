package cn.jbolt.common.util;

import com.jfinal.kit.StrKit;

import cn.jbolt.common.config.MainConfig;

/** 
 * 获取图片真实访问地址  
 * @ClassName:  RealIamgeUtil   
 * @author: JFinal学院-小木 QQ：909854136 
 * @date:   2019年4月14日 下午9:59:54   
 *    
 * 注意：本内容仅限于JFinal学院 JBolt平台VIP成员内部传阅，请尊重开发者劳动成果，不要外泄出去用于其它商业目的
 */
public class RealIamgeUtil {
	public static String get(Object url,Object defaultValue){
		if((url==null||StrKit.isBlank(url.toString().trim()))){
			if(defaultValue!=null&&StrKit.notBlank(defaultValue.toString().trim())){
				return  defaultValue.toString().trim();
			}else{
				return "/assets/img/uploadimg.png";
			}
		}
		
		String urlValue=url.toString().trim();
		if(StrKit.notBlank(MainConfig.BASEUPLOADPATH_PRE)){
			if(urlValue.indexOf(MainConfig.BASEUPLOADPATH_PRE)!=-1){
				urlValue=urlValue.replace(MainConfig.BASEUPLOADPATH_PRE, "");
			}
		}
		
		return urlValue;
	}
}
