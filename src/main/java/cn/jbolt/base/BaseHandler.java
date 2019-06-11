package cn.jbolt.base;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.jfinal.handler.Handler;
import com.jfinal.kit.HandlerKit;
import com.jfinal.kit.StrKit;

import cn.jbolt.common.config.MainConfig;

public class BaseHandler extends Handler {
	private static final String HTTPS = "https://";
	private static final String MHSP = "://";
	private static final String MH = ":";
	private static final String HTML = ".html";
	private static final String SLASH="/";

	@Override
	public void handle(String target, HttpServletRequest request, HttpServletResponse response, boolean[] isHandled) {
		String contextPath = request.getContextPath();
		request.setAttribute("ctx", contextPath);
		request.setAttribute("pmkey", target);
		if(StrKit.isBlank(contextPath)){
			contextPath=SLASH;
		}else if(contextPath.endsWith(SLASH)==false){
			contextPath=contextPath+SLASH;
		}
		String basePath = null;
		if (MainConfig.NEED_ALWAYS_HTTPS) {
			if (request.getServerPort() == 80) {
				basePath = HTTPS + request.getServerName() + contextPath;
			} else {
				basePath = HTTPS + request.getServerName() + MH + request.getServerPort() + contextPath;
			}
		} else {
			if (request.getServerPort() == 80) {
				basePath = request.getScheme() + MHSP + request.getServerName() + contextPath;
			} else {
				basePath = request.getScheme() + MHSP + request.getServerName() + MH + request.getServerPort()
						+ contextPath;
			}
		}
		request.setAttribute("need_always_https", MainConfig.NEED_ALWAYS_HTTPS);
		request.setAttribute("basepath", basePath);
		
		if (target.endsWith(HTML)) {
			HandlerKit.renderError404(request, response, isHandled);
		} else {
			next.handle(target, request, response, isHandled);
		}

	}

}
