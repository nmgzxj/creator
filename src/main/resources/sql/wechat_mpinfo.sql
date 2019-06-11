### 后台管理分页多条件查询
#sql("paginateAdminList")
select * from #(table) where 1=1 
#if(enable!=null)
 and enable=#(enable)
#end
#if(isAuthenticated!=null)
 and isAuthenticated=#(isAuthenticated)
#end
#if(type!=null)
 and type=#(type)
#end
#if(subjectType!=null)
 and subjectType=#(subjectType)
#end
#if(keywords??)
 and ((name like '%#(keywords)%') or (wechatId like '%#(keywords)%'))
#end
 order by id Desc
#end
