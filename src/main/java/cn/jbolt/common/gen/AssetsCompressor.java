package cn.jbolt.common.gen;

import java.util.Date;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import cn.jbolt.common.util.DateUtil;

/** 
 * 资源在线压缩生成  
 * @ClassName:  AssetsCompressor   
 * @author: JFinal学院-小木 QQ：909854136 
 * @date:   2019年4月15日 下午3:26:10   
 * 注意：本内容仅限于JFinal学院 JBolt平台VIP成员内部传阅，请尊重开发者劳动成果，不要外泄出去用于其它商业目的
 */
public class AssetsCompressor {
	/**
	 * 项目的绝对路径
	 */
	private final static String JBOLT_PROJECT_PATH="C:/dev/eclipsejeeneon3/work/jbolt";
	private final static String JS_URL="http://tool.oschina.net/action/jscompress/js_compress?munge=1&linebreakpos=5000";
	private final static String CSS_URL="http://tool.oschina.net/action/jscompress/css_compress?linebreakpos=5000";
	/**
	 * 压缩
	 * @param srcFilePath
	 * @param target
	 */
	private static void go(String url,String srcFilePath,String target){
		System.out.println(target+" 处理中...");
		String jsonResult=HttpUtil.post(url, FileUtil.readUtf8String(srcFilePath));
		System.out.println(jsonResult);
		if(jsonResult!=null&&jsonResult.indexOf("result")!=-1){
			JSONObject jsonObject=JSON.parseObject(jsonResult);
			if(jsonObject.containsKey("result")){
				String content=jsonObject.getString("result");
				if(content!=null&&content.trim().length()>0){
					FileUtil.writeUtf8String(content, target);
				}
			}
		}
	}
	/**
	 * 压缩JS
	 * @param srcFilePath
	 * @param target
	 */
	public static void js(String srcFilePath,String target){
		System.out.println("正在处理JS压缩...");
		go(JS_URL, srcFilePath, target);
		System.out.println("JS压缩完成...");
	}
	/**
	 * 压缩CSS
	 * @param srcFilePath
	 * @param target
	 */
	public static void css(String srcFilePath,String target){
		System.out.println("正在处理CSS压缩...");
		go(CSS_URL, srcFilePath, target);
		System.out.println("CSS压缩完成...");
	}
	
	public static void main(String[] args) {
		//开始压缩jbolt-admin.js
		//js源文件
		String jbolt_admin_js=JBOLT_PROJECT_PATH+"/src/main/webapp/assets/js/jbolt-admin.js";
		//js压缩后文件
		String jbolt_admin_min_js=JBOLT_PROJECT_PATH+"/src/main/webapp/assets/js/jbolt-admin.min.js";
		//执行JS压缩
		AssetsCompressor.js(jbolt_admin_js, jbolt_admin_min_js);
		
		//开始压缩jbolt-wechat-menu.js
		//js源文件
		String jbolt_wechat_menu_js=JBOLT_PROJECT_PATH+"/src/main/webapp/assets/js/jbolt-wechat-menu.js";
		//js压缩后文件
		String jbolt_wechat_menu_min_js=JBOLT_PROJECT_PATH+"/src/main/webapp/assets/js/jbolt-wechat-menu.min.js";
		//执行JS压缩
		AssetsCompressor.js(jbolt_wechat_menu_js, jbolt_wechat_menu_min_js);
		
		//开始压缩jbolt-admin.css
		//css源文件
		String jbolt_admin_css=JBOLT_PROJECT_PATH+"/src/main/webapp/assets/css/jbolt-admin.css";
		//CSS、压缩后文件
		String jbolt_admin_min_css=JBOLT_PROJECT_PATH+"/src/main/webapp/assets/css/jbolt-admin.min.css";
		//执行css压缩
		AssetsCompressor.css(jbolt_admin_css, jbolt_admin_min_css);
		
		
		//开始压缩jbolt-wechat-menu.css
		//css源文件
		String jbolt_wechat_menu_css=JBOLT_PROJECT_PATH+"/src/main/webapp/assets/css/jbolt-wechat-menu.css";
		//CSS、压缩后文件
		String jbolt_wechat_menu_min_css=JBOLT_PROJECT_PATH+"/src/main/webapp/assets/css/jbolt-wechat-menu.min.css";
		//执行css压缩
		AssetsCompressor.css(jbolt_wechat_menu_css, jbolt_wechat_menu_min_css);
		
		//开始压缩login.css
		//css源文件
		String login_css=JBOLT_PROJECT_PATH+"/src/main/webapp/assets/css/login.css";
		//css压缩后文件
		String login_min_css=JBOLT_PROJECT_PATH+"/src/main/webapp/assets/css/login.min.css";
		//执行JS压缩
		AssetsCompressor.css(login_css, login_min_css);
		
		//开始执行html替换
		AssetsCompressor.change();
		
		System.out.println("===提示：压缩后，请手动刷新项目工程目录,获取最新压缩文件===");
	}
	
	private static void change() {
		String version=DateUtil.format(new Date(), DateUtil.YMDHMSS2);
	
		//处理jbolt里 common中的__admin_layout.html中js css
		String admin_layout_html_path=JBOLT_PROJECT_PATH+"/src/main/webapp/_view/_admin/common/__admin_layout.html";
		changeHtml(admin_layout_html_path, version,new String[]{"jbolt-admin"},new String[]{"jbolt-admin"});
		
		//处理jbolt里 common中的__admin_dialog_layout.html中js css
		String admin_dialog_layout_html_path=JBOLT_PROJECT_PATH+"/src/main/webapp/_view/_admin/common/__admin_dialog_layout.html";
		changeHtml(admin_dialog_layout_html_path, version,new String[]{"jbolt-admin"},new String[]{"jbolt-admin"});
		
		//处理jbolt里自定义菜单管理的js css
		String wechat_menu_html_path=JBOLT_PROJECT_PATH+"/src/main/webapp/_view/_admin/_wechat/menu/mgr.html";
		changeHtml(wechat_menu_html_path, version,new String[]{"jbolt-wechat-menu"},new String[]{"jbolt-wechat-menu"});
		
		//处理jbolt 登录界面 js css
		String login_html_path=JBOLT_PROJECT_PATH+"/src/main/webapp/_view/_admin/index/login.html";
		changeHtml(login_html_path, version,new String[]{"jbolt-admin"},new String[]{"login"});
		
	}
	
	private static void changeHtml(String path,String version,String[] jsFiles,String[] cssFiles){
		if(jsFiles==null&&cssFiles==null){
			return;
		}
		String html=FileUtil.readUtf8String(path);
		Document doc=Jsoup.parse(html);
		doc.outputSettings().prettyPrint(true);
		if(jsFiles!=null){
			for(String js:jsFiles){
				Element element=doc.selectFirst("script[src*=\""+js+"\"]");
				if(element!=null){
					String oldsrc=element.attr("src");
					System.out.println("原数据:"+oldsrc);
					String src=element.attr("src");
					if(src.indexOf("js?v=")!=-1){
						src=src.substring(0, src.indexOf("?v="));
					}
					src=src+"?v="+version;
					html=StrUtil.replace(html, oldsrc, src);
					System.out.println("现数据:"+src);
					
				}
			}
		}
		if(cssFiles!=null){
			for(String css:cssFiles){
				Element element=doc.selectFirst("link[href*=\""+css+"\"]");
				if(element!=null){
					String oldHref=element.attr("href");
					System.out.println("原数据:"+oldHref);
					String href=element.attr("href");
					if(href.indexOf("css?v=")!=-1){
						href=href.substring(0, href.indexOf("?v="));
					}
					href=href+"?v="+version;
					html=StrUtil.replace(html, oldHref, href);
					System.out.println("现数据:"+href);
					
				}
			}
		}
//		System.out.println(html);
		FileUtil.writeUtf8String(html, path);
		System.out.println("处理完html："+path);
		
	}
}
