package cn.jbolt.common.bean;

public class ProjectFilter {
	private Integer state;//状态
	private Integer storageState;//存储属性
	private Integer operateState;//运营状态
	private Integer contactState;//关系属性
	private Boolean isMember;//会员属性
	private String  capitalStage;//资本阶段
	private String  dimensions;//热点区域
	private Boolean isFinancing;//是否正在融资
	private Boolean isSign;//是否已经签约
	public Integer getStorageState() {
		return storageState;
	}
	public void setStorageState(Integer storageState) {
		this.storageState = storageState;
	}
	public Integer getOperateState() {
		return operateState;
	}
	public void setOperateState(Integer operateState) {
		this.operateState = operateState;
	}
	public Integer getContactState() {
		return contactState;
	}
	public void setContactState(Integer contactState) {
		this.contactState = contactState;
	}

	public String getCapitalStage() {
		return capitalStage;
	}
	public void setCapitalStage(String capitalStage) {
		this.capitalStage = capitalStage;
	}
 
	public Boolean getIsFinancing() {
		return isFinancing;
	}
	public void setIsFinancing(Boolean isFinancing) {
		this.isFinancing = isFinancing;
	}
	public Boolean getIsMember() {
		return isMember;
	}
	public void setIsMember(Boolean isMember) {
		this.isMember = isMember;
	}
	public String getDimensions() {
		return dimensions;
	}
	public void setDimensions(String dimensions) {
		this.dimensions = dimensions;
	}
	public Boolean getIsSign() {
		return isSign;
	}
	public void setIsSign(Boolean isSign) {
		this.isSign = isSign;
	}
	public Integer getState() {
		return state;
	}
	public void setState(Integer state) {
		this.state = state;
	}
}
