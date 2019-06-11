package cn.jbolt.common.directive;

import java.io.IOException;
import java.util.Date;

import com.jfinal.template.Directive;
import com.jfinal.template.Env;
import com.jfinal.template.TemplateException;
import com.jfinal.template.expr.ast.Expr;
import com.jfinal.template.expr.ast.ExprList;
import com.jfinal.template.io.Writer;
import com.jfinal.template.stat.ParseException;
import com.jfinal.template.stat.Scope;

import cn.jbolt.common.config.JBoltTimestampConverter;

public class DateTimeDirective extends Directive {
	
	private Expr valueExpr;
	private int paraNum;
	private Expr withTExpr;
	
	public void setExprList(ExprList exprList) {
		this.paraNum = exprList.length();
		if (paraNum > 2) {
			throw new ParseException("Wrong number parameter of #datetime directive, two parameters allowed at most", location);
		}
		
		if (paraNum == 0) {
			this.valueExpr = null;
		} else if (paraNum == 1) {
			this.valueExpr = exprList.getExpr(0);
		} else if (paraNum == 2) {
			this.valueExpr = exprList.getExpr(0);
			this.withTExpr = exprList.getExpr(1);
		}
	}
	
	public void exec(Env env, Scope scope, Writer writer) {
		if (paraNum == 1) {
			outputDatetime(env, scope, writer,false);
		}else if (paraNum == 2) {
			outputDatetime(env, scope, writer,true);
		} else {
			outputToday(env, writer);
		}
	}
	
	private void outputToday(Env env, Writer writer) {
		try {
			writer.write( new Date(), env.getEngineConfig().getDatePattern());
		} catch (IOException e) {
			throw new TemplateException(e.getMessage(), location, e);
		}
	}
	
	private void outputDatetime(Env env, Scope scope, Writer writer,boolean withT) {
		Object value = valueExpr.eval(scope);
		if(value!=null){
			String datetime=null;
			if(withT){
				Object withTValue = withTExpr.eval(scope);
				if(withTValue!=null&&withTValue.toString().trim().equals("true")){
					datetime=JBoltTimestampConverter.doConvertInputString(value.toString());
				}else{
					datetime=JBoltTimestampConverter.doConvertShowString(value.toString());
				}
			}else{
				datetime=JBoltTimestampConverter.doConvertShowString(value.toString());
			}
			try {
				writer.write(datetime==null?"":datetime);
			} catch (IOException e) {
				throw new TemplateException(e.getMessage(), location, e);
			}
		}
		
	}
	 
	
	 
}
