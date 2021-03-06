package cn.jbolt.common.model.base;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.IBean;

/**
 * Generated by JFinal, do not modify this file.
 */
@SuppressWarnings({"serial", "unchecked"})
public abstract class BaseOrderItem<M extends BaseOrderItem<M>> extends Model<M> implements IBean {

	public M setId(java.lang.Integer id) {
		set("id", id);
		return (M)this;
	}
	
	public java.lang.Integer getId() {
		return getInt("id");
	}

	public M setOrderId(java.lang.Integer orderId) {
		set("orderId", orderId);
		return (M)this;
	}
	
	public java.lang.Integer getOrderId() {
		return getInt("orderId");
	}

	public M setGoodsId(java.lang.Integer goodsId) {
		set("goodsId", goodsId);
		return (M)this;
	}
	
	public java.lang.Integer getGoodsId() {
		return getInt("goodsId");
	}

	public M setGoodsName(java.lang.String goodsName) {
		set("goodsName", goodsName);
		return (M)this;
	}
	
	public java.lang.String getGoodsName() {
		return getStr("goodsName");
	}

	public M setGoodsSubTitle(java.lang.String goodsSubTitle) {
		set("goodsSubTitle", goodsSubTitle);
		return (M)this;
	}
	
	public java.lang.String getGoodsSubTitle() {
		return getStr("goodsSubTitle");
	}

	public M setPrice(java.math.BigDecimal price) {
		set("price", price);
		return (M)this;
	}
	
	public java.math.BigDecimal getPrice() {
		return get("price");
	}

	public M setGoodsCount(java.lang.Integer goodsCount) {
		set("goodsCount", goodsCount);
		return (M)this;
	}
	
	public java.lang.Integer getGoodsCount() {
		return getInt("goodsCount");
	}

	public M setTotalFee(java.math.BigDecimal totalFee) {
		set("totalFee", totalFee);
		return (M)this;
	}
	
	public java.math.BigDecimal getTotalFee() {
		return get("totalFee");
	}

	public M setGoodsImage(java.lang.String goodsImage) {
		set("goodsImage", goodsImage);
		return (M)this;
	}
	
	public java.lang.String getGoodsImage() {
		return getStr("goodsImage");
	}

}
