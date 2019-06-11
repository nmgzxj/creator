package cn.jbolt.common.directive;

import java.io.IOException;

import com.jfinal.template.Directive;
import com.jfinal.template.Env;
import com.jfinal.template.expr.ast.Expr;
import com.jfinal.template.expr.ast.ExprList;
import com.jfinal.template.io.Writer;
import com.jfinal.template.stat.ParseException;
import com.jfinal.template.stat.Scope;

public class SqlValueDirective extends Directive {
	
	private Expr valueExpr;
	private int paraNum;
	
	public void setExprList(ExprList exprList) {
		this.paraNum = exprList.length();
		if (paraNum ==0) {
			throw new ParseException("Wrong number parameter of #sqlValue directive, one parameters allowed at most", location);
		}else
		
			if (paraNum == 1) {
				this.valueExpr  = exprList.getExpr(0);
			} 
	}
	
	public void exec(Env env, Scope scope, Writer writer) {
		if (paraNum == 0) {
			outputNothing(env, writer);
		} else if (paraNum == 1) {
			outputSqlValue(env, scope, writer);
		}
	}
	/**
	 * 输出空字符
	 * @param env
	 * @param writer
	 */
	private void outputNothing(Env env, Writer writer) {
		try {
			writer.write("''");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	/**
	 * 输出处理后的value值
	 * @param env
	 * @param scope
	 * @param writer
	 */
	private void outputSqlValue(Env env,Scope scope, Writer writer) {
		Object value=this.valueExpr.eval(scope);
		if(value!=null&&value.toString().length()>0){
			try {
				String sqlValue=value.toString();
				boolean isLike=sqlValue.indexOf("like")!=-1;
				if(isLike){
					writer.write(sqlValue);
				}else{
					if(sqlValue.equals("true")){
						writer.write(true);
					}else if(sqlValue.equals("false")){
						writer.write(false);
					}else{
						writer.write("'");
						writer.write(sqlValue);
						writer.write("'");
					}
				}
				
				
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
	}
	
	
	
	
}
