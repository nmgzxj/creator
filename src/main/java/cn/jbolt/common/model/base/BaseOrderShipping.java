package cn.jbolt.common.model.base;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.IBean;

/**
 * Generated by JFinal, do not modify this file.
 */
@SuppressWarnings({"serial", "unchecked"})
public abstract class BaseOrderShipping<M extends BaseOrderShipping<M>> extends Model<M> implements IBean {

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

	public M setName(java.lang.String name) {
		set("name", name);
		return (M)this;
	}
	
	public java.lang.String getName() {
		return getStr("name");
	}

	public M setPhone(java.lang.String phone) {
		set("phone", phone);
		return (M)this;
	}
	
	public java.lang.String getPhone() {
		return getStr("phone");
	}

	public M setMobile(java.lang.String mobile) {
		set("mobile", mobile);
		return (M)this;
	}
	
	public java.lang.String getMobile() {
		return getStr("mobile");
	}

	public M setWxuserNickname(java.lang.String wxuserNickname) {
		set("wxuserNickname", wxuserNickname);
		return (M)this;
	}
	
	public java.lang.String getWxuserNickname() {
		return getStr("wxuserNickname");
	}

	public M setProvince(java.lang.String province) {
		set("province", province);
		return (M)this;
	}
	
	public java.lang.String getProvince() {
		return getStr("province");
	}

	public M setCity(java.lang.String city) {
		set("city", city);
		return (M)this;
	}
	
	public java.lang.String getCity() {
		return getStr("city");
	}

	public M setCounty(java.lang.String county) {
		set("county", county);
		return (M)this;
	}
	
	public java.lang.String getCounty() {
		return getStr("county");
	}

	public M setAddress(java.lang.String address) {
		set("address", address);
		return (M)this;
	}
	
	public java.lang.String getAddress() {
		return getStr("address");
	}

	public M setZipcode(java.lang.String zipcode) {
		set("zipcode", zipcode);
		return (M)this;
	}
	
	public java.lang.String getZipcode() {
		return getStr("zipcode");
	}

	public M setCreateTime(java.util.Date createTime) {
		set("createTime", createTime);
		return (M)this;
	}
	
	public java.util.Date getCreateTime() {
		return get("createTime");
	}

	public M setUpdateTime(java.util.Date updateTime) {
		set("updateTime", updateTime);
		return (M)this;
	}
	
	public java.util.Date getUpdateTime() {
		return get("updateTime");
	}

	public M setUpdateUserId(java.lang.Integer updateUserId) {
		set("updateUserId", updateUserId);
		return (M)this;
	}
	
	public java.lang.Integer getUpdateUserId() {
		return getInt("updateUserId");
	}

}
