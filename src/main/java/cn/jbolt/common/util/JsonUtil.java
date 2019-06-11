package cn.jbolt.common.util;

import java.util.HashSet;
import java.util.Set;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.JSONSerializer;
import com.alibaba.fastjson.serializer.PropertyPreFilter;
import com.alibaba.fastjson.serializer.SerializerFeature;
/**
 * fastjson 工具类
 * @author Michael
 *
 */
public class JsonUtil {
    private static SerializerFeature feature = SerializerFeature.DisableCircularReferenceDetect;

    public static String toJson(Object obj) {
        return JSON.toJSONString(obj,feature);
    }

    public static String toJsonWith(Object obj, String... param) {
        return JSON.toJSONString(obj,
                new SimplePropertyPreFilter(false, param), feature);
    }

    public static String toJsonWithOut(Object obj, String... param) {
        return JSON.toJSONString(obj,
                new SimplePropertyPreFilter(true, param), feature);
    }

}

class SimplePropertyPreFilter implements PropertyPreFilter {

    private final Class<?> clazz;
    private Set<String> includes = new HashSet<String>();
    private Set<String> excludes = new HashSet<String>();
    private boolean isSkipMode;

    public SimplePropertyPreFilter(boolean isSkipMode, String... properties) {
        this(isSkipMode, null, properties);
    }

    public SimplePropertyPreFilter(boolean isSkipMode, Class<?> clazz,
            String... properties) {
        super();
        this.clazz = clazz;
        this.isSkipMode = isSkipMode;
        includes.add("data");
        includes.add("state");
        includes.add("msg");
        includes.add("pageNumber");
        includes.add("pageSize");
        includes.add("totalPage");
        includes.add("totalRow");
        includes.add("list");
        includes.add("isFirstPage");
        includes.add("isLastPage");

        for (String item : properties) {
            if (item != null) {
                if (isSkipMode) {
                    this.excludes.add(item);
                } else {
                    this.includes.add(item);
                }
            }
        }
    }

    public Class<?> getClazz() {
        return clazz;
    }

    public boolean apply(JSONSerializer serializer, Object source, String name) {
        if (isSkipMode) {
            if (includes.contains(name)) {
                return true;
            }
            if (excludes.contains(name)) {
                return false;
            }
            return true;
        } else {
            if (includes.contains(name)) {
                return true;
            }
            return false;
        }
    }

    public Set<String> getIncludes() {
        return includes;
    }

    public Set<String> getExcludes() {
        return excludes;
    }

    public void setIncludes(Set<String> includes) {
        this.includes = includes;
    }

    public void setExcludes(Set<String> excludes) {
        this.excludes = excludes;
    }

}
