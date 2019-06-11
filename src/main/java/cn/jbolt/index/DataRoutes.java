package cn.jbolt.index;

import cn.jbolt._admin.demo.DemoController;
import cn.jbolt._admin.interceptor.AdminAuthInterceptor;
import cn.jbolt.data.rental.RentalController;
import com.jfinal.config.Routes;

/**
 * demo测试路由配置
 * @ClassName:  DemoRoutes   
 * @author: JFinal学院-小木 QQ：909854136 
 * @date:   2019年3月26日 下午12:25:32   
 *     
 * 注意：本内容仅限于JFinal学院 JBolt平台VIP成员内部传阅，请尊重开发者劳动成果，不要外泄出去用于其它商业目的
 */
public class DataRoutes extends Routes {

	@Override
	public void config() {
		this.setBaseViewPath("/_view/data");
		this.addInterceptor(new PjaxInterceptor());
		this.addInterceptor(new AdminAuthInterceptor());
		this.add("/admin/data/rental", RentalController.class,"/rental");
	}

}
