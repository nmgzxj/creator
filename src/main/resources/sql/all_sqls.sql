#define order()
#if(orderColumns?? && orderTypes??)
 #for(col:orderColumns)
 #(for.first?" order by ":"")#(col) #(orderTypes[for.index])#(for.last?"":",")
 #end
#end
#end

#define where()
 where 1=1
#if(myparas)
#for(myp:myparas)
#((or??)?((for.index==0)?' and (':' or '):' and ') #(myp.key) #(customCompare?"":"=")  #sqlValue(myp.value) #((for.index==for.size-1)?((or??)?")":""):"")
#end
#end
#@order()
#end

#namespace("common")
#include("common.sql")
#end

#namespace("user.auth")
#include("user_auth.sql")
#end

#namespace("mall.goods")
#include("mall_goods.sql")
#end

#namespace("wechat.mpinfo")
#include("wechat_mpinfo.sql")
#end

