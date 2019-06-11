package cn.jbolt.common.safe;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;

/**
 * @desc 处理Xss漏洞
 */
public class HttpServletRequestWrapper extends javax.servlet.http.HttpServletRequestWrapper{

    private HttpServletRequest request;

    public HttpServletRequestWrapper(HttpServletRequest request) {
        super(request);
        this.request = request;
    }
    /**
     * 重写getmeter方法
     */
    @Override
    public String getParameter(String name) {
        String value = this.request.getParameter(name);
        if (value == null)
            return null;
        value = format(value);
        return value;
    }
    /**
     * 重写getmeterMap
     */
    @SuppressWarnings("unchecked")
    @Override
    public Map<String, String[]> getParameterMap() {
        HashMap<String, String[]> paramMap = (HashMap<String, String[]>) super.getParameterMap();
        paramMap = (HashMap<String, String[]>) paramMap.clone();

        for (Iterator iterator = paramMap.entrySet().iterator(); iterator.hasNext(); ) {
            Map.Entry<String, String[]> entry = (Map.Entry<String, String[]>) iterator.next();
            String [] values = entry.getValue();
            for (int i = 0; i < values.length; i++) {
                if(values[i] instanceof String){
                    values[i] = format(values[i]);
                }
            }
            entry.setValue(values);
        }
        return paramMap;
    }
    public String filter(String message) {
        if (message == null)
            return (null);
        message = format(message);
        return message;
    }
    /**
     *  @desc jsoup处理
     *  @param str 
     */
    private String format(String str) {
        return  Jsoup.clean(str, Whitelist.basicWithImages());
    }
}
