package cn.jbolt.common.bean;
/**
 * 投资机构筛选条件
 * @author mmm
 *
 */
public class InvestFilter {
	private String investOrgan;//机构属性
	private String investCurrency;//投资币种
	private String  capitalStage;//资本阶段
	private String  focusArea;//细分领域
	public String getInvestOrgan() {
		return investOrgan;
	}
	public void setInvestOrgan(String investOrgan) {
		this.investOrgan = investOrgan;
	}
	public String getInvestCurrency() {
		return investCurrency;
	}
	public void setInvestCurrency(String investCurrency) {
		this.investCurrency = investCurrency;
	}
	public String getCapitalStage() {
		return capitalStage;
	}
	public void setCapitalStage(String capitalStage) {
		this.capitalStage = capitalStage;
	}
	public String getFocusArea() {
		return focusArea;
	}
	public void setFocusArea(String focusArea) {
		this.focusArea = focusArea;
	}
	
}
