### 后台管理分页多条件查询
#sql("paginateAdminList")
select * from #(table) where 1=1 
#if(onSale!=null)
 and onSale=#(onSale)
#end
#if(isHot!=null)
 and isHot=#(isHot)
#end
#if(isDelete!=null)
 and isDelete=#(isDelete)
#end
#if(isRecommend!=null)
 and isRecommend=#(isRecommend)
#end
#if(bcategoryId!=null&&bcategoryId>0)
 and (bcategoryId=#(bcategoryId) or  ( concat('_',bcategoryKey,'_')  regexp concat('_(',replace(#sqlValue(bcategoryId),'_','|'),')_') ))
#end
#if(fcategoryId!=null&&fcategoryId>0)
 and (fcategoryId=#(fcategoryId) or  ( concat('_',fcategoryKey,'_')  regexp concat('_(',replace(#sqlValue(fcategoryId),'_','|'),')_') ))
#end
#if(keywords??)
 and ((name like '%#(keywords)%') or (subTitle like '%#(keywords)%'))
#end
 order by updateTime Desc
#end

###检测商品分类是否已经被商品使用
#sql("checkGoodsBackCategoryInUse")
select id from #(table) where (bcategoryId=#(bcategoryId) or  ( concat('_',bcategoryKey,'_')  regexp concat('_(',replace(#sqlValue(bcategoryId),'_','|'),')_') ))
#end