package cn.jbolt.admin.mall.goodstype;

import cn.jbolt.base.BaseService;
import cn.jbolt.common.model.GoodsTypeBrand;

/**  
 * 类型和品牌关联中间表管理Service 
 * @ClassName:  GoodsTypeBrandService   
 * @author: JFinal学院-小木 QQ：909854136 
 * @date:   2019年4月13日 上午12:56:35   
 *    
 * 注意：本内容仅限于JFinal学院 JBolt平台VIP成员内部传阅，请尊重开发者劳动成果，不要外泄出去用于其它商业目的
 */
public class GoodsTypeBrandService extends BaseService<GoodsTypeBrand> {

	@Override
	protected GoodsTypeBrand dao() {
		return GoodsTypeBrand.dao;
	}
	/**
	 * 检测品牌被使用情况
	 * @param brandId
	 * @return
	 */
	public boolean checkBrandInUse(Integer brandId) {
		return exists("brandId", brandId);
	}
	
	

}
