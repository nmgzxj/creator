package cn.jbolt.common.bean;

public class OptionBean implements Option {
	private String text;
	private Object value;
	private Boolean selected;
	public OptionBean(){}
	public OptionBean(String text,Object value){
		this.text=text;
		this.value=value;
	}
	public OptionBean(String text,Object value,Boolean selected){
		this.text=text;
		this.value=value;
		this.selected=selected;
	}
	@Override
	public Object getValue() {
		return value;
	}

	@Override
	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public void setValue(Object value) {
		this.value = value;
	}
	public Boolean getSelected() {
		return selected;
	}
	public void setSelected(Boolean selected) {
		this.selected = selected;
	}

}
