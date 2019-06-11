### 验证是否拥有某个 permission
#sql("hasPermission")
	select * from role_permission where permissionId=#(permissionId) 
	and (
		#for (x : roleArray)
			#(for.first ? "" : "or") roleId = #(x)
		#end
	)
#end
